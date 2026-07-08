# Mittagstisch - backend

This guide covers the Spring Boot backend setup and usage for both development and production environments.

## Prerequisites

* JDK 21 or higher
* Maven (included via Maven Wrapper - mvnw)

## Getting started

```bash
# Navigate to backend directory
cd backend
```

## Database Migrations with Flyway

Database schema and seed data are managed by [Flyway](https://flywaydb.org/). Migration scripts are located in `src/main/resources/db/migration/` and are applied automatically on application startup.

- `V1__create_schema.sql` - Creates the database schema
- `V2__seed_bistro.sql` - Seeds initial bistro data

To add a new migration, create a file following the naming convention `V<number>__<description>.sql`.

## Development Mode

### Setup for development

1. **Create development configuration file:**

   ```bash
   cp src/main/resources/application-dev.default.yml src/main/resources/application-dev.yml
   ```

   **Note**: This file will not be under version control (listed in .gitignore).

2. **Configure development settings:**

   Edit `src/main/resources/application-dev.yml` to match your local environment:
   * Database connection settings (URL, username, password)
   * OpenAI API key
   * Other development-specific configurations

### Running in development mode

```bash
# Uses dev profile by default
./mvnw
```

The backend API will be available at **http://localhost:8080/**

Flyway will automatically apply any pending migrations on startup.

## Production Mode

### Setup for production

1. **Create production configuration file:**

   Edit `src/main/resources/application-prod.yml`:
   * Production database connection (uses `mysql` as host within Docker network)
   * Production API keys
   * Other production-specific settings

### Building for production

The prod Maven profile builds the frontend via pnpm and bundles it into the JAR:

```bash
./mvnw clean package -Pprod
```

This:
1. Runs `pnpm install --frozen-lockfile` in the frontend directory
2. Runs `pnpm run build:prod` to build the Angular frontend
3. Copies the frontend build output into `public/` in the JAR
4. Creates `mittagstisch-1.13.1.jar` in the `target/` directory

### Running in production

**Via Docker (recommended):**

The JAR and `application-prod.yml` are copied into the Docker image (see [Dockerfile](../docker/Dockerfile)). The container runs with the prod profile and connects to MySQL within the Docker network.

See the [Docker Guide](../docker/README.md) for the full production deployment.

**Direct JAR execution (for testing):**

```bash
cd target
java -jar ./mittagstisch-1.13.1.jar --spring.profiles.active=prod
```

Ensure `application-prod.yml` is in the same directory as the JAR.
