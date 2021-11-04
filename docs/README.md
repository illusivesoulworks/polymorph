# Polymorph

Polymorph is a mod that solves recipe conflicts by letting players choose between all potential
outputs shared by the same ingredients.

With a sufficiently large amount of mods, recipe conflicts are a common occurrence and the
responsibility for resolving these usually falls on the user or modpack developer, using datapacks
or other tools to ensure that each recipe is unique.

Polymorph offers an alternative solution, allowing all possible crafting and smelting recipes to
co-exist regardless of conflicts.

## Features

### Crafting

![](https://i.ibb.co/TkWswkG/polymorph.gif)

When a group of ingredients matches more than one recipe, a button will appear above the output
slot. Pushing this button will show a list of all possible results and selecting one will change the
crafting output to match. Polymorph will also remember the last selection as long as the ingredients
don't change, so repeated crafting actions are possible on the same selection.

### Smelting

![](https://i.ibb.co/QX9MNYM/polymorph-furnacedemo.gif)

When a valid input matches more than one output, a button will appear above the output slot. Pushing
this button will show a list of all possible results with the currently selected result highlighted
in green. Selecting one of the listed results will change the smelting output to match. This
selection will be saved to the block itself and persist across world loading and unloading.

### Smithing

![](https://i.ibb.co/GTCgL3S/smithingconflicts.png)

Exactly like the crafting screen, a button will appear above the output slot when a valid input matches more than one
output. Upon selection, the output will change to match.

### Commands

To assist identifying potential conflicts, there's a command `/polymorph conflicts` that will try to identify recipes
that conflict with each other and outputs a list of them to your logs folder. Currently, the command will scan crafting,
smelting, blasting, smoking, and smithing recipes.

### Mod Integration

**Just Enough Items**

Upon selecting a recipe to transfer from the Just Enough Item screen, the output will automatically change to match the
selection if there is more than one possible output.

**Modded Support**

Many crafting tables that are simply variants of the vanilla crafting table will automatically be compatible. Other
types of support may or may not be compatible; below is a list of all mods that Polymorph has specific integration with
and has already been tested for functionality.

- [Applied Energistics 2](https://www.curseforge.com/minecraft/mc-mods/applied-energistics-2)
  - ME Crafting Terminal
  - ME Pattern Terminal
- [CraftingCraft](https://www.curseforge.com/minecraft/mc-mods/craftingcraft)
  - Portable Crafting
  - Inventory Crafting
- [Crafting Station](https://www.curseforge.com/minecraft/mc-mods/crafting-station)
  - Crafting Station
- [Cyclic](https://www.curseforge.com/minecraft/mc-mods/cyclic)
  - Crafter
  - Workbench
- [FastFurnace](https://www.curseforge.com/minecraft/mc-mods/fastfurnace)
  - Furnace
  - Blasting Furnace
  - Smoker
- [FastWorkbench](https://www.curseforge.com/minecraft/mc-mods/fastworkbench)
  - Crafting Table
- [Iron Furnaces](https://www.curseforge.com/minecraft/mc-mods/iron-furnaces)
  - All furnace variants
- [Pretty Pipes](https://www.curseforge.com/minecraft/mc-mods/pretty-pipes)
  - Crafting Terminal
- [Refined Storage](https://www.curseforge.com/minecraft/mc-mods/refined-storage)
  - Crafting Grid
  - Pattern Grid
- [Refined Storage Addons](https://www.curseforge.com/minecraft/mc-mods/refined-storage-addons)
  - Wireless Crafting Grid
- [Simple Storage Network](https://www.curseforge.com/minecraft/mc-mods/simple-storage-network)
  - Storage Request Table
- [Sophisticated Backpacks](https://www.curseforge.com/minecraft/mc-mods/sophisticated-backpacks)
  - Crafting Upgrade
  - Smelting Upgrade
- [Tinkers' Construct](https://www.curseforge.com/minecraft/mc-mods/tinkers-construct)
  - Crafting Station
- [Tom's Storage](https://www.curseforge.com/minecraft/mc-mods/toms-storage)
  - Crafting Terminal

Please request support by opening an issue on the [issue tracker](https://github.com/TheIllusiveC4/Polymorph/issues).

## Downloads

**CurseForge**

[![](http://cf.way2muchnoise.eu/short_polymorph_downloads%20on%20Forge.svg)](https://www.curseforge.com/minecraft/mc-mods/polymorph/files) [![](http://cf.way2muchnoise.eu/short_polymorph-fabric_downloads%20on%20Fabric.svg)](https://www.curseforge.com/minecraft/mc-mods/polymorph-fabric/files)
- [Polymorph for Forge](https://www.curseforge.com/minecraft/mc-mods/polymorph/files)
- [Polymorph for Fabric](https://www.curseforge.com/minecraft/mc-mods/polymorph-fabric/files)

## Developing

**Help! I'm getting Mixin crashes when I try to launch in development on Forge!**

Polymorph uses Mixins to implement its core features. This may cause issues when depending on
Polymorph for your project inside a development environment since ForgeGradle/MixinGradle do not yet
support this natively like on the Fabric toolchain. As a workaround, please disable the refmaps in
development by setting the `mixin.env.disableRefMap` JVM argument to `true` in your run
configuration.

## Support

Please report all bugs, issues, and feature requests to the
[issue tracker](https://github.com/TheIllusiveC4/Polymorph/issues).

For non-technical support and questions, join the developer's [Discord](https://discord.gg/JWgrdwt).

## License

All source code and assets are licensed under LGPL 3.0 or later.

## Donations

Donations to the developer can be sent through [Ko-fi](https://ko-fi.com/C0C1NL4O).

## Affiliates

[![BisectHosting](https://i.ibb.co/1G4QPdc/bh-illusive.png)](https://bisecthosting.com/illusive)
