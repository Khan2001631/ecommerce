import requests
from typing import Any, Dict, Optional
from config.settings import settings

BASE_API_URL = settings.SPRING_BOOT_API_URL


def add_to_cart(product_id: int, quantity: int, token: Optional[str] = None) -> Dict[str, Any]:
    if not token or not token.strip():
        return {"success": False, "error": "Missing authorization token"}

    try:
        url = f"{BASE_API_URL}/api/cart/add"
        headers = {
            "Content-Type": "application/json",
            "Cookie": f"accessToken={token.strip()}"
        }

        payload = {"productId": product_id, "quantity": quantity}

        response = requests.post(url, json=payload, headers=headers)

        if response.status_code != 200:
            return {
                "success": False,
                "status_code": response.status_code,
                "error": response.text,
                "headers": dict(response.headers)
            }

        return {
            "success": True,
            "message": "Product added to cart"
        }

    except Exception as e:
        return {
            "success": False,
            "error": str(e)
        }


def remove_from_cart(product_id: int, token: Optional[str] = None) -> Dict[str, Any]:
    if not token or not token.strip():
        return {"success": False, "error": "Missing authorization token"}

    try:
        url = f"{BASE_API_URL}/api/cart/remove"
        headers = {
            "Content-Type": "application/json",
            "Cookie": f"accessToken={token.strip()}"
        }

        payload = {"productId": product_id}

        response = requests.delete(url, json=payload, headers=headers)

        if response.status_code != 200:
            return {
                "success": False,
                "status_code": response.status_code,
                "error": response.text,
                "headers": dict(response.headers)
            }

        return {
            "success": True,
            "message": "Product removed from cart"
        }

    except Exception as e:
        return {
            "success": False,
            "error": str(e)
        }
        
        
# **Comments:**

# * This function is a **tool layer utility** that communicates with your Spring Boot backend to add items to a user’s cart.
# * It depends on `settings.SPRING_BOOT_API_URL`, keeping the base API configurable via environment variables.
# * The function requires an **authorization token**; it validates early and fails fast if the token is missing or empty.
# * It constructs a POST request to `/api/cart/add`, sending `productId` and `quantity` as JSON payload.
# * The `Authorization` header uses the **Bearer token pattern**, enabling authenticated backend operations.
# * Uses the `requests` library for making HTTP calls in a synchronous manner.
# * If the API response is not `200`, it returns a detailed error object including:

#   * HTTP status code
#   * raw response text
#   * response headers (useful for debugging backend issues)
# * On success, it returns a simple standardized success response.
# * Wrapped in a `try-except` block to handle network failures, timeouts, or unexpected errors gracefully.
# * Return structure is consistent (`success: True/False`), making it easy for higher-level logic (`handle_query`) to interpret results.
# * Overall, this isolates **external API interaction**, keeping your business logic clean and modular.
