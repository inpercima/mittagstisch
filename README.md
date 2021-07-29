# Mittagstisch

[![MIT license](https://img.shields.io/badge/license-MIT-blue.svg)](./LICENSE.md)

The lunch in your area. Based on Leipzig (Plagwitz, Lindenau).

This project was generated with [swaaplate](https://github.com/inpercima/swaaplate) version 2.3.1.

## Prerequisites

### Angular CLI

* `angular-cli 12.1.1` or higher

### Java

* `jdk 16` or higher

### Node, npm or yarn

* `node 14.16.1` or higher in combination with
  * `npm 6.14.12` or higher or
  * `yarn 1.22.5` or higher, used in this repository

## Dependency check

Some libraries could not be updated b/c of peer dependencies or knowing issues.

| library    | current version | last version | reason |
| ---------- | --------------- | ------------ | ------ |
| rxjs       | 6.6.0           | 7.3.0        | "@angular/common@12.1.4" has incorrect peer dependency "rxjs@^6.5.3" |

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
