# Mittagstisch

[![MIT license](https://img.shields.io/badge/license-MIT-blue.svg)](./LICENSE.md)

Daily lunches from canteens and bistros in Leipzig (mainly in Plagwitz and Lindenau) are automatically recorded and clearly displayed.

## Prerequisites

### Node, npm or pnpm

It's recommended to use [nvm (Node version Manager)](https://github.com/nvm-sh/nvm).

- `node 24.16.0` or higher in combination with
  - `npm 11.13.0` or higher or
  - `pnpm 11.8.0` or higher, used in this repository

Install pnpm by running:

```bash
npm install -g pnpm@11.8.0
```

### Info for npm and pnpm

This repo uses `pnpm` as package manager.
You can also use `npm` for your local work but changes will be made by `pnpm` only.

### Angular CLI

- `@angular/cli 22.0.5` or higher

Install @angular/cli by running:

```bash
pnpm install -g @angular/cli@22
```

### Java

- `jdk 21` or higher

### Docker (when running services within docker)

- `docker 28.3.2` or higher

## Getting started

### Clone project

```bash
git clone https://github.com/inpercima/mittagstisch/
cd mittagstisch
```

### Install tools

Some tools are both used by backend and frontend.
Run the following command to install:

```bash
pnpm install
```

## Development Mode

### Starting the application in development

For development, you need a running MySQL instance.
Start one via Docker (see [Docker Guide](./docker/README.md)) or use an external database.

Database schema and seed data are managed by [Flyway](https://flywaydb.org/) and applied automatically on application
startup (see `backend/src/main/resources/db/migration/`).

#### Option 1: Start everything with one command (Recommended)

```bash
pnpm start
```

This command will start both backend and frontend concurrently.

#### Option 2: Start backend and frontend separately

Use two separate terminals for more control and better log visibility:

**Terminal 1 - Backend:**

```bash
cd backend
./mvnw
```

**Terminal 2 - Frontend:**

```bash
cd frontend
pnpm start
```

For detailed development setup and configuration options, check:

- [Frontend Development Guide](./frontend/README.md)
- [Backend Development Guide](./backend/README.md)
- [Docker Setup for Development](./docker/README.md)

### Access the application

- **Frontend:** [http://localhost:4200/](http://localhost:4200/)
- **Backend API:** [http://localhost:8080/](http://localhost:8080/)
- **phpMyAdmin:** [http://localhost:81](http://localhost:81) (or configured `PHPMYADMIN_PORT` in `.env`)

## Production Mode

### Prerequisites for production

- Docker and Docker Compose installed on the server
- nginx configuration files (see `docker/nginx/`)
- SSL certificates for HTTPS

### Build process

1. **Prepare frontend environment:**

   Check for the existence of `environment.prod.ts` as described in [Frontend Guide](./frontend/README.md).

2. **Build the backend (includes frontend):**

   ```bash
   cd backend
   ./mvnw clean package -Pprod
   ```

   The prod profile automatically builds the frontend via pnpm and bundles it into the JAR. This creates `mittagstisch-<VERSION>.jar` in the `target/` directory.

### Deployment

1. **Prepare configuration files:**

   Copy the following files to your server:
   - `.env` (Docker environment configuration)
   - `docker-compose.yml` and `docker-compose.prod.yml`
   - `mittagstisch-<VERSION>.jar` (from backend/target)
   - `application-prod.yml` (Spring Boot production configuration)

2. **Configure for your environment:**
   - Modify `.env` with your production settings
   - Update `application-prod.yml` with production database credentials and API keys
   - Configure nginx with your domain and SSL certificates, for this create the folder `nginx/` in `docker/` and add a `nginx.conf` file

3. **Deploy and run:**

   ```bash
   docker compose --project-name mittagstisch -f docker-compose.yml -f docker-compose.prod.yml up -d --build
   ```

   This starts MySQL, the Spring Boot webapp (with embedded frontend), and nginx as reverse proxy.

   Database migrations are applied automatically by Flyway on startup.

For detailed production deployment instructions, see [Docker Guide](./docker/README.md).
