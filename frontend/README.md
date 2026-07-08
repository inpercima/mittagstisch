# Mittagstisch - frontend

This guide covers the Angular frontend setup and usage for both development and production environments.

## Prerequisites

* Node.js 24.16.0 or higher
* pnpm 11.8.0 or higher
* Angular CLI 21.2.7 or higher

## Getting started

```bash
# Navigate to frontend directory
cd frontend

# Install dependencies
pnpm install
```

## Development Mode

### Setup for development

1. **Create development environment file:**

   ```bash
   cp src/environments/environment.ts src/environments/environment.dev.ts
   ```

   **Note**: This file will not be under version control (listed in .gitignore).

2. **Configure development settings:**

   Edit `src/environments/environment.dev.ts` to match your local setup:
   * Set `production: false`
   * Configure `api` URL (default: `./api/`)
   * Adjust other settings as needed (see [Configuration](#configuration) section)

### Running in development mode

```bash
pnpm start
```

The application will be available at **http://localhost:4200/** and automatically reload when you make changes to the source code.

### Building for development

```bash
pnpm build:dev
```

This runs `ng lint` followed by `ng build --configuration=development`, creating a development build in the `dist/` directory.

## Production Mode

### Setup for production

1. **Create production environment file:**

   ```bash
   cp src/environments/environment.ts src/environments/environment.prod.ts
   ```

2. **Configure production settings:**

   Edit `src/environments/environment.prod.ts`:
   * Set `production: true`
   * Configure production `api` URL
   * Set appropriate theme and other production-specific settings

### Building for production

```bash
pnpm build:prod
```

This runs `ng lint` followed by `ng build` (production configuration), creating an optimized bundle in `dist/mittagstisch/browser/`.

**Note**: In production deployment, the frontend is automatically built via the backend's Maven prod profile (`./mvnw clean package -Pprod`). You typically don't need to run this command separately.

### Deployment

In production, the frontend is bundled into the Spring Boot JAR and served directly by the backend. nginx acts as a reverse proxy in front of the application (see [Docker Guide](../docker/README.md)).

## Testing

```bash
# Run unit tests
ng test

# Run end-to-end tests
ng e2e
```

## Configuration

### General

All options have to be set in the environment files but some of them do not need to be changed.
All defaults refer to the environment file (`environment.ts`), they are prepared in `development mode` (`environment.dev.ts`).
Change for `production mode` the option `production` to `true`.

### Table of contents

- [api](#api)
- [appname](#appname)
- [production](#production)
- [theme](#theme)

### `api`

Defines the URL to the backend.

- default: `./api/`
- type: `string`

### `appname`

Applicationwide title of the app, displayed in title and toolbar.

- default: `Mittagstisch`
- type: `string`

### `production`

Defines whether the app is in production or not.

- default: `false`
- type: `boolean`
- values: `true`/`false`

### `theme`

Name of a pre-build-theme or a custom theme.

- default: `azure-blue`
- type: `string`
- values: `rose-red`/`azure-blue`/`magenta-violet`/`cyan-orange`/`custom`

To modify the custom theme just edit the colors and themes in `themes.scss`.
