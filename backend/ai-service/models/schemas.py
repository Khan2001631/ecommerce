from typing import Optional

from pydantic import BaseModel


class ChatRequest(BaseModel):
    question: str
    session_id: Optional[str] = None   # Existing conversation session
    user_id: Optional[int] = None      # Required to create a new conversation