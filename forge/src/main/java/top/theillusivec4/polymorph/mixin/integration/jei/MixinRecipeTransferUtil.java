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
import mezz.jei.gui.recipes.RecipeLayout;
import mezz.jei.recipes.RecipeTransferManager;
import mezz.jei.transfer.RecipeTransferUtil;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.container.Container;
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
      method = "transferRecipe(Lmezz/jei/recipes/RecipeTransferManager;Lnet/minecraft/inventory/container/Container;Lmezz/jei/gui/recipes/RecipeLayout;Lnet/minecraft/entity/player/PlayerEntity;Z)Z",
      remap = false)
  private static void polymorph$transferRecipe(RecipeTransferManager recipeTransferManager,
                                               Container container, RecipeLayout<?> recipeLayout,
                                               PlayerEntity player, boolean maxTransfer,
                                               CallbackInfoReturnable<IRecipeTransferError> cb) {
    JeiModule.selectRecipe(recipeLayout.getRecipe());
  }
}
