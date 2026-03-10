# Mittagstisch - frontend

This guide covers the Angular frontend setup and usage for both development and production environments.

## Prerequisites

* Node.js 22.20.0 or higher
* pnpm 10.27.0 or higher
* Angular CLI 21.1.1 or higher

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
   * Configure `api` URL (default: `http://localhost:8080/`)
   * Adjust other settings as needed (see [Configuration](#configuration) section)

### Running in development mode

**Option 1: Development server with hot reload (Recommended)**

```bash
pnpm start
```

The application will be available at **http://localhost:4200/** and automatically reload when you make changes to the source code.

**Option 2: Build for development**

```bash
pnpm build:dev
```

This creates a development build in the `dist/` directory. You can serve it with any static file server.

### Development workflow

1. Start the development server: `pnpm start`
2. Make changes to your code
3. The browser will automatically reload with your changes
4. Check the console for any compilation errors

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
# Build optimized production bundle
pnpm build:prod
```

This creates an optimized, compressed production build in the `dist/` directory with:
* Minified code
* Tree-shaking (removal of unused code)
* Optimized bundle sizes
* AOT (Ahead-of-Time) compilation

### Deployment

The production build files in `dist/` should be:
1. Served by a web server (nginx, Apache, etc.)
2. Configured with proper routing for Angular's router
3. Served over HTTPS in production

## Testing

```bash
# Run unit tests
ng test

# Run end-to-end tests
ng e2e
```

## Configuration

### General

All options have to been set in the environment files but some of them do not need to be changed.
All defaults refer to the environment file (`environment.ts`), they are prepared in `development mode` (`environment.dev.ts`).
Change for `production mode` the option `production` to `true`.

### Table of contents

* [api](#api)
* [appname](#appname)
* [defaultRoute](#defaultroute)
* [production](#production)
* [theme](#theme)

### `api`

Defines the URL to the backend.

* default: `http://localhost:8080/`
* type: `string`

### `appname`

Applicationwide title of the app, displayed in title and toolbar.

* default: `Mittagstisch`
* type: `string`

### `defaultRoute`

The default route and the route to be redirected after a login if no route is stored or if a route does not exist.

* default: `dashboard`
* type: `string`

### `production`

Defines whether the app is in production or not.

* default: `false`
* type: `boolean`
* values: `true`/`false`

### `theme`

Name of a pre-build-theme or a custom theme.

* default: `rose-red`
* type: `string`
* values: `rose-red`/`azure-blue`/`magenta-violet`/`cyan-orange`/`custom`

To modify the custom theme just edit the colors and themes in `themes.scss`.
