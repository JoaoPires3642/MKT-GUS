# Build stage
FROM maven:3.9-eclipse-temurin-21-alpine AS build

WORKDIR /app

# Copy parent pom and all module poms first for better layer caching
COPY pom.xml .
COPY domain/pom.xml domain/
COPY application/pom.xml application/
COPY infra-utils/pom.xml infra-utils/
COPY infra-data/pom.xml infra-data/
COPY web.ui/pom.xml web.ui/
COPY app/pom.xml app/

# Download dependencies
RUN mvn dependency:go-offline -B

# Copy source code
COPY domain/src domain/src
COPY application/src application/src
COPY infra-utils/src infra-utils/src
COPY infra-data/src infra-data/src
COPY web.ui/src web.ui/src
COPY app/src app/src

# Build the application (install dependencies first)
RUN mvn clean install -DskipTests -DskipITs

# Runtime stage
FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

RUN apk add --no-cache wget

# Create non-root user
RUN addgroup -S spring && adduser -S spring -G spring

# Copy the built jar from build stage
COPY --from=build /app/app/target/autoatendimento-app-*.jar app.jar

# Change ownership to non-root user
RUN chown -R spring:spring /app

USER spring

# Expose port
EXPOSE 8080

# Health check
HEALTHCHECK --interval=30s --timeout=3s --start-period=5s --retries=3 \
  CMD wget -qO- http://localhost:8080/actuator/health || exit 1

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]
