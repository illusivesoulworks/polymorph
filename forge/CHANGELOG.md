# Changelog
All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](http://keepachangelog.com/en/1.0.0/) and this project adheres to the format [MCVERSION-MAJORMOD.MAJORAPI.MINOR.PATCH](https://mcforge.readthedocs.io/en/1.15.x/conventions/versioning/).

## [1.18.1-0.33] - 2021.12.23
### Added
- Re-added all mod compatibility from 1.16.5
### Changed
- Updated to Minecraft 1.18.1
- Updated to Forge 39.0+

## [1.17.1-0.32] - 2021.12.23
### Added
- Re-added FastFurnace and FastWorkbench compatibility
### Fixed
- Fixed dedicated server lag [#117](https://github.com/TheIllusiveC4/Polymorph/issues/117)
- Fixed freezes with Applied Energistics 2 pattern terminal [#114](https://github.com/TheIllusiveC4/Polymorph/issues/114)

## [1.17.1-0.31] - 2021.11.16
### Fixed
- Fixed potential overflow errors when using certain modded furnaces [#108](https://github.com/TheIllusiveC4/Polymorph/issues/108)

## [1.17.1-0.30] - 2021.11.14
### Added
- Added Sophisticated Backpack's auto-smelting upgrade compatibility [#105](https://github.com/TheIllusiveC4/Polymorph/issues/105)
### Fixed
- Fixed potential fallback errors so that recipes can still operate normally even if the Polymorph logic fails
- Fixed Sophisticated Backpack's smelting upgrade compatibility
- Fixed recipe failures after changing dimensions [#106](https://github.com/TheIllusiveC4/Polymorph/issues/106)

## [1.17.1-0.29] - 2021.11.12
### Changed
- Improved recipe output equality logic
- Recipe outputs will now display the stack size

## [1.17.1-0.28] - 2021.11.06
### Changed
- Updated to Minecraft 1.17.1+
- Updated to Forge 37+
### Removed
- Removed compatibility for mods that are not on 1.17.1+ yet
