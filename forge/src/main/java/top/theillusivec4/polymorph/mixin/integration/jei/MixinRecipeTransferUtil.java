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

package top.theillusivec4.polymorph.mixin.integration.jei;

import mezz.jei.api.recipe.transfer.IRecipeTransferError;
import mezz.jei.common.gui.recipes.layout.IRecipeLayoutInternal;
import mezz.jei.common.recipes.RecipeTransferManager;
import mezz.jei.common.transfer.RecipeTransferUtil;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import top.theillusivec4.polymorph.common.integration.jei.JeiModule;

@SuppressWarnings("unused")
@Mixin(RecipeTransferUtil.class)
public class MixinRecipeTransferUtil {

  @Inject(
      at = @At("HEAD"),
      method = "transferRecipe(Lmezz/jei/common/recipes/RecipeTransferManager;Lnet/minecraft/world/inventory/AbstractContainerMenu;Lmezz/jei/common/gui/recipes/layout/IRecipeLayoutInternal;Lnet/minecraft/world/entity/player/Player;ZZ)Lmezz/jei/api/recipe/transfer/IRecipeTransferError;",
      remap = false)
  private static <C extends AbstractContainerMenu, R> void polymorph$transferRecipe(
      RecipeTransferManager recipeTransferManager, C container,
      IRecipeLayoutInternal<R> recipeLayout, Player player, boolean maxTransfer, boolean doTransfer,
      CallbackInfoReturnable<@Nullable IRecipeTransferError> cir) {
    JeiModule.selectRecipe(recipeLayout.getRecipe());
  }
}
