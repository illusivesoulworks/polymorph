# Changelog
All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](http://keepachangelog.com/en/1.0.0/) and this project adheres to [Semantic Versioning](http://semver.org/spec/v2.0.0.html).
Prior to version 0.45.0, this project used MCVERSION-MAJORMOD.MAJORAPI.MINOR.PATCH.

## [0.46.2+1.19.2] - 2023.06.30
### Added
- Added `pt_br` localization (thanks FITFC!) [#198](https://github.com/illusivesoulworks/polymorph/pull/198)
- Added `zh_tw` localization (thanks xMikux!) [#207](https://github.com/illusivesoulworks/polymorph/pull/207)
### Changed
- Recipe outputs now prioritize modded recipes as defaults over vanilla recipes [#189](https://github.com/illusivesoulworks/polymorph/issues/189)
### Fixed
- Fixed Polymorph button failing to appear on furnace block entities [#178](https://github.com/illusivesoulworks/polymorph/issues/178)
- Fixed recipes caching invalid outputs [#181](https://github.com/illusivesoulworks/polymorph/issues/181)
- Fixed Polymorph button failing to appear when recipes with different inputs have the same output [#183](https://github.com/illusivesoulworks/polymorph/issues/183)

## [0.46.1+1.19.2] - 2022.11.27
### Fixed
- Fixed network crash [#167](https://github.com/illusivesoulworks/polymorph/issues/167)

## [0.46.0+1.19.2] - 2022.11.13
### Added
- [Forge] Re-added FastSuite, FastWorkbench, and FastFurnace support [#188](https://github.com/illusivesoulworks/polymorph/issues/188)
### Fixed
- Fixed Polymorph not differentiating some outputs by NBT values [#195](https://github.com/illusivesoulworks/polymorph/issues/195)

## [0.45.0+1.19.2] - 2022.08.11
### Changed
- Updated to Minecraft 1.19.2
- [Forge] Updated to Forge 43+
- [Fabric] Updated to Fabric API 0.59.0+

## [0.45.0+1.19.1] - 2022.08.03
### Changed
- Merged Forge and Fabric versions of the project together using the [MultiLoader template](https://github.com/jaredlll08/MultiLoader-Template)
- Changed to [Semantic Versioning](http://semver.org/spec/v2.0.0.html)
- Updated to Minecraft 1.19.1
- [Forge] Updated to Forge 41.0.94+
- [Fabric] Updated to Fabric API 0.55.2+
- [Fabric] Updated to Cardinal Components API 5.0.1+
### Removed
- Removed support for other mods, integration with other mods will be reworked and return at a later date

## [1.18.2-0.44] - 2022.05.15
### Changed
- Updated to Minecraft 1.18.2
- Updated Applied Energistics 2 integration to 11.0.0+ [#170](https://github.com/TheIllusiveC4/Polymorph/issues/170)
- Updated Iron Furnaces integration to 3.3.0+
### Fixed
- Fixed recipe outputs merging items with different NBT elements [#169](https://github.com/TheIllusiveC4/Polymorph/issues/169)

## [1.18.1-0.43] - 2022.04.04
### Fixed
- Fixed NullPointerException error log with FastWorkbench [#163](https://github.com/TheIllusiveC4/Polymorph/issues/163)

## [1.18.1-0.42] - 2022.04.01
### Changed
- Updated Sophisticated Backpacks integration to 1.18.1-3.15.15+ [#162](https://github.com/TheIllusiveC4/Polymorph/issues/162)

## [1.18.1-0.41] - 2022.04.01
### Changed
- Optimized performance issues with block entities again [#161](https://github.com/TheIllusiveC4/Polymorph/issues/161)

## [1.18.1-0.40] - 2022.03.11
### Added
- Re-added Tinkers' Construct integration [#156](https://github.com/TheIllusiveC4/Polymorph/issues/156)

## [1.18.1-0.39] - 2022.02.26
### Fixed
- Fixed NPE crash with Refined Storage integration [#150](https://github.com/TheIllusiveC4/Polymorph/issues/150)

## [1.18.1-0.38] - 2022.02.26
### Added
- Added `uk_ua.json` localization (thanks Sushomeister!)
- Added Extended Crafting Basic Table and Basic Auto Table compatibility [#144](https://github.com/TheIllusiveC4/Polymorph/issues/144)
### Changed
- Optimized performance issues with block entities [#149](https://github.com/TheIllusiveC4/Polymorph/issues/149)

## [1.18.1-0.37] - 2022.02.07
### Changed
- Updated to Sophisticated Backpacks 1.18.1-3.15.9+ [#146](https://github.com/TheIllusiveC4/Polymorph/issues/146)

## [1.18.1-0.36] - 2022.01.13
### Added
- Added Occultism Storage Actuator compatibility [#132](https://github.com/TheIllusiveC4/Polymorph/issues/132)

## [1.18.1-0.35] - 2022.01.02
### Changed
- Updated to Applied Energistics 2 10.0.0-beta.3 [#129](https://github.com/TheIllusiveC4/Polymorph/issues/129)

## [1.18.1-0.34] - 2022.01.01
### Changed
- Updated to Sophisticated Backpacks 1.18.1-3.15.0+ [#127](https://github.com/TheIllusiveC4/Polymorph/issues/127)
### Fixed
- Fixed NPE furnace crash [#122](https://github.com/TheIllusiveC4/Polymorph/issues/122)
- Fixed NPE from JEI crash [#124](https://github.com/TheIllusiveC4/Polymorph/issues/124)

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

## [1.16.5-0.24] - 2021.01.31
### Added
- Added Korean translation (thanks othuntgithub!)

## [1.16.4-0.23] - 2021.01.17
### Fixed
- Fixed issue with empty pattern outputs in Refined Storage [#56](https://github.com/TheIllusiveC4/Polymorph/issues/56)

## [1.16.4-0.22] - 2021.01.01
### Fixed
- Fixed duplicate recipe outputs [#51](https://github.com/TheIllusiveC4/Polymorph/issues/51)

## [1.16.4-0.21] - 2020.12.25
### Fixed
- Attempted fix for empty recipe outputs [#49](https://github.com/TheIllusiveC4/Polymorph/issues/49)

## [1.16.4-0.20] - 2020.12.20
### Fixed
- Attempted fix for IllegalArgumentException crash with Create [#45](https://github.com/TheIllusiveC4/Polymorph/issues/45)

## [1.16.4-0.19] - 2020.12.17
### Added
- Added smelting recipe conflict management for the Furnace, Smoker, and the Blast Furnace
- Added smelting recipe conflict integration for FastFurnace and Iron Furnaces
### Changed
- JEI integration uses recipe identifiers now instead of crafting outputs
### Fixed
- Fixed Pretty Pipes recipe toggle button overlapping with crafting grid [#40](https://github.com/TheIllusiveC4/Polymorph/issues/40)

## [1.16.4-0.18] - 2020.11.27
### Fixed
- Fixed dedicated server crash [#38](https://github.com/TheIllusiveC4/Polymorph/issues/38)

## [1.16.4-0.17] - 2020.11.27
### Changed
- Added automatic integration with the majority of modded crafting tables

## [1.16.3-0.16] - 2020.10.26
### Added
- Added French localization (thanks Lykrast!)
### Fixed
- Fixed crash with BYG [#32](https://github.com/TheIllusiveC4/Polymorph/issues/32)

## [1.16.3-0.15] - 2020.09.29
### Changed
- Updated to Minecraft 1.16.3
- Added Croatian localization (thanks COMBOhrenovke!)

## [1.16.2-0.14] - 2020.09.01
### Changed
- Updated to Minecraft 1.16.2

## [1.16.1-0.13] - 2020.08.13
### Fixed
- Attempted fix for recipes breaking when updating worlds [#24](https://github.com/TheIllusiveC4/Polymorph/issues/24)

## [1.16.1-0.12] - 2020.08.01
### Added
- Added Chinese localization (thanks Samekichi!)

## [1.16.1-0.11] - 2020.07.22
### Fixed
- Fixed Oh The Biomes You'll Go crafting tables not display outputs correctly

## [1.16.1-0.10] - 2020.07.19
### Changed
- Port to 1.16.1 Forge

## [1.15.2-0.9] - 2020.07.19
### Fixed
- Removed debug line of code

## [1.15.2-0.8] - 2020.07.19
### Added
- Added Refined Storage integration [#7](https://github.com/TheIllusiveC4/Polymorph/issues/7)
- Added CraftingCraft integration [#13](https://github.com/TheIllusiveC4/Polymorph/issues/13)

## [1.15.2-0.7] - 2020.07.15
### Fixed
- Fixed not being able to select recipe outputs for FastWorkbench [#14](https://github.com/TheIllusiveC4/Polymorph/issues/14)

## [1.15.2-0.6] - 2020.07.12
### Fixed
- Fixed dedicated server crash

## [1.15.2-0.5] - 2020.07.11
### Fixed
- Removed debug line of code

## [1.15.2-0.4] - 2020.07.10
### Fixed
- Attempted fix for potential race condition in fetching recipes [#10](https://github.com/TheIllusiveC4/Polymorph/issues/10)
- Attempted fix for Simple Storage Network integration crash [#11](https://github.com/TheIllusiveC4/Polymorph/issues/11)

## [1.15.2-0.3] - 2020.06.21
### Added
- Added Silent Gear Crafting Station support [#5](https://github.com/TheIllusiveC4/Polymorph/issues/5)
- Added Simple Storage Network Storage Request Table and Crafting Remote support [#6](https://github.com/TheIllusiveC4/Polymorph/issues/6)
- Added JEI support so that transferring recipes will keep the selected output
### Changed
- Added error handling to recipe fetching, which prevents crashes and helps diagnose [#3](https://github.com/TheIllusiveC4/Polymorph/issues/3)

## [1.15.2-0.2] - 2020.06.16
### Fixed
- Fixed default crafting outputs being used when shift-clicking using FastWorkbench [#2](https://github.com/TheIllusiveC4/Polymorph/issues/2)
- Fixed duplication bug when shift-clicking single quantity crafting outputs [#1](https://github.com/TheIllusiveC4/Polymorph/issues/1)

## [1.15.2-0.1] - 2020.06.13
- Initial beta release
