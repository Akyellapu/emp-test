# Stage 1: Build the application
FROM maven:3.9.6-eclipse-temurin-17 AS builder
 
# Set working directory
WORKDIR /app
 
# Copy all files
COPY . .
 
# Build the application
RUN mvn clean package -DskipTests
 
# Stage 2: Run the application
FROM eclipse-temurin:17-jdk-alpine
 
# Set working directory
WORKDIR /app
 
# Copy the JAR from the builder image
COPY --from=builder /app/target/*.jar app.jar
 
# Expose the port used by Spring Boot
EXPOSE 8080
 
# Use dynamic port on Render
ENV PORT=8080

ENV JAVA_OPTS="-Xms512m -Xmx2048m -XX:+UseG1GC -XX:MaxGCPauseMillis=200"
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
 
# Health check to ensure the application is running