# Mittagstisch

[![MIT license](https://img.shields.io/badge/license-MIT-blue.svg)](./LICENSE.md)

The lunch in your area. Based on Leipzig (Plagwitz, Lindenau).

This project was generated with [swaaplate](https://github.com/inpercima/swaaplate) version 2.5.3.

## Prerequisites

### Node, npm or pnpm

It's recommended to use [nvm (Node version Manager)](https://github.com/nvm-sh/nvm).

* `node 22.20.0` or higher in combination with
  * `npm 10.9.3` or higher or
  * `pnpm 10.27.0` or higher, used in this repository

Install pnpm by running:

```bash
npm install -g pnpm@10.27.0
```

### Info for npm and pnpm

This repo uses `pnpm` as package manager.
You can also use `npm` for your local work but changes will be made by `pnpm` only.

### Angular CLI

* `@angular/cli 21.1.1` or higher

Install @angular/cli by running:

```bash
pnpm install -g @angular/cli@21
```

### Java

* `jdk 21` or higher

### Docker (when running services within docker)

* `docker 28.3.2` or higher

## Getting started

### Clone project

```bash
# clone project
git clone https://github.com/inpercima/mittagstisch/
cd mittagstisch
```

### Read more

Check the documentation for each module/component.
Each individual document describes the basic tasks for the module.

For frontend check [mittagstisch - frontend](./frontend/README.md).

For backend check [mittagstisch - backend](./backend/README.md).

For docker check [mittagstisch - docker](./docker/README.md).

### Install tools

Some tools are both used by backend and frontend.
Run the following command to install:

```bash
pnpm install
```

## Development Mode

### Starting the application in development

For development, you have multiple options to run the application:

#### Option 1: Start everything with one command (Recommended for quick start)

```bash
pnpm start
```

This command will start both backend and frontend concurrently.

#### Option 2: Start backend and frontend separately

Use two separate terminals for more control and better log visibility:

**Terminal 1 - Backend:**
```bash
cd backend
./mvnw spring-boot:run -Pdev
```

**Terminal 2 - Frontend:**
```bash
cd frontend
pnpm start
```

For detailed development setup and configuration options, check:
* [Frontend Development Guide](./frontend/README.md)
* [Backend Development Guide](./backend/README.md)
* [Docker Setup for Development](./docker/README.md)

### Access the application

* **Frontend:** http://localhost:4200/
* **Backend API:** http://localhost:8080/
* **phpMyAdmin:** http://localhost (or configured port in `.env`)

## Production Mode

### Prerequisites for production

* Docker and Docker Compose installed on the server
* Server with sufficient resources (RAM, CPU, storage)

### Build process

1. **Prepare frontend environment:**
   
   Check for the existence of `environment.prod.ts` as described in [Frontend Guide](./frontend/README.md).

2. **Build the backend:**
   
   ```bash
   cd backend
   ./mvnw clean package
   ```

   This creates `mittagstisch-1.0.0-SNAPSHOT.jar` in the `target` directory.

### Deployment

1. **Prepare configuration files:**

   Copy the following files to your server:
   * `.env` (Docker environment configuration)
   * `dump.sql` (Database dump file)
   * `docker-compose.yml` and `docker-compose.prod.yml`
   * `mittagstisch-1.0.0-SNAPSHOT.jar` (from backend/target)
   * `application-prod.yml` (Spring Boot production configuration)

2. **Configure for your environment:**

   * Modify `.env` with your production settings
   * Update `application-prod.yml` with production-specific configurations (database credentials, API keys, etc.)

3. **Deploy and run:**

   Run the Docker Compose setup as described in the [Docker Production Guide](./docker/README.md).

   ```bash
   docker compose --project-name mittagstisch -f docker-compose.yml -f docker-compose.prod.yml up -d
   ```

For detailed production deployment instructions, see [Docker Guide](./docker/README.md).
