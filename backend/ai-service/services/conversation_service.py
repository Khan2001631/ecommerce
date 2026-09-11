"""
Conversation Service
====================
Centralises all HTTP communication with the Spring Boot AI conversation APIs.

Every public method is **fail-safe**: on any network or server error it logs the
problem and returns a neutral fallback (``None``, ``[]``, or ``False``).  The
caller can therefore treat conversation persistence as *best-effort* without
ever crashing the agent loop.
"""

import json
import logging
from typing import Any, Dict, List, Optional

import requests

from config.settings import settings

logger = logging.getLogger(__name__)

BASE_URL = str(settings.SPRING_BOOT_API_URL).rstrip("/")
CONVERSATION_API = f"{BASE_URL}/ai/conversations"

# Default timeout for all outgoing requests (connect, read) in seconds.
_TIMEOUT = (5, 10)


# ---------------------------------------------------------------------------
# Helpers
# ---------------------------------------------------------------------------

def _headers(token: Optional[str] = None) -> Dict[str, str]:
    """Build common request headers, optionally including auth."""
    headers: Dict[str, str] = {"Content-Type": "application/json"}
    if token:
        headers["Cookie"] = f"accessToken={token}"
    return headers


# ---------------------------------------------------------------------------
# Public API
# ---------------------------------------------------------------------------

def create_conversation(
    user_id: int,
    token: Optional[str] = None,
) -> Optional[Dict[str, Any]]:
    """
    POST /ai/conversations?userId={user_id}

    Returns ``{"id": <int>, "sessionId": <str>}`` on success, or ``None``.
    """
    logger.info("Calling create_conversation: URL=%s, userId=%s", CONVERSATION_API, user_id)
    try:
        resp = requests.post(
            CONVERSATION_API,
            params={"userId": user_id},
            headers=_headers(token),
            timeout=_TIMEOUT,
        )
        logger.info("create_conversation response: status=%s", resp.status_code)
        if resp.status_code == 201:
            return resp.json()

        logger.warning(
            "create_conversation failed — status %s: %s",
            resp.status_code, resp.text,
        )
    except Exception:
        logger.exception("create_conversation error")

    return None


def get_conversation_by_session(
    session_id: str,
    token: Optional[str] = None,
) -> Optional[Dict[str, Any]]:
    """
    GET /ai/conversations/session/{sessionId}

    Returns ``{"id": <int>, "sessionId": <str>}`` on success, or ``None``.
    """
    url = f"{CONVERSATION_API}/session/{session_id}"
    logger.info("Calling get_conversation_by_session: URL=%s", url)
    try:
        resp = requests.get(
            url,
            headers=_headers(token),
            timeout=_TIMEOUT,
        )
        logger.info("get_conversation_by_session response: status=%s", resp.status_code)
        if resp.status_code == 200:
            return resp.json()

        if resp.status_code != 404:
            logger.warning(
                "get_conversation_by_session(%s) — status %s: %s",
                session_id, resp.status_code, resp.text,
            )
    except Exception:
        logger.exception("get_conversation_by_session error")

    return None


def get_conversation_history(
    conversation_id: int,
    token: Optional[str] = None,
    limit: Optional[int] = None,
) -> List[Dict[str, Any]]:
    """
    GET /ai/conversations/{conversationId}/messages

    Returns the most recent *limit* messages in chronological order,
    formatted for LLM consumption: ``[{"role": ..., "content": ...}, ...]``.

    If *limit* is ``None`` the setting ``MAX_HISTORY_MESSAGES`` is used.
    """
    if limit is None:
        limit = settings.MAX_HISTORY_MESSAGES

    url = f"{CONVERSATION_API}/{conversation_id}/messages"
    logger.info("Calling get_conversation_history: URL=%s, limit=%d", url, limit)
    try:
        resp = requests.get(
            url,
            headers=_headers(token),
            timeout=_TIMEOUT,
        )
        logger.info("get_conversation_history response: status=%s", resp.status_code)
        if resp.status_code == 200:
            raw_messages: List[Dict[str, Any]] = resp.json()

            # The API returns messages ordered by createdAt (oldest first).
            # We only keep the most recent `limit` messages.
            recent = raw_messages[-limit:] if len(raw_messages) > limit else raw_messages

            # Convert to LLM-compatible format (strip DB-only fields).
            return [
                {"role": msg["role"], "content": msg["content"]}
                for msg in recent
            ]

        logger.warning(
            "get_conversation_history(%s) — status %s: %s",
            conversation_id, resp.status_code, resp.text,
        )
    except Exception:
        logger.exception("get_conversation_history error")

    return []


def save_message(
    conversation_id: int,
    role: str,
    content: str,
    token: Optional[str] = None,
) -> bool:
    """
    POST /ai/conversations/{conversationId}/messages

    Persists a single message (user or assistant).  Returns ``True`` on
    success, ``False`` otherwise.
    """
    try:
        resp = requests.post(
            f"{CONVERSATION_API}/{conversation_id}/messages",
            json={"role": role, "content": content},
            headers=_headers(token),
            timeout=_TIMEOUT,
        )
        if resp.status_code == 201:
            return True

        logger.warning(
            "save_message(%s, %s) — status %s: %s",
            conversation_id, role, resp.status_code, resp.text,
        )
    except Exception:
        logger.exception("save_message error")

    return False


def save_tool_execution(
    conversation_id: int,
    tool_name: str,
    tool_arguments: str,
    tool_response: str,
    success: bool,
    token: Optional[str] = None,
) -> bool:
    """
    POST /ai/conversations/{conversationId}/tools

    Logs a tool execution record.  Returns ``True`` on success.
    """
    try:
        resp = requests.post(
            f"{CONVERSATION_API}/{conversation_id}/tools",
            json={
                "toolName": tool_name,
                "toolArguments": tool_arguments,
                "toolResponse": tool_response,
                "success": success,
            },
            headers=_headers(token),
            timeout=_TIMEOUT,
        )
        if resp.status_code == 201:
            return True

        logger.warning(
            "save_tool_execution(%s, %s) — status %s: %s",
            conversation_id, tool_name, resp.status_code, resp.text,
        )
    except Exception:
        logger.exception("save_tool_execution error")

    return False
