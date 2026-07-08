# Mittagstisch - docker

This guide covers Docker setup and usage for running MySQL, phpMyAdmin (development), and nginx (production).

## Prerequisites

- Docker 28.3.2 or higher
- Docker Compose

## Getting started

### Create environment configuration

```bash
# Create .env file from template
cp default.env .env
```

**Note**: This file will not be under version control (listed in .gitignore).

### Configure environment variables

Edit the `.env` file to customize your setup.
See [Configuration](#configuration) section for details.

## Configuration

### Table of contents

- [COMPOSE_PROJECT_NAME](#compose_project_name)
- [MYSQL_PASSWORD](#mysql_password)
- [MYSQL_PORT](#mysql_port)
- [MYSQL_USER](#mysql_user)
- [MYSQL_VERSION](#mysql_version)
- [PHPMYADMIN_PORT](#phpmyadmin_port)
- [PHPMYADMIN_VERSION](#phpmyadmin_version)

### `COMPOSE_PROJECT_NAME`

Defines a global name for the compose project used for the container and the database

- default: `mittagstisch`
- type: `string`

### `MYSQL_PASSWORD`

Defines the password for MySQL

- default: `mysql`
- type: `string`

### `MYSQL_PORT`

Defines the port for MySQL

- default: `3306`
- type: `string`

### `MYSQL_USER`

Defines the user for MySQL

- default: `mysql`
- type: `string`

### `MYSQL_VERSION`

Defines the version for MySQL

- default: `8.0.44-debian`
- type: `string`

### `PHPMYADMIN_PORT`

Defines the port for phpMyAdmin

- default: `81`
- type: `string`

### `PHPMYADMIN_VERSION`

Defines the version for phpMyAdmin

- default: `5.2.3`
- type: `string`

## Development Mode

### Running Docker services for development

**Start MySQL and phpMyAdmin:**

```bash
docker compose --project-name mittagstisch -f docker-compose.yml -f docker-compose.dev.yml up -d
```

**Important for first run:**

On the first run, you need to retrieve the MySQL root password from the logs:

```bash
# Check MySQL logs for generated password
docker logs mittagstisch_mysql

# Look for the line containing "GENERATED ROOT PASSWORD"
# Save this password securely - it won't be shown again
```

### Access services in development

- **phpMyAdmin:** [http://localhost:81](http://localhost:81) (or your configured `PHPMYADMIN_PORT`)
  - Server: `mysql`
  - Username: root or `MYSQL_USER`
  - Password: Generated password from logs or `MYSQL_PASSWORD`

- **MySQL Direct Connection:**
  - Host: localhost
  - Port: `MYSQL_PORT` (default: 3306)
  - Username: root or `MYSQL_USER`
  - Password: From logs or `MYSQL_PASSWORD`

**Note**: Database schema and seed data are managed by Flyway and applied automatically when the backend starts.
You do not need to manually import SQL dumps.

### Stop development services

```bash
docker compose --project-name mittagstisch down
```

**Note**: This stops containers but preserves data volumes.
Use `docker compose down -v` to also remove volumes (deletes all database data).

## Production Mode

### Architecture

In production, Docker Compose runs three services:

1. **MySQL** - Database server
2. **webapp** - Spring Boot application (with embedded Angular frontend), built from the [Dockerfile](./Dockerfile)
3. **nginx** - Reverse proxy handling HTTP/HTTPS traffic and forwarding to the webapp

### Setup

1. **Build the production JAR** (see [Backend Guide](../backend/README.md)):

   ```bash
   cd backend
   ./mvnw clean package -Pprod
   ```

2. **Prepare the deployment directory** with these files:
   - `docker-compose.yml` and `docker-compose.prod.yml`
   - `.env` (from `default.env`, configured for production)
   - `Dockerfile`
   - `application-prod.yml` (configured with production credentials)
   - `mittagstisch-<VERSION>.jar` (from `backend/target/`)
   - `nginx/nginx.conf` (nginx configuration)
   - `nginx/certs/` (SSL certificates)

3. **Configure nginx:**

   Create `nginx/nginx.conf` with your domain, SSL certificates, and proxy settings to forward traffic to `webapp:8080`.

### Running Docker services for production

```bash
docker compose --project-name mittagstisch -f docker-compose.yml -f docker-compose.prod.yml up -d --build
```

This starts MySQL, builds and runs the webapp container, and starts nginx as a reverse proxy.

Database migrations are applied automatically by Flyway on webapp startup.

### Production considerations

1. **Security:**
   - Change default passwords in `.env`
   - Use strong MySQL passwords
   - Configure SSL certificates for nginx
   - Consider using Docker secrets for sensitive data

2. **Data persistence:**
   - Ensure volumes are properly configured for data persistence
   - Set up regular database backups
   - Monitor disk space

3. **Monitoring:**
   - Check logs: `docker logs mittagstisch_webapp` or `docker logs nginx`
   - Monitor container health: `docker ps`
   - Set up alerts for container failures

### Stop production services

```bash
docker compose --project-name mittagstisch down
```

## Database Management

### Import database dump

If you have an existing database dump (`dump.sql`):

```bash
# Copy dump file to MySQL container
docker cp dump.sql mittagstisch_mysql:/dump.sql

# Import the dump (you will be prompted for password)
docker exec -it mittagstisch_mysql mysql -uroot -p mittagstisch < dump.sql
```

**Note**: For normal operation, Flyway handles schema creation and seed data automatically.
Manual imports are only needed for restoring backups or migrating existing data.

### Export database backup

```bash
# Export database to dump file (you will be prompted for password)
docker exec -it mittagstisch_mysql mysqldump -uroot -p mittagstisch > backup.sql
```
