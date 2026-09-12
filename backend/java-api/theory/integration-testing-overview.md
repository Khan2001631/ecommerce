# Integration Testing Overview

## Why Do We Need Integration Testing?
While **Unit Tests** are excellent for testing individual pieces of logic in isolation (using mocks), they have a blind spot: they cannot verify if the pieces actually work together. 

Integration tests fill this gap by testing the "wiring" of your application. They catch real-world bugs that unit tests miss, such as:
- **Database constraints:** (e.g., trying to save a User without a `role` when the database requires it).
- **Security & Filters:** (e.g., `JwtAuthFilter` failing to read a cookie, or CORS blocking a request).
- **JSON Serialization:** (e.g., Jackson failing to convert a Java object into JSON because of a missing constructor).
- **Spring Configurations:** (e.g., ensuring your application context actually boots up without crashing).

---

## What is `UserIntegrationTest.java` About?
Instead of mocking the database or services, `UserIntegrationTest` spins up the **entire Spring Boot application** using a temporary, in-memory database (H2). 

It uses a tool called `MockMvc` to simulate a real web browser or frontend client making HTTP requests. The test executes a complete, end-to-end user journey:

1. **Register:** Sends a `POST /users/register` request and checks the database to ensure the user row was saved with a hashed password.
2. **Login:** Sends a `POST /users/login` request, verifies that `accessToken` and `refreshToken` cookies are returned, and checks that an active session was saved in the DB.
3. **Access Protected Route:** Sends a `GET /users` request *with* the cookie to prove the security filter accepts it, and sends one *without* the cookie to prove it gets rejected (`403 Forbidden`).
4. **Logout:** Sends a `POST /users/logout` request, verifies the cookies are wiped (`Max-Age=0`), and checks the database to ensure the session is marked as `REVOKED`.

---

## How Does This Help Us?
- **Confidence in Deployments:** You know your database, security filters, controllers, and services actually communicate correctly.
- **Automated CI/CD:** When you push code to GitHub, this test runs automatically. You don't need a live MySQL server to run it.
- **Zero Manual Verification:** You no longer need to open Postman and manually click through the registration and login flow every time you change a line of code.

---

## Key Files Involved

### 1. `pom.xml`
We added the **H2 Database** dependency under the `<scope>test</scope>`. This allows Spring to use a lightweight, RAM-based database strictly during the test phase.

### 2. `application-test.properties`
Located in `src/test/resources/`. This configuration file overrides your normal settings specifically for tests. 
- It tells Spring to use the `jdbc:h2:mem` database URL instead of MySQL.
- It enables MySQL compatibility mode so your standard JPA queries work seamlessly.

### 3. `UserIntegrationTest.java`
The core test file. 
- `@SpringBootTest`: Tells Spring to boot up the entire application.
- `@AutoConfigureMockMvc`: Gives us the `mockMvc` object to send fake HTTP requests.
- `@ActiveProfiles("test")`: Tells Spring to load the settings from `application-test.properties`.
- `@BeforeEach`: Erases the database before every run to ensure a clean slate.
