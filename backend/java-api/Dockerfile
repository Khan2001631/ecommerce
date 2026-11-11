# syntax=docker/dockerfile:1
# Stage 1: Build the Spring Boot application
FROM maven:3.9.9-eclipse-temurin-21-alpine AS builder

WORKDIR /app

# Copy Maven configuration first
COPY pom.xml .

# Copy source code
COPY src ./src

# Build using BuildKit cache for Maven dependencies
RUN --mount=type=cache,target=/root/.m2 \
    mvn package -DskipTests

# Stage 2: Run the application
FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

# Copy the compiled JAR from the builder stage
COPY --from=builder /app/target/*.jar app.jar

# Spring Boot application port
EXPOSE 8080

# Start the application
ENTRYPOINT ["java", "-jar", "app.jar"]