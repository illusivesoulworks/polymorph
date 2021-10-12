/*
 * Copyright (c) 2020 C4
 *
 * This file is part of Polymorph, a mod made for Minecraft.
 *
 * Polymorph is free software: you can redistribute it and/or modify it under the terms of the GNU
 * Lesser General Public License as published by the Free Software Foundation, either version 3 of
 * the License, or any later version.
 *
 * Polymorph is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without
 * even the implied warranty of MERCHANTABILITY or FITNESS FOR PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with Polymorph.
 * If not, see <https://www.gnu.org/licenses/>.
 */

package top.theillusivec4.polymorph.mixin;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import net.minecraftforge.fml.loading.FMLLoader;
import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;

public class IntegratedMixinPlugin implements IMixinConfigPlugin {

  private static final Map<String, String> CLASS_TO_MOD = new HashMap<>();

  static {
    CLASS_TO_MOD.put("mezz.jei.transfer.RecipeTransferUtil", "jei");
    CLASS_TO_MOD.put("net.blay09.mods.craftingcraft.container.CraftingContainer", "craftingcraft");
    CLASS_TO_MOD.put("com.tfar.craftingstation.CraftingStationContainer", "craftingstation");
    CLASS_TO_MOD.put("de.ellpeck.prettypipes.terminal.containers.CraftingTerminalContainer", "prettypipes");
    CLASS_TO_MOD.put("com.tom.storagemod.tile.TileEntityCraftingTerminal", "toms_storage");
    CLASS_TO_MOD.put("com.tom.storagemod.gui.ContainerCraftingTerminal", "toms_storage");
    CLASS_TO_MOD.put("com.tom.storagemod.gui.ContainerStorageTerminal", "toms_storage");
    CLASS_TO_MOD.put("shadows.fastbench.gui.ContainerFastBench", "fastbench");
    CLASS_TO_MOD.put("com.lothrazar.storagenetwork.gui.ContainerNetwork", "storagenetwork");
    CLASS_TO_MOD.put("com.refinedmods.refinedstorage.apiimpl.network.node.GridNetworkNode", "refinedstorage");
    CLASS_TO_MOD.put("com.refinedmods.refinedstorage.container.GridContainer", "refinedstorage");
    CLASS_TO_MOD.put("com.refinedmods.refinedstorage.apiimpl.autocrafting.CraftingPatternFactory", "refinedstorage");
    CLASS_TO_MOD.put("com.refinedmods.refinedstorageaddons.item.WirelessCraftingGrid", "refinedstorageaddons");
    CLASS_TO_MOD.put("slimeknights.tconstruct.tables.tileentity.table.CraftingStationTileEntity", "tconstruct");
    CLASS_TO_MOD.put("com.lothrazar.cyclic.item.craftingsimple.CraftingStickContainer", "cyclic");
    CLASS_TO_MOD.put("com.lothrazar.cyclic.item.crafting.CraftingBagContainer", "cyclic");
    CLASS_TO_MOD.put("com.lothrazar.cyclic.block.workbench.ContainerWorkbench", "cyclic");
    CLASS_TO_MOD.put("com.lothrazar.cyclic.block.crafter.TileCrafter", "cyclic");
  }

  @Override
  public void onLoad(String mixinPackage) {

  }

  @Override
  public String getRefMapperConfig() {
    return null;
  }

  @Override
  public boolean shouldApplyMixin(String targetClassName, String mixinClassName) {
    return !CLASS_TO_MOD.containsKey(targetClassName) ||
        FMLLoader.getLoadingModList().getModFileById(CLASS_TO_MOD.get(targetClassName)) != null;
  }

  @Override
  public void acceptTargets(Set<String> myTargets, Set<String> otherTargets) {

  }

  @Override
  public List<String> getMixins() {
    return null;
  }

  @Override
  public void preApply(String targetClassName, ClassNode targetClass, String mixinClassName,
                       IMixinInfo mixinInfo) {

  }

  @Override
  public void postApply(String targetClassName, ClassNode targetClass, String mixinClassName,
                        IMixinInfo mixinInfo) {

  }
}
