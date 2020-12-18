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

package top.theillusivec4.polymorph.loader.mixin.core;

import java.util.Optional;
import net.minecraft.inventory.Inventory;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeManager;
import net.minecraft.recipe.RecipeType;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import top.theillusivec4.polymorph.loader.common.MixinHooks;

@Mixin(value = RecipeManager.class, priority = 1200)
public class RecipeManagerMixin {

  @Inject(at = @At("HEAD"), method = "getFirstMatch(Lnet/minecraft/recipe/RecipeType;Lnet/minecraft/inventory/Inventory;Lnet/minecraft/world/World;)Ljava/util/Optional;", cancellable = true)
  private <C extends Inventory, T extends Recipe<C>> void _polymorph_getRecipe(
      RecipeType<T> recipeTypeIn, C inventoryIn, World worldIn,
      CallbackInfoReturnable<Optional<T>> cb) {
    MixinHooks.getSelectedRecipe(recipeTypeIn, inventoryIn, worldIn)
        .ifPresent(recipe -> cb.setReturnValue(Optional.of(recipe)));
  }
}
