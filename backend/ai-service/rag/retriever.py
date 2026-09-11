from typing import List
from langchain_huggingface import HuggingFaceEmbeddings
from langchain_community.vectorstores import FAISS

# Step 1: Load embeddings (same as ingest)
embeddings = HuggingFaceEmbeddings(
    model_name="all-MiniLM-L6-v2"
)

# Step 2: Load FAISS index
vectorstore = FAISS.load_local("vectorstore", embeddings, allow_dangerous_deserialization=True)


def get_relevant_chunks(query: str, k: int = 3) -> List[str]:
    results = vectorstore.similarity_search(query, k=k)

    chunks: List[str] = []
    for doc in results:
        chunks.append(doc.page_content)

    return chunks