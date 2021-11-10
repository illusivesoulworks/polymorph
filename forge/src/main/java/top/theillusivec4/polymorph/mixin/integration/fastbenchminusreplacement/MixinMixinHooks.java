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

package top.theillusivec4.polymorph.mixin.integration.fastbenchminusreplacement;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.CraftResultInventory;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import tfar.fastbenchminusreplacement.MixinHooks;
import top.theillusivec4.polymorph.common.crafting.RecipeSelection;

@Mixin(MixinHooks.class)
public class MixinMixinHooks {

  @Redirect(
      at = @At(
          value = "INVOKE",
          target = "tfar/fastbenchminusreplacement/MixinHooks.findRecipe(Lnet/minecraft/inventory/CraftingInventory;Lnet/minecraft/world/World;)Lnet/minecraft/item/crafting/IRecipe;"),
      method = "slotChangedCraftingGrid",
      remap = false)
  private static IRecipe<CraftingInventory> polymorph$findRecipe(CraftingInventory inv, World world,
                                                                 World unused1, PlayerEntity player,
                                                                 CraftingInventory unused2,
                                                                 CraftResultInventory result) {
    return RecipeSelection.getPlayerRecipe(IRecipeType.CRAFTING, inv, world, player).orElse(null);
  }
}
