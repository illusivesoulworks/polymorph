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

package top.theillusivec4.polymorph.mixin.integration.refinedstorageaddons;

import com.refinedmods.refinedstorage.api.network.grid.ICraftingGridListener;
import com.refinedmods.refinedstorageaddons.item.WirelessCraftingGrid;
import java.util.Optional;
import java.util.Set;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.item.crafting.RecipeManager;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import top.theillusivec4.polymorph.common.integration.refinedstorage.RefinedStorageModule;

@SuppressWarnings("unused")
@Mixin(WirelessCraftingGrid.class)
public class MixinWirelessCraftingGrid {

  @Shadow(remap = false)
  @SuppressWarnings("MismatchedQueryAndUpdateOfCollection")
  private Set<ICraftingGridListener> listeners;

  @Redirect(
      at = @At(
          value = "INVOKE",
          target = "net/minecraft/item/crafting/RecipeManager.getRecipeFor(Lnet/minecraft/item/crafting/IRecipeType;Lnet/minecraft/inventory/IInventory;Lnet/minecraft/world/World;)Ljava/util/Optional;"),
      method = "onCraftingMatrixChanged")
  private <C extends IInventory, T extends IRecipe<C>> Optional<T> polymorph$getRecipe(
      RecipeManager recipeManager, IRecipeType<T> type, C inventory, World world) {
    return RefinedStorageModule.getWirelessRecipe(recipeManager, type, inventory, world,
        listeners.stream().findFirst().orElse(null));
  }
}
