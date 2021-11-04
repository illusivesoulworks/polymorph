/*
 * Copyright (C) 2020-2021 C4
 *
 * This file is part of Polymorph.
 *
 * Polymorph is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Polymorph is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * and the GNU Lesser General Public License along with Polymorph.
 * If not, see <https://www.gnu.org/licenses/>.
 *
 */

package top.theillusivec4.polymorph.mixin.integration.sophisticatedbackpacks;

import java.util.Optional;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.inventory.ResultContainer;
import net.minecraft.world.inventory.ResultSlot;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.p3pp3rf1y.sophisticatedbackpacks.common.gui.UpgradeContainerBase;
import net.p3pp3rf1y.sophisticatedbackpacks.common.gui.UpgradeContainerType;
import net.p3pp3rf1y.sophisticatedbackpacks.upgrades.crafting.CraftingUpgradeContainer;
import net.p3pp3rf1y.sophisticatedbackpacks.upgrades.crafting.CraftingUpgradeWrapper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import top.theillusivec4.polymorph.common.crafting.RecipeSelection;
import top.theillusivec4.polymorph.common.integration.sophisticatedbackpacks.SophisticatedBackpacksModule;

@SuppressWarnings("unused")
@Mixin(CraftingUpgradeContainer.class)
public abstract class MixinCraftingUpgradeContainer extends
    UpgradeContainerBase<CraftingUpgradeWrapper, CraftingUpgradeContainer> {

  @Shadow(remap = false)
  private CraftingRecipe lastRecipe;

  protected MixinCraftingUpgradeContainer(Player player, int upgradeContainerId,
                                          CraftingUpgradeWrapper upgradeWrapper,
                                          UpgradeContainerType<CraftingUpgradeWrapper, CraftingUpgradeContainer> type) {
    super(player, upgradeContainerId, upgradeWrapper, type);
  }

  @Inject(
      at = @At(value = "HEAD"),
      method = "updateCraftingResult",
      remap = false)
  private void polymorph$updateCraftingResult(Level world, Player player,
                                              CraftingContainer inventory,
                                              ResultContainer inventoryResult,
                                              ResultSlot craftingResultSlot,
                                              CallbackInfo ci) {
    SophisticatedBackpacksModule.updateCraftingResult(world, this.lastRecipe, inventory,
        player, this.getUpgradeStack());
  }

  @Redirect(
      at = @At(
          value = "INVOKE",
          target = "net/minecraft/world/item/crafting/RecipeManager.getRecipeFor(Lnet/minecraft/world/item/crafting/RecipeType;Lnet/minecraft/world/Container;Lnet/minecraft/world/level/Level;)Ljava/util/Optional;"),
      method = "updateCraftingResult")
  private <C extends Container, T extends Recipe<C>> Optional<T> polymorph$getRecipe(
      RecipeManager recipeManager, RecipeType<T> type, C inventory, Level world,
      Level unused, Player player, CraftingContainer unused2,
      ResultContainer inventoryResult, ResultSlot craftingResultSlot) {
    return RecipeSelection.getStackRecipe(type, inventory, world, this.getUpgradeStack());
  }
}
