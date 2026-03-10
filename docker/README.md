# Mittagstisch - docker

This guide covers Docker setup and usage for running MySQL and phpMyAdmin in both development and production environments.

## Prerequisites

* Docker 28.3.2 or higher
* Docker Compose (included with Docker Desktop)

## Getting started

### Create environment configuration

```bash
# Create .env file from template
cp default.env .env
```

**Note**: This file will not be under version control (listed in .gitignore).

### Configure environment variables

Edit the `.env` file to customize your setup. See [Configuration](#configuration) section for details.

## Configuration

### Table of contents

* [COMPOSE_PROJECT_NAME](#compose_project_name)
* [MYSQL_PASSWORD](#mysql_password)
* [MYSQL_PORT](#mysql_port)
* [MYSQL_USER](#mysql_user)
* [MYSQL_VERSION](#mysql_version)
* [PHPMYADMIN_PORT](#phpmyadmin_port)
* [PHPMYADMIN_VERSION](#phpmyadmin_version)

### `COMPOSE_PROJECT_NAME`

Defines a global name for the compose project used for the container and the database

* default: `mittagstisch`
* type: `string`

### `MYSQL_PASSWORD`

Defines the password for mySQL

* default: `mysql`
* type: `string`

### `MYSQL_PORT`

Defines the port for mySQL

* default: `3306`
* type: `string`

### `MYSQL_USER`

Defines the user for mySQL

* default: `mysql`
* type: `string`

### `MYSQL_VERSION`

Defines the version for mySQL

* default: `8.0.44-debian`
* type: `string`

### `PHPMYADMIN_PORT`

Defines the port for phpMyAdmin

* default: `80`
* type: `string`

### `PHPMYADMIN_VERSION`

Defines the version for phpMyAdmin

* default: `5.2.3`
* type: `string`

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

* **phpMyAdmin:** http://localhost (or your configured `PHPMYADMIN_PORT`)
  * Server: `mysql`
  * Username: root or `MYSQL_USER`
  * Password: Generated password from logs or `MYSQL_PASSWORD`

* **MySQL Direct Connection:**
  * Host: localhost
  * Port: `MYSQL_PORT` (default: 3306)
  * Username: root or `MYSQL_USER`
  * Password: From logs or `MYSQL_PASSWORD`

### Stop development services

```bash
docker compose --project-name mittagstisch down
```

**Note**: This stops containers but preserves data volumes. Use `docker compose down -v` to also remove volumes (deletes all database data).

## Production Mode

### Running Docker services for production

**Start MySQL and phpMyAdmin:**

```bash
docker compose --project-name mittagstisch -f docker-compose.yml -f docker-compose.prod.yml up -d
```

### Production considerations

1. **Security:**
   * Change default passwords in `.env`
   * Restrict phpMyAdmin access (firewall rules or disable in production)
   * Use strong MySQL passwords
   * Consider using Docker secrets for sensitive data

2. **Data persistence:**
   * Ensure volumes are properly configured for data persistence
   * Regular database backups (see [Database Management](#database-management))
   * Monitor disk space

3. **Monitoring:**
   * Check logs regularly: `docker logs mittagstisch_mysql`
   * Monitor container health: `docker ps`
   * Set up alerts for container failures

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

# Import the dump
docker exec -i mittagstisch_mysql mysql -uroot -p[password] mittagstisch < dump.sql
```

### Export database backup

```bash
# Export database to dump file
docker exec mittagstisch_mysql mysqldump -uroot -p[password] mittagstisch > backup.sql
```

### External database management

If you manage your database outside of this project:
* Use the provided `dump.sql` file to initialize your database
* Configure your application to connect to the external database
* Update `application-dev.yml` or `application-prod.yml` with external database credentials
