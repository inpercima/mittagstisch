# mittagstisch - docker

This is for managing `mysql` and `phpMyAdmin`.

## Getting started

Create an environment file for `docker` and `docker compose` and check the [configuration](#configuration).

```bash
cp default.env .env
```

**Note**: This file will not be under version control but listed in .gitignore.

Check for the existence of `environment.prod.ts` as described in [Mittagstisch - frontend](./frontend).

Check for the existence of `application-prod.yml` as described in [Mittagstisch - backend](./backend).

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

* default: `8.0.30`
* type: `string`

### `PHPMYADMIN_PORT`

Defines the port for phpMyAdmin

* default: `80`
* type: `string`

### `PHPMYADMIN_VERSION`

Defines the version for phpMyAdmin

* default: `5.2.0`
* type: `string`

## Usage

### Information

To work with the compose file use following commands.
Use as `project-name` the same name from the configuration `COMPOSE_PROJECT_NAME`.

### Mysql in this project

If you manage your data in this project, run following commands.

**Note**: On the first run you need to look into the logs of mysql to get the root password.
The password will not shown again.

```bash
# run compose file
docker compose --project-name mittagstisch -f compose.yml -f compose-mysql.yml up -d

# check the log file of the mysql service and search for "GENERATED ROOT PASSWORD" and note this
docker logs mittagstisch_mysql

# stop compose file
docker compose --project-name mittagstisch down
```

### Mysql out of this project

If you manage your data out of this project, you can use the dump file (`dump.sql`) and connect to your database.
Then run following commands:

```bash
# run compose file
docker compose --project-name mittagstisch up -d

# stop compose file
docker compose --project-name mittagstisch down
```
