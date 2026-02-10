# Multi-stage build for Spring Boot application

# Stage 1: Build the application
FROM maven:3.9.6-eclipse-temurin-17-alpine AS build

# Install git
RUN apk add --no-cache git

# Set working directory
WORKDIR /app

# Clone the repository from GitHub
RUN git clone https://github.com/yashkumardubey/employee.git .

# Update credentials in application.properties
RUN sed -i 's/AUTH_USERNAME:dipali/AUTH_USERNAME:yash/g' src/main/resources/application.properties

# Build the application (skip tests for faster build, run them separately if needed)
RUN mvn clean package -DskipTests

# Stage 2: Run the application
FROM eclipse-temurin:17-jre-alpine

# Set working directory
WORKDIR /app

# Create a non-root user for security
RUN addgroup -S spring && adduser -S spring -G spring

# Copy the built JAR from the build stage
# Note: If your pom.xml creates a WAR, you'll need to change this to embedded tomcat
COPY --from=build /app/target/*.war app.war

# Change to non-root user
USER spring:spring

# Expose the application port
EXPOSE 8080

# Set JVM options for containerized environment
ENV JAVA_OPTS="-Xmx512m -Xms256m"

# Health check
HEALTHCHECK --interval=30s --timeout=3s --start-period=40s --retries=3 \
  CMD wget --no-verbose --tries=1 --spider http://localhost:8080/api || exit 1

# Run the application
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -Djava.security.egd=file:/dev/./urandom -jar app.war"]
