# Changelog

All notable changes to this project will be documented in this file. See [standard-version](https://github.com/conventional-changelog/standard-version) for commit guidelines.

### [1.4.2](https://github.com/Panzer1119/xREL4J/compare/v1.4.1...v1.4.2) (2021-08-11)


### Bug Fixes

* create SizeNumberToStringConverter.java and StringToSizeNumberConverter.java to properly serialize Size.java ([f0a1a11](https://github.com/Panzer1119/xREL4J/commit/f0a1a11bcccb14757cfd1bc28299c70411346080))

### [1.4.1](https://github.com/Panzer1119/xREL4J/compare/v1.4.0...v1.4.1) (2021-08-10)

## [1.4.0](https://github.com/Panzer1119/xREL4J/compare/v1.3.1...v1.4.0) (2021-08-10)


### Features

* use ResponseInterceptor.java objects in RestClient.java instead of a single instance to keep track of ratelimits for each Xrel instance ([50415de](https://github.com/Panzer1119/xREL4J/commit/50415de9fc65de59e5dff52bc67f85b6279756c2))


### Bug Fixes

* use correct accessor methods in Xrel.java after converting some Classes to Records ([d739512](https://github.com/Panzer1119/xREL4J/commit/d739512c7f42df0439724341abacc27c6939c9cc))

### [1.3.1](https://github.com/Panzer1119/xREL4J/compare/v1.3.0...v1.3.1) (2021-08-09)


### Bug Fixes

* ensure generic type T in PaginationList.java is Serializable ([01797f7](https://github.com/Panzer1119/xREL4J/commit/01797f7edd05b0ed9d48f0057f4c0a26fa966c0e))
* make RestClient.java publicly accessible ([922da27](https://github.com/Panzer1119/xREL4J/commit/922da2733103ce3f7d57a6dfe28e34eae8f94847))

## [1.3.0](https://github.com/Panzer1119/xREL4J/compare/v1.2.2...v1.3.0) (2021-08-09)


### Features

* add restClient variable to Xrel.java ([d4a3d41](https://github.com/Panzer1119/xREL4J/commit/d4a3d41703645d43336c07a0a7c86cbb2e83f2ec))
* make RestClient.java more customizable ([f9e09e1](https://github.com/Panzer1119/xREL4J/commit/f9e09e13c22ffabe8aecd719d48a856e0579e8ec))


### Bug Fixes

* use favsListAddEntry in postFavsListAddDelEntry when delete is false ([a931b16](https://github.com/Panzer1119/xREL4J/commit/a931b165ccde5abc35d4208002db163da5cceaca))

### [1.2.2](https://github.com/Panzer1119/xREL4J/compare/v1.2.1...v1.2.2) (2021-08-05)


### Bug Fixes

* replace groupId with com.github.panzer1119 ([0d4e775](https://github.com/Panzer1119/xREL4J/commit/0d4e775b6c9ae420dd0e393e1cc7b50ac3f4ff0e))

### [1.2.1](https://github.com/Panzer1119/xREL4J/compare/v1.2.0...v1.2.1) (2021-08-04)


### Bug Fixes

* implement Serializable on comment entities ([b597d66](https://github.com/Panzer1119/xREL4J/commit/b597d665fba520e128b58ac2c33473501b3785ad))
* implement Serializable on extinfo entities ([41fbdda](https://github.com/Panzer1119/xREL4J/commit/41fbdda9a349d450dd8717c59af9f468f07b236c))
* implement Serializable on favorite entities ([b270860](https://github.com/Panzer1119/xREL4J/commit/b2708609d29baea2ed758b89f887d036d33991cf))
* implement Serializable on release entities ([1b16791](https://github.com/Panzer1119/xREL4J/commit/1b1679198f939662c158e6a3eb2d9bc594ea8086))
* implement Serializable on the remaining entities ([da4dff5](https://github.com/Panzer1119/xREL4J/commit/da4dff5b3b14afcdfdb4352f08894f194f1960a2))

## 1.2.0 (2021-08-04)
