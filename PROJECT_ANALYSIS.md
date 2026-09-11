# Complete Architectural and Functional Analysis

## 1. Overall Project

**What is this application?**
This is a modern, full-stack e-commerce platform that goes beyond standard CRUD functionality by integrating an Agentic AI shopping assistant. The system is built using a hybrid microservices-oriented architecture: a Java/Spring Boot backend for core business logic, a Python/FastAPI backend for AI and RAG (Retrieval-Augmented Generation) capabilities, and a React frontend for the user interface.

**What problem does it solve?**
It provides a seamless shopping experience where users can browse products, manage their cart, and checkout. Crucially, it solves the "customer support and discovery" problem by offering an intelligent, conversational AI assistant that can answer policy questions, search for products, and autonomously add items to the user's cart based on natural language commands.

**Who are the users?**
- **Shoppers:** Looking to buy products, ask questions about store policies, or find specific items via the AI agent.
- **Administrators:** (Supported by the database schema) to manage orders and inventory.

**Complete User Journey:**
1. A user lands on the home page and browses products.
2. The user registers or logs in (secured via JWT).
3. The user interacts with the AI Chatbot to ask about return policies (handled by RAG).
4. The user asks the AI to "find me a laptop and add it to my cart." The AI autonomously executes `search_products` and `add_to_cart` tools.
5. The user navigates to the Cart page, reviews the items, and proceeds to checkout.
6. The user completes the payment via Razorpay integration, and the order is persisted.

---

## 2. Functional Features

* **Authentication & Authorization:** 
  * **Registration/Login:** Users can sign up and authenticate.
  * **JWT Flow:** Issues a stateless JSON Web Token upon login, used to authenticate subsequent protected API calls.
* **Product Management:**
  * **Product Browsing:** Users can view a catalog of products retrieved from the database.
* **Cart & Checkout Management:**
  * **Cart:** Users can add, remove, and adjust quantities of items. Managed via Zustand on the frontend and persisted in the backend during checkout.
  * **Orders:** Users can place orders which are linked to their user account.
  * **Payments:** Integration with Razorpay for secure transaction processing.
* **Agentic AI Chatbot:**
  * **Multimodal Voice UI:** Integrates the native Web Speech API to allow users to interact with the agent using voice commands, complete with auto-submission and text-to-speech AI responses.
  * **Conversational UI & Stateful Context:** A dedicated chat interface that preserves multi-turn conversation history across HTTP requests, seamlessly bridging cross-origin cookie authentication.
  * **Tool Calling Workflow:** The AI can autonomously execute backend functions.
  * **search_products:** The AI queries the database for products matching natural language descriptions.
  * **add_to_cart / remove_from_cart:** The AI programmatically modifies the user's shopping cart state upon request.
  * **search_knowledge_base:** A RAG pipeline to answer questions about FAQs, shipping, and return policies.
* **Security & Rate Limiting:**
  * **Custom Rate Limiter:** Protects against brute-force attacks and DDoS by throttling IPs and emails using Leaky Bucket and Token Bucket algorithms.

---

## 3. Backend Flow

The backend is split into two primary services:

### Core API (Java / Spring Boot)
* **Request Flow:** Incoming requests pass through a `RateLimitFilter` (custom implementation), followed by a `JwtAuthFilter`. If authorized, the request reaches the respective Controller (`UserController`, `ProductController`, `CartController`, `AIConversationController`, `OrderController`, `PaymentController`).
* **Database Interactions:** Uses Spring Data JPA and Hibernate to interact with a MySQL database.
* **AI Conversation State:** The Java backend acts as the persistence layer for the AI service. When the Python AI service needs to remember a conversation or log a tool execution, it makes an HTTP call to the Java `AIConversationController`.

### AI Service (Python / FastAPI)
* **Request Flow:** The frontend sends natural language queries to the `/chat` endpoint.
* **Agent Workflow:**
  1. Retrieves conversation history from the Java backend via HTTP.
  2. Constructs a prompt and sends it to the LLM (Ollama).
  3. If the LLM decides to call a tool, the Python service intercepts this, executes the local Python function (`search_products`, `add_to_cart`, `search_knowledge_base`), and feeds the result back to the LLM.
  4. The final conversational response is returned to the frontend and persisted in the Java backend.
* **RAG Pipeline:** Uses `HuggingFaceEmbeddings` and a local `FAISS` vector store to retrieve relevant document chunks for policy questions.

---

## 4. Frontend Flow

* **Tech Stack:** React 18, React Router DOM, Tailwind CSS, Zustand.
* **State Management:** `Zustand` is used for global state (`useAuthStore` for user sessions, `useCartStore` for shopping cart state).
* **Protected Routes:** `AuthGuard` and `NonAuthGuard` components wrap routes to ensure users are authenticated before accessing the Cart, Products, or AI Chat pages.
* **User Experience:** Features a modern dark-mode aesthetic with glassmorphism (`backdrop-blur-xl`, `bg-slate-900/50`), gradients, and responsive layouts. Toast notifications (`react-toastify`) provide immediate feedback for actions like login, adding to cart, or payment success.

---

## 5. AI Components

* **Why AI is being used:** To provide a next-generation shopping experience where users don't have to manually search or navigate. They can command the system using natural language.
* **Agent Workflow:** The AI operates in a multi-turn loop (`chat_with_tools`). It receives the user prompt, system tools (formatted as OpenAI schemas), and the historical context. The LLM determines if a tool should be called. 
* **Tool Calling:**
  * The agent supports `search_products`, `add_to_cart`, `remove_from_cart`, and `search_knowledge_base`.
  * *Example:* If a user says "I want a laptop", the LLM triggers `search_products("laptop")`. The Python code executes the DB lookup and returns JSON to the LLM. The LLM then replies to the user: "I found an ASUS laptop, would you like me to add it to your cart?"
* **Retrieval-Augmented Generation (RAG):** When the `search_knowledge_base` tool is triggered, the `rag_service.py` queries a `FAISS` vector store using `all-MiniLM-L6-v2` HuggingFace embeddings. The retrieved context is injected into a strict `RAG_RESPONSE_PROMPT` to prevent hallucination.
* **Stateful Persistence via Proxy:** The Python service natively extracts HTTP-only cookies and proxies them to the Java backend to fetch and save chat history and tool execution logs, maintaining robust conversational state across cross-origin boundaries.

---

## 6. Security Features

* **JWT (JSON Web Tokens):** Used for stateless, secure session management.
* **Password Hashing:** `BCryptPasswordEncoder` ensures passwords are never stored in plaintext.
* **Custom Rate Limiting (Leaky Bucket & Token Bucket):**
  * Implemented from scratch in `RateLimitFilter.java`.
  * **IP-based Limit:** Throttles excessive requests from a single IP to `/login` and `/register`.
  * **Email-based Limit:** Specifically tracks *failed* login attempts per email address to prevent brute-force dictionary attacks.
  * **Global API Limit:** A Token Bucket algorithm protects all other API endpoints.
* **CORS Protection:** Configured globally in Spring Security to only allow requests from `http://localhost:3000`.
* **Input Sanitization:** The AI service sanitizes user queries (removing control characters and enforcing length bounds) before sending them to the LLM.

---

## 7. Performance Optimizations

* **Stateless Architecture:** Both Java and Python backends are stateless (session state is either in JWT or DB), allowing them to be horizontally scaled independently.
* **Vector Indexing:** Using FAISS for the RAG pipeline ensures that similarity searches across the knowledge base are executed in milliseconds, avoiding full-text scans.
* **Microservices Separation:** By splitting the heavy LLM/AI processing into a separate Python/FastAPI service, the core Java API remains highly responsive for standard e-commerce transactions.
* **Debouncing/Throttling:** The custom rate limiter efficiently sheds load during traffic spikes.

---

## 8. Unique Features

* **Autonomous Tool Execution:** This is not a typical CRUD app. The AI can actively modify the user's state (adding and removing items from the cart). This is highly impressive and mirrors cutting-edge industry trends in Agentic workflows.
* **Custom Algorithmic Security:** The dual-layered rate limiter (IP-based and Email-based failed attempt tracking) is a sophisticated security feature rarely seen in junior/mid-level portfolios.
* **Cross-Language Microservice Communication:** Synchronizing conversational state between a Java backend and a Python AI agent using HTTP APIs and cross-origin cookie proxying shows exceptional maturity in system integration and security design.

---

## 10. Resume-Worthy Contributions

**1. Designed and Integrated an Agentic AI Shopping Assistant**
* **What:** Built a Python/FastAPI service using LangChain and Ollama that interacts with users and executes backend functions.
* **Significance:** Demonstrates advanced AI concepts (Tool Calling, Function Execution, Agentic Loops).
* **Keywords:** Agentic AI, Tool Calling, LangChain, LLM, FastAPI.

**2. Architected a Hybrid Microservices E-Commerce Platform**
* **What:** Decoupled AI processing (Python) from core transactional business logic (Java/Spring Boot).
* **Significance:** Shows system design maturity. Ensures that heavy AI inference does not block high-throughput order processing.
* **Keywords:** Microservices, Spring Boot, System Architecture, REST APIs.

**3. Implemented a Multi-Layered Custom Rate Limiter from Scratch**
* **What:** Built a Servlet Filter in Java utilizing Leaky Bucket and Token Bucket algorithms.
* **Significance:** Protects against DDoS and targeted brute-force attacks by tracking both IP addresses and specific email failure rates.
* **Keywords:** Cyber Security, Token Bucket Algorithm, Leaky Bucket, DDoS Protection, Servlet Filters.

**4. Built a Retrieval-Augmented Generation (RAG) Pipeline**
* **What:** Utilized FAISS and HuggingFace Embeddings to allow the AI to answer complex policy questions without hallucinating.
* **Significance:** Demonstrates practical application of vector databases and semantic search.
* **Keywords:** RAG, Vector Database, FAISS, HuggingFace, Semantic Search.

**5. Developed a Native Multimodal Voice UI for E-Commerce**
* **What:** Integrated the browser's Web Speech API directly into the React frontend to enable Voice Commerce without backend overhead.
* **Significance:** Demonstrates an understanding of modern accessibility standards and multimodal application design, elevating the UX.
* **Keywords:** Web Speech API, Multimodal UI, Speech-to-Text, Voice Commerce, Accessibility.

---