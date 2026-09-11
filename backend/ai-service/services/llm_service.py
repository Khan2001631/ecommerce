import logging
import ollama
from typing import Any, Dict, List, Optional
from config.settings import settings

logger = logging.getLogger(__name__)


def generate_response(prompt: str) -> str:
    """Simple single-turn text generation. Used for RAG knowledge base formatting."""
    logger.info("Calling Ollama generate_response with model=%s", settings.OLLAMA_MODEL)
    try:
        response: Dict[str, Any] = ollama.chat(
            model=settings.OLLAMA_MODEL,
            messages=[
                {"role": "user", "content": prompt}
            ]
        )
        logger.info("Ollama generate_response success")
        return response["message"]["content"]
    except Exception as e:
        logger.error("Ollama generate_response failed: %s", str(e), exc_info=True)
        raise


def chat_with_tools(
    messages: List[Dict[str, Any]],
    tools: Optional[List[Dict[str, Any]]] = None
) -> Dict[str, Any]:
    """
    Multi-turn chat with optional tool/function calling support.

    Sends the full message history to the LLM along with tool schemas.
    Returns the raw Ollama response object so the caller can inspect
    `message.tool_calls` and `message.content`.

    Args:
        messages: Full conversation history in Ollama message format.
                  Each entry is {"role": ..., "content": ..., "tool_calls": ..., etc.}
        tools:    List of tool schemas in OpenAI-compatible format.
                  If None, behaves like a plain chat call.

    Returns:
        The raw ollama.chat() response dict.
    """
    logger.info(
        "Calling Ollama chat_with_tools with model=%s, messages_count=%d, has_tools=%s",
        settings.OLLAMA_MODEL, len(messages), bool(tools)
    )
    kwargs: Dict[str, Any] = {
        "model": settings.OLLAMA_MODEL,
        "messages": messages,
    }
    if tools:
        kwargs["tools"] = tools

    try:
        response = ollama.chat(**kwargs)
        logger.info("Ollama chat_with_tools response received: %s", response)
        return response
    except Exception as e:
        logger.error(
            "Ollama chat_with_tools failed. Model: %s. Error: %s",
            settings.OLLAMA_MODEL, str(e), exc_info=True
        )
        raise