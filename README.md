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

* `@angular/cli 19.2.10` or higher

Install @angular/cli by running:

```bash
pnpm install -g @angular/cli@19
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

For frontend check [mittagstisch - frontend](./frontend/README.md).

For backend check [mittagstisch - backend](./backend/README.md).

For docker check [mittagstisch - docker](./README_docker.md).

### Install Tools

Some tools are both used by backend and frontend.
Run the following command to install:

```bash
pnpm install
```

### Starting the application

For development you can use two separate terminals for starting backend and frontend separately.
More can find in the specified README files in the separate folders.

You could also use following command in root folder to start in one single terminal:

```bash
pnpm start
```
