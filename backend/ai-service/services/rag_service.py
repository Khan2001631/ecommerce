from typing import List
from rag.retriever import get_relevant_chunks

def get_context(question: str) -> str:
    chunks: List[str] = get_relevant_chunks(question)
    return "\n\n".join(chunks)