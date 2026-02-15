# CiNect Spring Backend

Cinema booking platform backend built with Spring Boot 3.x, Java 21, PostgreSQL, JWT authentication, and WebSockets.

## Prerequisites

- **Java 21** (Eclipse Temurin or OpenJDK)
- **Maven 3.9+**
- **PostgreSQL 16+**

## Setup

1. Create and configure the database:

   ```bash
   createdb cinect_spring
   ```

2. Copy the environment example and adjust values:

   ```bash
   cp .env.example .env
   # Edit .env with your DB credentials, JWT secret, etc.
   ```

3. Run the application:

   ```bash
   mvn spring-boot:run
   ```

   Or run tests first:

   ```bash
   mvn test
   mvn spring-boot:run
   ```

## Environment Variables

| Variable           | Description                        | Default                                              |
|--------------------|------------------------------------|------------------------------------------------------|
| `DB_URL`           | PostgreSQL JDBC URL                | `jdbc:postgresql://localhost:5432/cinect_spring`     |
| `DB_USERNAME`      | Database username                  | `postgres`                                           |
| `DB_PASSWORD`      | Database password                  | `postgres`                                           |
| `JWT_SECRET`       | JWT signing key (min 256 bits)     | -                                                    |
| `JWT_ACCESS_EXP`   | Access token expiry (ms)           | `900000` (15 min)                                    |
| `JWT_REFRESH_EXP`  | Refresh token expiry (ms)          | `604800000` (7 days)                                 |
| `CORS_ORIGINS`     | Allowed CORS origins               | `http://localhost:3000`                              |
| `PORT`             | Server port                        | `8080`                                               |
| `HOLD_TTL`         | Seat hold TTL (minutes)            | `10`                                                 |
| `PAYMENT_TIMEOUT`  | Payment timeout (minutes)          | `2`                                                  |
| `POINTS_PER_BOOKING` | Loyalty points per booking      | `10`                                                 |
| `MAINTENANCE_MODE` | Enable maintenance mode            | `false`                                              |

## Run Commands

**Development:**
```bash
mvn spring-boot:run
```

**Tests:**
```bash
mvn test
```

**Docker:**
```bash
docker build -t cinect-spring-backend .
docker run -p 8080:8080 \
  -e DB_URL=jdbc:postgresql://host.docker.internal:5432/cinect_spring \
  -e DB_USERNAME=postgres \
  -e DB_PASSWORD=postgres \
  -e JWT_SECRET=your-jwt-secret-key \
  cinect-spring-backend
```

Or use the project root `docker-compose.yml`:

```bash
cd ..
docker compose up -d
```

## API Documentation

- **Base URL:** `http://localhost:8080/api/v1`
- **Health:** `http://localhost:8080/api/v1/actuator/health`
- **Info:** `http://localhost:8080/api/v1/actuator/info`

## Tech Stack

- Spring Boot 3.4, Spring Security, Spring Data JPA
- PostgreSQL, Flyway migrations
- JWT (jjwt), WebSockets
- Actuator, Quartz scheduling
