import os
from typing import Any, Dict, List

BASE_DIR = os.path.dirname(os.path.dirname(os.path.abspath(__file__)))
DATA_PATH = os.path.join(BASE_DIR, "data")

def load_documents() -> List[Dict[str, Any]]:
    documents: List[Dict[str, Any]] = []

    for filename in os.listdir(DATA_PATH):
        if filename.endswith((".txt", ".md")):
            with open(os.path.join(DATA_PATH, filename), "r", encoding="utf-8") as f:
                documents.append({
                    "source": filename,
                    "content": f.read()
                })

    return documents


def chunk_text(text: str, chunk_size: int = 500, overlap: int = 100) -> List[str]:
    chunks: List[str] = []
    start = 0

    while start < len(text):
        end = start + chunk_size
        chunks.append(text[start:end])
        start += chunk_size - overlap

    return chunks


if __name__ == "__main__":
    docs = load_documents()

    all_chunks = []
    for doc in docs:
        chunks = chunk_text(doc["content"])
        for chunk in chunks:
            all_chunks.append({
                "source": doc["source"],
                "chunk": chunk
            })

    print(f"Total chunks: {len(all_chunks)}")
    print("\nSample chunk:\n", all_chunks[0]["chunk"])