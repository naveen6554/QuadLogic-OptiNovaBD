# Multi-stage Dockerfile for Spring Boot 3.x (Java 21) on Railway

# Stage 1: Build JAR using Maven
FROM maven:3.9.6-eclipse-temurin-21-alpine AS build
WORKDIR /app

# Copy pom.xml and cache dependencies
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copy source code and build application JAR
COPY src ./src
RUN mvn clean package -DskipTests

# Stage 2: Production Runtime image using Eclipse Temurin JRE 21
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

# Copy compiled JAR artifact from build stage
COPY --from=build /app/target/*.jar app.jar

# Dynamic PORT binding handled by Spring Boot server.port=${PORT:8080} in application.properties
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
