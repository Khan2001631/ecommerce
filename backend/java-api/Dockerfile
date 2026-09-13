# Stage 1: Build the JAR
FROM maven:3.9.9-eclipse-temurin-21-alpine AS builder
WORKDIR /app
# Copy the pom.xml and source code
COPY pom.xml .
COPY src ./src
# Build the application (skipping tests since GitHub Actions runs them)
RUN mvn clean package -DskipTests

# Stage 2: Run the Application
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
# Copy the compiled JAR from the builder stage
COPY --from=builder /app/target/*.jar app.jar
# Expose the port the Spring Boot app runs on
EXPOSE 8080
# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]
