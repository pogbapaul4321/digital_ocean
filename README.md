# URL Shortener REST API

Production-ready REST API service built with **Java 21** and **Spring Boot** that accepts a long URL, generates a shortened alias, redirects users to the original URL, and handles concurrent creation requests safely.

## Features

- Create shortened URLs with auto-generated or custom aliases
- Handle custom alias collisions with `409 Conflict`
- Redirect users via `GET /{alias}`
- In-memory LRU caching for frequently accessed aliases
- Metadata endpoint with creation time and access counts
- Optional TTL-based link expiration
- Unit and integration tests
- GitHub Actions CI pipeline

## Requirements

- Java 21+
- Maven (or use the included `./mvnw` wrapper)

## Quick Start

```bash
# Run tests
./mvnw test

# Start the server
./mvnw spring-boot:run
```

The server listens on `http://localhost:8080` by default.

## Configuration

| Variable | Default | Description |
|----------|---------|-------------|
| `PORT` | `8080` | Server listen port |
| `BASE_URL` | `http://localhost:8080` | Base URL used in short link responses |
| `DB_URL` | `jdbc:h2:file:./data/urlshortener;...` | JDBC connection string |

## API

### Health Check

```bash
curl http://localhost:8080/health
```

### Create Short URL

Auto-generated alias:

```bash
curl -X POST http://localhost:8080/api/urls \
  -H "Content-Type: application/json" \
  -d '{"url":"https://example.com/very/long/path"}'
```

Custom alias with TTL:

```bash
curl -X POST http://localhost:8080/api/urls \
  -H "Content-Type: application/json" \
  -d '{"url":"https://example.com","alias":"my-link","ttl_seconds":3600}'
```

Example response:

```json
{
  "alias": "my-link",
  "short_url": "http://localhost:8080/my-link",
  "long_url": "https://example.com",
  "created_at": "2026-06-27T10:00:00Z",
  "access_count": 0,
  "expires_at": "2026-06-27T11:00:00Z"
}
```

### Get Metadata

```bash
curl http://localhost:8080/api/urls/my-link
```

### Redirect

```bash
curl -I http://localhost:8080/my-link
```

## Testing

```bash
./mvnw test
```

Build the JAR:

```bash
./mvnw -DskipTests package
java -jar target/url-shortener-1.0.0.jar
```

## Docker

```bash
docker build -t url-shortener .
docker run --rm -p 8080:8080 \
  -e BASE_URL=http://localhost:8080 \
  -v urlshortener-data:/app/data \
  url-shortener
```

## Architecture

See [docs/architecture.md](docs/architecture.md) for request lifecycle diagrams and component responsibilities.

## Project Structure

```text
src/main/java/com/urlshortener/
  UrlShortenerApplication.java   Spring Boot entrypoint
  controller/                    REST and redirect endpoints
  service/                       Business logic
  repository/                    JPA repository
  entity/                        Database entity
  cache/                         Redirect cache
  util/                          Validation and alias generation
  exception/                     Error handling
src/test/java/                   Unit and integration tests
docs/                            Architecture documentation
.github/workflows/               CI pipeline
```

## CI/CD

GitHub Actions runs on every push and pull request to `main`:

- `./mvnw test`
- `./mvnw -DskipTests package`

## DigitalOcean Deployment (Extension)

1. Build and push the Docker image to a container registry.
2. Create a DigitalOcean App Platform service or Droplet.
3. Set environment variables:
   - `BASE_URL=https://your-domain.com`
   - `DB_URL=jdbc:h2:file:/app/data/urlshortener;DB_CLOSE_DELAY=-1;AUTO_SERVER=TRUE`
4. Mount persistent storage for `/app/data`.

## License

MIT
