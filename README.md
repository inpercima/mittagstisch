# Mittagstisch

[![MIT license](https://img.shields.io/badge/license-MIT-blue.svg)](./LICENSE.md)

The lunch in your area. Based on Leipzig (Plagwitz, Lindenau).

This project was generated with [swaaplate](https://github.com/inpercima/swaaplate) version 2.1.0.

## Prerequisites

### Angular CLI

* `angular-cli 11.0.2` or higher

### Java

* `jdk 11` or higher

### Node, npm or yarn

* `node 12.19.0` or higher in combination with
  * `npm 6.14.8` or higher or
  * `yarn 1.22.5` or higher, used in this repository

## Dependency check

Some libraries could not be updated b/c of peer dependencies or knowing issues.

| library    | current version | wanted version | reason |
| ---------- | --------------- | -------------- | ------ |
| typescript | 4.0.5           | 4.1.2          | @angular-devkit/build-angular@0.1100.2" has incorrect peer dependency "typescript@~4.0.0" |
| zone.js    | 0.10.3          | 0.11.3         | @angular/core@11.0.2" has incorrect peer dependency "zone.js@~0.10.3" |

## Getting started

```bash
# clone project
git clone https://github.com/inpercima/mittagstisch/
cd mittagstisch
```

## Usage

### Modules

For the client check [mittagstisch - client](./client).

For the server check [mittagstisch - server](./server).
