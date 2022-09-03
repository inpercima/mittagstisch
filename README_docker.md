# mittagstisch - docker

## Getting started

This docker version is used to run the backend in container if java and mysql are not installed locally.

Create environment file for `docker` and `docker compose` and check the [configuration](#configuration).

```bash
cp default.env .env
```

**Note**: This file will not be under version control but listed in .gitignore.

TODO

## Usage

```bash
# build the image and container
docker compose build

# run the container
docker compose up -d

# run the container and rebuild
docker compose up -d --build

# stop the container
docker compose down
```

## Configuration

### Table of contents

TODO
