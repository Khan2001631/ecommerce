from langchain_huggingface import HuggingFaceEmbeddings
from langchain_community.vectorstores import FAISS
from rag.loader import load_documents, chunk_text
import os
from dotenv import load_dotenv

load_dotenv()

# Step 1: Load docs
docs = load_documents()

# Step 2: Chunk docs
texts = []
metadatas = []

for doc in docs:
    chunks = chunk_text(doc["content"])
    for chunk in chunks:
        texts.append(chunk)
        metadatas.append({"source": doc["source"]})

# Step 3: Create embeddings
embeddings = HuggingFaceEmbeddings(
    model_name="all-MiniLM-L6-v2"
)

# Step 4: Create FAISS index
vectorstore = FAISS.from_texts(
    texts=texts,
    embedding=embeddings,
    metadatas=metadatas
)

# Step 5: Save locally
vectorstore.save_local("vectorstore")

print("✅ FAISS index created and saved.")