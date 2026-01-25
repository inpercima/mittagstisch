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

* `@angular/cli 21.0.4` or higher

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

### Starting the application in dev mode

For development you can use two separate terminals for starting backend and frontend separately.
More can find in the specified README files in the separate folders.

You could also use following command in root folder to start in one single terminal:

```bash
pnpm start
```

### Deploy the application

#### Server preparation

First you need to have Docker installed on the server.

#### Build process

Check for the existence of `environment.prod.ts` as described in [Mittagstisch - frontend](./frontend).
Build the backend by using `./mvnw clean package`.

#### Deployment

Copy following files to the server:

* `.env`
* `dump.sql`
* `docker-compose.yml` and `docker-compose.prod.yml`
* `mittagstisch-1.0.0-SNAPSHOT.jar`
* `application-prod.yml`

Modify the `.env` file for your needs.
Modify the `application-prod.yml` for your needs.

#### Run

Run the compose files for prod mode as described in [mittagstisch - docker](./docker/README.md).
