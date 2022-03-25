# Changelog
All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](http://keepachangelog.com/en/1.0.0/) and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [0.0.20-1.18.2] - 2022.03.24
### Added
- Added Origins `modify_crafting` power type compatibility
- Added Cammie's Wearable Backpacks compatibility

## [0.0.19-1.18.1] - 2022.01.13
### Added
- Re-added FastBench and Improved Stations compatibility

## [0.0.18-1.18.1] - 2022.01.03
### Fixed
- Fixed `fabric.mod.json` incorrectly listing Applied Energistics 2 as a required dependency [#131](https://github.com/TheIllusiveC4/Polymorph/issues/131)

## [0.0.17-1.18.1] - 2022.01.02
### Changed
- Updated to Applied Energistics 2 10.0.0-beta.3 [#129](https://github.com/TheIllusiveC4/Polymorph/issues/129)

## [0.0.16-1.18.1] - 2021.12.23
### Changed
- Updated to Minecraft 1.18.1
- Updated to CCA 4.0.1
### Removed
- Removed Fabric Furnaces and Improved Stations compatibility as they are not currently available on 1.18.1

## [0.0.15-1.17.1] - 2021.12.22
### Added
- Added Tom's Storage Crafting Terminal compatibility
- Added Recipe Cache compatibility [#96](https://github.com/TheIllusiveC4/Polymorph/issues/96)
- Added `polymorph-integrations.toml` configuration file to turn off/on any mod compatibility (in case any module is
  either bugged or undesired)
### Changed
- Rewrote the entire mod again, fixing many old issues but be aware of new issues or old resolved issue that may resurface
- Improved `/polymorph conflicts` command to more accurately catch conflicts, output easier-to-read results, and include
  recipes for smelting and smithing
### Fixed
- Fixed Polymorph button vanishing when loaded with Consular's Origins [#95](https://github.com/TheIllusiveC4/Polymorph/issues/95)

## [0.0.14-1.17.1] - 2021.09.28
### Added
- Added REI display bounds to recipe selection GUI [#93](https://github.com/TheIllusiveC4/Polymorph/issues/93)
### Fixed
- Fixed furnace recipe toggles not working when loaded with Better Nether [#87](https://github.com/TheIllusiveC4/Polymorph/issues/87)

## [0.0.13-1.17.1] - 2021.09.10
### Fixed
- Fixed recipe toggles showing empty recipes [#89](https://github.com/TheIllusiveC4/Polymorph/issues/89)

## [0.0.12-1.17.1] - 2021.08.26
### Changed
- Updated to Minecraft 1.17.1
### Removed
- Removed Applied Energistics 2 compatibility due to the mod being currently unavailable on 1.17.1

## [0.0.11-1.16.5] - 2021.08.25
### Added
- Added Smithing Table compatibility
- Added Applied Energistics 2 Crafting Terminal and Pattern Terminal compatibility
- Added Italian localization (thanks simcrafter!)
### Changed
- Rewrote the entire mod, fixing many old issues but be aware of new issues or old resolved issue that may resurface
- Recipe selection preference now persists across multiple screens

## [0.0.10-1.16.5] - 2021.02.24
### Fixed
- Fixed recipe output not respecting Origins: Classes changes [#67](https://github.com/TheIllusiveC4/Polymorph/issues/67)

## [0.0.9-1.16.5] - 2021.01.31
### Added
- Added Korean translation (thanks othuntgithub!)

## [0.0.8-1.16.4] - 2021.01.01
### Fixed
- Fixed duplicate recipe outputs [#51](https://github.com/TheIllusiveC4/Polymorph/issues/51)

## [0.0.7-1.16.4] - 2020.12.25
### Fixed
- Fixed FastFurnace integration to prevent potential crashes

## [0.0.6-1.16.4] - 2020.12.17
### Added
- Added automatic integration with the majority of modded crafting tables
- Added smelting recipe conflict management for the Furnace, Smoker, and the Blast Furnace
- Added smelting recipe conflict integration for FastFurnace, Iron Furnaces, and Fabric Furnaces
- Added French, Croatian, and Russian localization
- Added REI integration to automatically select the recipe that players clicked on for transferring
ingredients
### Changed
- Updated to Minecraft 1.16.4

## [0.0.5-1.16.3] - 2020.09.17
### Changed
- Updated to Minecraft 1.16.3
### Fixed
- Fixed recipes breaking when worlds update mods

## [0.0.4-1.16.1] - 2020.08.01
### Added
- Added Chinese localization (thanks Samekichi!)

## [0.0.3-1.16.1] - 2020.07.31
### Fixed
- Fixed modded handlers for crafting screens by introducing fallbacks server-side

## [0.0.2-1.16.1] - 2020.07.24
### Fixed
- Fixed mouse clicks being disabled on non-crafting screens

## [0.0.1-1.16.1] - 2020.07.24
Initial beta release
