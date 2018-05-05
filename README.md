# About this - mittagstisch
[![MIT license](https://img.shields.io/badge/license-MIT-blue.svg)](./LICENSE.md)
[![dependencies Status](https://david-dm.org/inpercima/mittagstisch/status.svg)](https://david-dm.org/inpercima/mittagstisch)
[![devDependencies Status](https://david-dm.org/inpercima/mittagstisch/dev-status.svg)](https://david-dm.org/inpercima/mittagstisch?type=dev)

The lunch in your area. Based on Leipzig (Plagwitz, Lindenau).

This project was generated with [swaaplate](https://github.com/inpercima/swaaplate).

# Prerequisites
## Node, npm or yarn
* `node 8.10.0` or higher in combination with
  * `npm 5.7.1` or higher or
  * `yarn 1.5.1` or higher, used in this repository

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

```
# build js resources in devMode, no backend given
yarn run build:dev

# build js resources in prodMode, compressed, no backend given
yarn run build:prod

# build js resources and application in devMode
./mvnw spring-boot:run
# open result in browser
http://localhost:8080/
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
