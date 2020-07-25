# Polymorph [![](http://cf.way2muchnoise.eu/versions/polymorph-fabric.svg)](https://www.curseforge.com/minecraft/mc-mods/polymorph-fabric) [![](http://cf.way2muchnoise.eu/short_polymorph-fabric_downloads.svg)](https://www.curseforge.com/minecraft/mc-mods/polymorph-fabric/files) [![License: LGPL v3](https://img.shields.io/badge/License-LGPL%20v3-blue.svg?&style=flat-square)](https://www.gnu.org/licenses/lgpl-3.0) [![Discord](https://img.shields.io/discord/500852157503766538.svg?color=green&label=Discord&style=flat-square)](https://discord.gg/JWgrdwt) [![ko-fi](https://img.shields.io/badge/Support%20Me-Ko--fi-%23FF5E5B?style=flat-square)](https://ko-fi.com/C0C1NL4O)

## Overview

Polymorph is a mod that solves recipe conflicts by letting players choose between all potential outputs shared by the same ingredients. With a sufficiently large amount of mods, recipe conflicts are a common occurrence and the responsibility for resolving these usually falls on the user or modpack developer, using datapacks or other tools to ensure that each recipe is unique. Polymorph offers an alternative solution, allowing all possible crafting recipes to co-exist regardless of conflicts.

When a group of ingredients matches more than one recipe, a button will appear above the output slot. Pushing this button will show a list of all possible results and selecting one will change the crafting output to match. Polymorph will also remember the last selection as long as the ingredients don't change, so repeated crafting actions are possible on the same selection.

![](https://i.ibb.co/TkWswkG/polymorph.gif)

To assist identifying potential conflicts, there's a command `/polymorph conflicts` that will try to identify recipes that conflict with each other and outputs a list of them to your logs folder.

Currently, only the vanilla crafting table, the vanilla player crafting grid, and a select number of modded crafting tables are supported. Please open an issue on the issue tracker if you'd like to request support for specific additional mods.

Requires [Fabric API](https://www.curseforge.com/minecraft/mc-mods/fabric-api).
