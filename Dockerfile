# Build stage
FROM maven:3.9-eclipse-temurin-21-alpine AS builder

WORKDIR /app
COPY pom.xml .
RUN mvn -B -q dependency:go-offline
COPY src ./src
RUN mvn -B -q -DskipTests package

# Runtime stage
FROM eclipse-temurin:21-jre-alpine

RUN adduser -D -g '' appuser
WORKDIR /app

COPY --from=builder /app/target/url-shortener-1.0.0.jar /app/app.jar

ENV PORT=8080
ENV BASE_URL=http://localhost:8080
ENV DB_URL=jdbc:h2:file:/app/data/urlshortener;DB_CLOSE_DELAY=-1;AUTO_SERVER=TRUE

RUN mkdir -p /app/data && chown -R appuser:appuser /app
USER appuser

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
