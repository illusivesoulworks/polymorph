# Changelog
All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](http://keepachangelog.com/en/1.0.0/) and this project adheres to the format [MCVERSION-MAJORMOD.MAJORAPI.MINOR.PATCH](https://mcforge.readthedocs.io/en/1.15.x/conventions/versioning/).

## [1.16.5-0.36] - 2021.12.16
### Fixed
- Fixed freezes with Applied Energistics 2 pattern terminal [#114](https://github.com/TheIllusiveC4/Polymorph/issues/114)

## [1.16.5-0.35] - 2021.12.15
### Fixed
- Fixed ConcurrentModificationException [#118](https://github.com/TheIllusiveC4/Polymorph/issues/118)

## [1.16.5-0.34] - 2021.12.15
### Fixed
- Fixed dedicated server lag [#117](https://github.com/TheIllusiveC4/Polymorph/issues/117)
- Fixed freezes with Applied Energistics 2 pattern terminal [#114](https://github.com/TheIllusiveC4/Polymorph/issues/114)
- Fixed freezes with loading Refined Storage integration

## [1.16.5-0.33] - 2021.11.23
### Changed
- Polymorph now only supports FastWorkbench 4.6.0+ and FastFurnace 4.5.0+
### Fixed
- Updated FastWorkbench and FastFurnace compatibility to fix crashes [#109](https://github.com/TheIllusiveC4/Polymorph/issues/109)

## [1.16.5-0.32] - 2021.11.16
### Fixed
- Fixed potential overflow errors when using certain modded furnaces [#108](https://github.com/TheIllusiveC4/Polymorph/issues/108)

## [1.16.5-0.31] - 2021.11.14
### Added
- Added Sophisticated Backpack's auto-smelting upgrade compatibility [#105](https://github.com/TheIllusiveC4/Polymorph/issues/105)
### Fixed
- Fixed potential fallback errors so that recipes can still operate normally even if the Polymorph logic fails

## [1.16.5-0.30] - 2021.11.12
### Changed
- Improved recipe output equality logic
- Recipe outputs will now display the stack size

## [1.16.5-0.29] - 2021.11.10
### Added
- Added Fast Workbench Minus Replacement compatibility [#104](https://github.com/TheIllusiveC4/Polymorph/issues/104)
- Added Fast Furnace Minus Replacement compatibility

## [1.16.5-0.28] - 2021.11.07
### Fixed
- Fixed potential server hang with Refined Storage [#102](https://github.com/TheIllusiveC4/Polymorph/issues/102)

## [1.16.5-0.27] - 2021.11.06
### Fixed
- Attempted fix for CME crash [#101](https://github.com/TheIllusiveC4/Polymorph/issues/101)
- Fixed AE2 Pattern Terminal toggle recipe button rendering over processing slots if switched over from crafting

## [1.16.5-0.26] - 2021.11.03
### Added
- Added Italian localization (thanks simcrafter!)
- Added Smithing Table compatibility
- Added Sophisticated Backpacks's crafting upgrade and smelting upgrade compatibility [#55](https://github.com/TheIllusiveC4/Polymorph/issues/55)
- Added Improved Stations's Crafting Station compatibility [#61](https://github.com/TheIllusiveC4/Polymorph/issues/61) [#82](https://github.com/TheIllusiveC4/Polymorph/issues/82)
- Added Cyclic's Crafter compatibility [#70](https://github.com/TheIllusiveC4/Polymorph/issues/70)
- Added Tinkers' Construct's Crafting Station compatibility [#74](https://github.com/TheIllusiveC4/Polymorph/issues/74)
- Added Crafting Station compatibility
- Added Applied Energistics 2's Crafting Terminal and Pattern Terminal compatibility
- Added Refined Storage Pattern Grid compatibility
- Added Refined Storage Addons's Wireless Grid compatibility
- Added Simple Storage Network's Storage Request Table compatibility
- Added Tom's Storage's Crafting Terminal compatibility
- Added `polymorph-integrations.toml` configuration file to turn off/on any mod compatibility (in case any module is
either bugged or undesired)
### Changed
- Rewrote the entire mod, fixing many old issues but be aware of new issues or old resolved issue that may resurface
- Player recipe selection preference now persists across screens and sessions
- Improved `/polymorph conflicts` command to more accurately catch conflicts, output easier-to-read results, and include
recipes for smelting and smithing
### Fixed
- Fixed miscellaneous syncing issues by rewriting the mod to calculate recipes server-side
- Fixed recipe calculations bypassing certain modded furnace logic [#63](https://github.com/TheIllusiveC4/Polymorph/issues/63) [#69](https://github.com/TheIllusiveC4/Polymorph/issues/69)

## [1.16.5-0.25] - 2021.06.15
### Added
- Added Spanish translation (thanks FrannDzs!)
### Fixed
- Fixed duplication exploit with Refined Storage Pattern Grid by temporarily disabling integration [#78](https://github.com/TheIllusiveC4/Polymorph/issues/78)

## [0.24] - 2021.01.31
### Added
- Added Korean translation (thanks othuntgithub!)

## [0.23] - 2021.01.17
### Fixed
- Fixed issue with empty pattern outputs in Refined Storage [#56](https://github.com/TheIllusiveC4/Polymorph/issues/56)

## [0.22] - 2021.01.01
### Fixed
- Fixed duplicate recipe outputs [#51](https://github.com/TheIllusiveC4/Polymorph/issues/51)

## [0.21] - 2020.12.25
### Fixed
- Attempted fix for empty recipe outputs [#49](https://github.com/TheIllusiveC4/Polymorph/issues/49)

## [0.20] - 2020.12.20
### Fixed
- Attempted fix for IllegalArgumentException crash with Create [#45](https://github.com/TheIllusiveC4/Polymorph/issues/45)

## [0.19] - 2020.12.17
### Added
- Added smelting recipe conflict management for the Furnace, Smoker, and the Blast Furnace
- Added smelting recipe conflict integration for FastFurnace and Iron Furnaces
### Changed
- JEI integration uses recipe identifiers now instead of crafting outputs
### Fixed
- Fixed Pretty Pipes recipe toggle button overlapping with crafting grid [#40](https://github.com/TheIllusiveC4/Polymorph/issues/40)

## [0.18] - 2020.11.27
### Fixed
- Fixed dedicated server crash [#38](https://github.com/TheIllusiveC4/Polymorph/issues/38)

## [0.17] - 2020.11.27
### Changed
- Added automatic integration with the majority of modded crafting tables

## [0.16] - 2020.10.26
### Added
- Added French localization (thanks Lykrast!)
### Fixed
- Fixed crash with BYG [#32](https://github.com/TheIllusiveC4/Polymorph/issues/32)

## [0.15] - 2020.09.29
### Changed
- Updated to Minecraft 1.16.3
- Added Croatian localization (thanks COMBOhrenovke!)

## [0.14] - 2020.09.01
### Changed
- Updated to Minecraft 1.16.2

## [0.13] - 2020.08.13
### Fixed
- Attempted fix for recipes breaking when updating worlds [#24](https://github.com/TheIllusiveC4/Polymorph/issues/24)

## [0.12] - 2020.08.01
### Added
- Added Chinese localization (thanks Samekichi!)

## [0.11] - 2020.07.22
### Fixed
- Fixed Oh The Biomes You'll Go crafting tables not display outputs correctly

## [0.10] - 2020.07.19
### Changed
- Port to 1.16.1 Forge
