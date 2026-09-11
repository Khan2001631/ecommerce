# AI Service for Ecommerce

This service provides an AI-powered ecommerce assistant built with FastAPI, Ollama, and FAISS-based RAG retrieval.

## Setup

1. Create and activate a Python virtual environment:

```powershell
python -m venv venv
.\venv\Scripts\Activate.ps1
```

2. Install dependencies:

```powershell
pip install -r requirements.txt
```

3. Create a `.env` file in the project root with at least:

```env
OLLAMA_MODEL=<your-ollama-model>
SPRING_BOOT_API_URL=http://localhost:8080
```

## Running the App

From the project root:

```powershell
uvicorn app:app --reload --host 0.0.0.0 --port 8000
```

## API

### POST /chat

Request body:

```json
{
  "question": "Add 2 Nike shoes"
}
```

Response:

- `type: response` for normal answers
- `type: action` for cart actions
- `type: error` when something fails

For cart operations, include an optional `Authorization: Bearer <token>` header.

## Configuration

- `config/settings.py` validates required environment values and exposes typed settings.
- `config/prompts.py` stores prompt templates for intent extraction and RAG response generation.

## Notes

- User input is sanitized before processing.
- Environment settings are validated at startup.
- The cart API integration expects an authorization token if used.
