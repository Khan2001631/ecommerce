import json
import logging
import re
from typing import Any, Dict, List, Optional

from config.prompts import RAG_RESPONSE_PROMPT, SYSTEM_PROMPT
from config.settings import settings
from services.llm_service import chat_with_tools, generate_response
from services.rag_service import get_context
from services import conversation_service
from tools.cart_tools import add_to_cart, remove_from_cart
from tools.product_tools import search_products

logger = logging.getLogger(__name__)


# ---------------------------------------------------------------------------
# Tool schemas (OpenAI-compatible format, supported by Ollama)
# ---------------------------------------------------------------------------

TOOLS: List[Dict[str, Any]] = [
    {
        "type": "function",
        "function": {
            "name": "search_products",
            "description": (
                "Search for products in the store by name or keyword. "
                "Always call this before add_to_cart to obtain a product ID."
            ),
            "parameters": {
                "type": "object",
                "properties": {
                    "query": {
                        "type": "string",
                        "description": "Product name or search keyword"
                    }
                },
                "required": ["query"]
            }
        }
    },
    {
        "type": "function",
        "function": {
            "name": "add_to_cart",
            "description": (
                "Add a product to the user's cart. "
                "Requires a valid product_id (obtained from search_products) and quantity."
            ),
            "parameters": {
                "type": "object",
                "properties": {
                    "product_id": {
                        "type": "integer",
                        "description": "The numeric product ID returned by search_products"
                    },
                    "quantity": {
                        "type": "integer",
                        "description": "Number of items to add (minimum 1)"
                    }
                },
                "required": ["product_id", "quantity"]
            }
        }
    },
    {
        "type": "function",
        "function": {
            "name": "remove_from_cart",
            "description": (
                "Remove a product from the user's cart. "
                "Requires a valid product_id (obtained from search_products)."
            ),
            "parameters": {
                "type": "object",
                "properties": {
                    "product_id": {
                        "type": "integer",
                        "description": "The numeric product ID returned by search_products"
                    }
                },
                "required": ["product_id"]
            }
        }
    },
    {
        "type": "function",
        "function": {
            "name": "search_knowledge_base",
            "description": (
                "Search store policies, FAQs, return policy, shipping information, "
                "and general store questions. Use this for any non-product question."
            ),
            "parameters": {
                "type": "object",
                "properties": {
                    "query": {
                        "type": "string",
                        "description": "The question or topic to look up"
                    }
                },
                "required": ["query"]
            }
        }
    }
]


# ---------------------------------------------------------------------------
# Input validation
# ---------------------------------------------------------------------------

def sanitize_query(query: str) -> str:
    if not isinstance(query, str):
        raise ValueError("Query must be a string")

    cleaned = " ".join(query.strip().split())
    cleaned = re.sub(r"[\x00-\x1f\x7f-\x9f]", "", cleaned)

    if len(cleaned) < settings.MIN_QUERY_LENGTH:
        raise ValueError("Query is too short")
    if len(cleaned) > settings.MAX_QUERY_LENGTH:
        raise ValueError("Query is too long")

    return cleaned


# ---------------------------------------------------------------------------
# Tool executor — maps LLM tool call names to real Python functions
# ---------------------------------------------------------------------------

def execute_tool(
    tool_name: str,
    tool_args: Dict[str, Any],
    token: Optional[str]
) -> str:
    """
    Dispatch a tool call requested by the LLM and return a string result.
    The result is fed back into the conversation as a 'tool' role message.
    """
    if tool_name == "search_products":
        query = tool_args.get("query", "")
        result = search_products(query, token)
        if not result["success"]:
            return json.dumps({"error": result.get("error", "Search failed")})
        products = result["data"]
        if not products:
            return json.dumps({"error": f"No products found for '{query}'"})
        # Return the top 3 results so the LLM can pick the best match
        summarized = [
            {"id": p["id"], "name": p["name"], "price": p.get("price")}
            for p in products[:3]
        ]
        return json.dumps({"products": summarized})

    elif tool_name == "add_to_cart":
        product_id = tool_args.get("product_id")
        quantity = tool_args.get("quantity", 1)

        # Enforce quantity bounds
        try:
            quantity = max(1, min(int(quantity), settings.MAX_CART_QUANTITY))
        except (TypeError, ValueError):
            quantity = 1

        if not product_id:
            return json.dumps({"error": "product_id is required"})

        result = add_to_cart(int(product_id), quantity, token)
        if result["success"]:
            return json.dumps({"success": True, "message": "Product added to cart"})
        return json.dumps({"error": result.get("error", "Failed to add to cart")})

    elif tool_name == "remove_from_cart":
        product_id = tool_args.get("product_id")
        
        if not product_id:
            return json.dumps({"error": "product_id is required"})
            
        result = remove_from_cart(int(product_id), token)
        if result["success"]:
            return json.dumps({"success": True, "message": "Product removed from cart"})
        return json.dumps({"error": result.get("error", "Failed to remove from cart")})

    elif tool_name == "search_knowledge_base":
        query = tool_args.get("query", "")
        context = get_context(query)
        prompt = RAG_RESPONSE_PROMPT.format(context=context, query=query)
        answer = generate_response(prompt)
        return json.dumps({"answer": answer})

    else:
        return json.dumps({"error": f"Unknown tool: {tool_name}"})


# ---------------------------------------------------------------------------
# Conversation resolution helpers
# ---------------------------------------------------------------------------

def _resolve_conversation(
    session_id: Optional[str],
    user_id: Optional[int],
    token: Optional[str],
) -> tuple:
    """
    Resolve or create a conversation via the Spring Boot APIs.

    Returns:
        (conversation_id, session_id)  — both may be ``None`` when the
        conversation APIs are unavailable or no identifiers were supplied
        (stateless fallback).
    """
    # Case 1: Existing session — look it up
    if session_id:
        conv = conversation_service.get_conversation_by_session(session_id, token)
        if conv:
            return conv["conversationId"], conv["sessionId"]
        # Lookup failed — log and fall through to stateless
        logger.warning("Session %s not found — falling back to stateless", session_id)
        return None, None

    # Case 2: New conversation — create one if we have a userId
    if user_id:
        conv = conversation_service.create_conversation(user_id, token)
        if conv:
            return conv["conversationId"], conv["sessionId"]
        logger.warning("Failed to create conversation for user %s", user_id)

    # Case 3: No identifiers — fully stateless
    return None, None


def _build_messages_with_history(
    conversation_id: Optional[int],
    current_query: str,
    token: Optional[str],
) -> List[Dict[str, Any]]:
    """
    Build the LLM message list.

    If a conversation exists, loads recent history from Spring Boot and
    prepends the system prompt.  Otherwise falls back to the original
    two-message list (system + user).
    """
    messages: List[Dict[str, Any]] = [
        {"role": "system", "content": SYSTEM_PROMPT},
    ]

    if conversation_id:
        history = conversation_service.get_conversation_history(
            conversation_id, token
        )
        # History already contains user/assistant pairs in chronological order.
        messages.extend(history)

    # Always append the current user message last.
    messages.append({"role": "user", "content": current_query})

    return messages


# ---------------------------------------------------------------------------
# Main agentic loop
# ---------------------------------------------------------------------------

MAX_TOOL_ROUNDS = 5  # Guard against infinite loops


def handle_query(
    query: str,
    token: Optional[str] = None,
    session_id: Optional[str] = None,
    user_id: Optional[int] = None,
) -> Dict[str, Any]:
    """
    Main entry point.

    Runs an agentic tool-calling loop:
      1. Resolve / create a conversation (Spring Boot).
      2. Load history and build message list.
      3. Save the user message.
      4. Send user message + tool schemas to the LLM.
      5. If the LLM returns tool_calls → execute each tool, log execution,
         append results, repeat.
      6. When the LLM returns plain text → save assistant message,
         return the final answer.
    """
    try:
        cleaned_query = sanitize_query(query)
    except ValueError as e:
        return {"type": "error", "message": str(e)}

    # ── 1. Resolve conversation ──────────────────────────────────────────
    conversation_id, resolved_session_id = _resolve_conversation(
        session_id, user_id, token
    )

    # ── 2. Build messages (system + history + current user message) ──────
    messages = _build_messages_with_history(
        conversation_id, cleaned_query, token
    )

    # ── 3. Persist the user message ─────────────────────────────────────
    if conversation_id:
        conversation_service.save_message(
            conversation_id, "user", cleaned_query, token
        )

    # ── 4. Agent tool-calling loop (unchanged core logic) ───────────────
    for _ in range(MAX_TOOL_ROUNDS):
        response = chat_with_tools(messages, tools=TOOLS)
        assistant_message = response["message"]

        # Append the assistant's reply (tool calls or text) to history
        messages.append(assistant_message)

        tool_calls = assistant_message.get("tool_calls")

        # No tool calls → LLM produced the final answer
        if not tool_calls:
            final_text = assistant_message.get("content", "").strip()
            if not final_text:
                return {"type": "error", "message": "No response from assistant"}

            # ── 5a. Persist the assistant message ───────────────────────
            if conversation_id:
                conversation_service.save_message(
                    conversation_id, "assistant", final_text, token
                )

            return {
                "type": "response",
                "message": final_text,
                "session_id": resolved_session_id,
                "conversation_id": conversation_id,
            }

        # Execute every tool the LLM requested and feed results back
        for tool_call in tool_calls:
            fn = tool_call.get("function", {})
            tool_name = fn.get("name", "")
            tool_args = fn.get("arguments", {})

            # Ollama may return arguments as a JSON string; normalise to dict
            if isinstance(tool_args, str):
                try:
                    tool_args = json.loads(tool_args)
                except json.JSONDecodeError:
                    tool_args = {}

            tool_result = execute_tool(tool_name, tool_args, token)

            # ── Log tool execution to Spring Boot ───────────────────────
            if conversation_id:
                tool_success = "error" not in tool_result.lower()
                conversation_service.save_tool_execution(
                    conversation_id,
                    tool_name,
                    json.dumps(tool_args),
                    tool_result,
                    tool_success,
                    token,
                )

            messages.append({
                "role": "tool",
                "content": tool_result,
            })

    # Safety fallback: exceeded max rounds
    return {
        "type": "error",
        "message": "The assistant could not complete the request. Please try again.",
        "session_id": resolved_session_id,
        "conversation_id": conversation_id,
    }