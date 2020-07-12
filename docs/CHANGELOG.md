# Changelog
All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](http://keepachangelog.com/en/1.0.0/) and this project adheres to the format [MCVERSION-MAJORMOD.MAJORAPI.MINOR.PATCH](https://mcforge.readthedocs.io/en/1.15.x/conventions/versioning/).

## [0.5] - 2020.07.11
### Fixed
- Removed debug line of code

## [0.4] - 2020.07.10
### Fixed
- Attempted fix for potential race condition in fetching recipes [#10](https://github.com/TheIllusiveC4/Polymorph/issues/10)
- Attempted fix for Simple Storage Network integration crash [#11](https://github.com/TheIllusiveC4/Polymorph/issues/11)

## [0.3] - 2020.06.21
### Added
- Added Silent Gear Crafting Station support [#5](https://github.com/TheIllusiveC4/Polymorph/issues/5)
- Added Simple Storage Network Storage Request Table and Crafting Remote support [#6](https://github.com/TheIllusiveC4/Polymorph/issues/6)
- Added JEI support so that transferring recipes will keep the selected output
### Changed
- Added error handling to recipe fetching, which prevents crashes and helps diagnose [#3](https://github.com/TheIllusiveC4/Polymorph/issues/3)

## [0.2] - 2020.06.16
### Fixed
- Fixed default crafting outputs being used when shift-clicking using FastWorkbench [#2](https://github.com/TheIllusiveC4/Polymorph/issues/2)
- Fixed duplication bug when shift-clicking single quantity crafting outputs [#1](https://github.com/TheIllusiveC4/Polymorph/issues/1)

## [0.1] - 2020.06.13
- Initial beta release
