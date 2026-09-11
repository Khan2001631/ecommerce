from typing import Any, Dict, Optional
from fastapi import APIRouter, Header, Cookie
from models.schemas import ChatRequest
from agents.shopping_agent import handle_query

router = APIRouter()

@router.post("/chat")
def chat(request: ChatRequest, accessToken: Optional[str] = Cookie(None)) -> Dict[str, Any]:
    try:
        return handle_query(
            request.question,
            accessToken,
            session_id=request.session_id,
            user_id=request.user_id,
        )
    except Exception as e:
        return {"error": str(e)}


# Comments:
# This defines a FastAPI POST endpoint /chat using a router.
# It accepts a request body (ChatRequest) and optionally reads the Authorization header from incoming HTTP requests.
# The authorization parameter allows you to pass authentication tokens (e.g., Bearer tokens) into your business logic.
# The function forwards the user's question, auth header, session_id, and user_id to handle_query,
# enabling authenticated, personalized, and multi-turn processing.
# -> Dict[str, Any] is a type hint indicating the response will be a dictionary with string keys and flexible value types.
# Errors are caught and returned as a simple JSON response instead of crashing the API