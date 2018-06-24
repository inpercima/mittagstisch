# About this - mittagstisch
[![MIT license](https://img.shields.io/badge/license-MIT-blue.svg)](./LICENSE.md)
[![dependencies Status](https://david-dm.org/inpercima/mittagstisch/status.svg)](https://david-dm.org/inpercima/mittagstisch)
[![devDependencies Status](https://david-dm.org/inpercima/mittagstisch/dev-status.svg)](https://david-dm.org/inpercima/mittagstisch?type=dev)

The lunch in your area. Based on Leipzig (Plagwitz, Lindenau).

This project was generated with [swaaplate](https://github.com/inpercima/swaaplate).

# Prerequisites
## Node, npm or yarn
* `node 8.11.3` or higher in combination with
  * `npm 5.6.0` or higher or
  * `yarn 1.7.0` or higher, used in this repository

# Getting started

```
# clone project
git clone https://github.com/inpercima/mittagstisch
cd mittagstisch

# copy src/config.default.json to src/config.json
cp src/config.default.json src/config.json

# install tools and frontend dependencies
yarn
```

# Usage
## Run

```
# build client and server in development mode
./mvnw clean spring-boot:run

# build client and server in production mode
./mvnw clean spring-boot:run -Pprod

# serve on http://localhost:8080/
```

## Build

```
# build project
./mvnw clean package -Pprod

# build project without tests to speed up
./mvnw clean package -Pprod -DskipTests

# run project as jar
java -jar target/mittagstisch-*.jar
```

# Configuration
## General
All options have to bet set but some of them do not need to be changed.

## Table of contents
* [appname](#appname)
* [routes/default](#routesdefault)
* [routes/features/show](#routesfeaturesshow)
* [routes/login/activate](#routesloginactivate)
* [routes/login/show](#routesloginshow)
* [routes/notFound/redirect](#routesnotfoundredirect)
* [theme](#theme)

## `appname`
Applicationwide title of the app, displayed in title and toolbar.
* default: `mittagstisch`
* type: `string`

## `routes/default`
The main route and the redirect route after login if no route is stored.
* default: `dashboard`
* type: `string`

## `routes/features/show`
Defines whether feature routes will be displayed or not.
* default: `true`
* type: `boolean`
* values: `true`/`false`

## `routes/login/activate`
Defines whether a login will be used or not.
* default: `true`
* type: `boolean`
* values: `true`/`false`

## `routes/login/show`
Defines whether login route will be displayed or not.
* default: `false`
* type: `boolean`
* values: `true`/`false`

## `routes/notFound/redirect`
Defines whether the 404 route will redirect to the default route or not.
* default: `false`
* type: `boolean`
* values: `true`/`false`

## `theme`
Name of a build-in theme from angular-material.
* default: `indigo-pink`
* type: `string`
* values: `deeppurple-amber`/`indigo-pink`/`pink-bluegrey`/`purple-green`
