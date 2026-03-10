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

## Development Mode

### Setup for development

1. **Create development configuration file:**

   ```bash
   cp src/main/resources/application.yml src/main/resources/application-dev.yml
   ```

   **Note**: This file will not be under version control (listed in .gitignore).

2. **Configure development settings:**

   Edit `src/main/resources/application-dev.yml` to match your local environment:
   * Database connection settings
   * Server port (default: 8080)
   * Logging levels
   * Other development-specific configurations

### Running in development mode

**Recommended: Using Maven with dev profile**

```bash
# Short command (uses dev profile by default)
./mvnw

# Explicit dev profile
./mvnw spring-boot:run -Pdev
```

The backend API will be available at **http://localhost:8080/**

### Development workflow

1. Start the backend: `./mvnw spring-boot:run -Pdev`
2. Make changes to your Java code
3. Stop the server (Ctrl+C) and restart to see changes
4. Use your IDE's debugging capabilities for better development experience

**Note**: For automatic reload on code changes, consider using Spring Boot DevTools in your IDE.

## Production Mode

### Setup for production

1. **Create production configuration file:**

   ```bash
   cp src/main/resources/application.yml src/main/resources/application-prod.yml
   ```

2. **Configure production settings:**

   Edit `src/main/resources/application-prod.yml`:
   * Production database connection (secure credentials)
   * Production server settings
   * Logging configuration for production
   * Security settings
   * Any production-specific environment variables

### Building for production

**Build the JAR file:**

```bash
# Build with tests
./mvnw clean package

# Build without tests (faster, but not recommended)
./mvnw clean package -DskipTests
```

This creates `mittagstisch-1.0.0-SNAPSHOT.jar` in the `target/` directory.

### Running in production

**Option 1: Direct JAR execution**

```bash
# Copy production config alongside the JAR
cp src/main/resources/application-prod.yml target/application-prod.yml

# Navigate to target directory and run
cd target
java -jar ./mittagstisch-1.0.0-SNAPSHOT.jar --spring.profiles.active=prod
```

**Option 2: Using Maven with prod profile (for testing production build locally)**

```bash
# Short command with prod profile
./mvnw -Pprod

# Explicit prod profile
./mvnw spring-boot:run -Pprod
```

### Deployment

For production deployment:
1. Build the application JAR as described above
2. Copy `mittagstisch-1.0.0-SNAPSHOT.jar` and `application-prod.yml` to your server
3. Set up as a systemd service or use Docker (recommended)
4. Ensure proper security configurations
5. Use a reverse proxy (nginx, Apache) in front of the application
6. Set up proper logging and monitoring

See the [Docker Guide](../docker/README.md) for containerized deployment.
