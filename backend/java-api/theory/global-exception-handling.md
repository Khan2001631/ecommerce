# Global Exception Handling in Spring Boot

## What is it?
Global Exception Handling is a centralized mechanism to intercept and handle all errors (exceptions) thrown by your application before they reach the user. In Spring Boot, this is achieved using the `@RestControllerAdvice` annotation.

## Why do we need it?
Without global exception handling, Spring Boot returns a generic "White-label Error Page" or messy stack traces when something goes wrong. This causes two major issues:
1. **Security Risk:** Stack traces often reveal sensitive database structures, SQL queries, or file paths to the frontend user.
2. **Poor Debugging:** If the server swallows errors, developers have no logs to figure out what broke.

`@RestControllerAdvice` solves both:
- **Sanitization:** It catches the error and sends a safe, clean JSON response (e.g., `404 Not Found`) to the frontend.
- **Traceability:** It allows you to securely log the full, ugly stack trace into the server logs via `SLF4J` so you know exactly where to debug.

## How it works in our project

1. **Custom Exceptions:** We create custom exceptions like `UserAlreadyExistsException` and `ResourceNotFoundException`. These inherit from Java's `RuntimeException`.
2. **`super(message)`:** Inside our custom exceptions, we call `super(message)`. This passes our custom error string up to the parent `RuntimeException` class so it can store the message securely. Without it, `ex.getMessage()` would return `null`.
3. **The Global Interceptor:** The `GlobalExceptionHandler` class is annotated with `@RestControllerAdvice`. It contains methods annotated with `@ExceptionHandler`.
4. **Automatic Routing:** When `UserService` throws a `UserAlreadyExistsException`, Java immediately stops executing the normal flow. It exits the Controller and the exception is caught by the `@ExceptionHandler` matching that specific exception type.

## Why this makes our Code Cleaner
Because exceptions break the normal execution flow, our Controllers no longer need to manually check for errors. 

**Before (Manual Error Checking):**
```java
String message = userService.registerUser(user);
if (message.equals("User already exists")) {
    return ResponseEntity.status(409).body(message); // Clutters the controller
}
return ResponseEntity.status(201).body("Success");
```

**After (Exception Driven):**
```java
// Throws exception and exits immediately if it fails!
userService.registerUser(user); 

// If we reach this line, we know 100% it was successful.
return ResponseEntity.status(201).body("Success"); 
```
The controller is now extremely lean and only focuses on the "happy path."
