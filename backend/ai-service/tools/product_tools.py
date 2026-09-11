import requests
from typing import Any, Dict, Optional
from config.settings import settings

BASE_API_URL = settings.SPRING_BOOT_API_URL


def search_products(query: str, token: Optional[str] = None) -> Dict[str, Any]:
    try:
        cleaned_query = query.strip()
        if not cleaned_query:
            return {"success": False, "error": "Empty product search query"}

        url = f"{BASE_API_URL}/api/products/search"
        headers: Dict[str, str] = {}

        if token:
            headers["Authorization"] = f"Bearer {token}"

        params = {"query": cleaned_query}

        response = requests.get(url, headers=headers, params=params)

        if response.status_code != 200:
            return {
                "success": False,
                "error": f"Status: {response.status_code}, Response: {response.text}"
            }

        products = response.json()

        return {
            "success": True,
            "data": products
        }

    except Exception as e:
        return {
            "success": False,
            "error": str(e)
        }