/*
 * Copyright (C) 2020-2022 Illusive Soulworks
 *
 * Polymorph is free software: you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published
 * by the Free Software Foundation, either version 3 of the License, or
 * any later version.
 *
 * Polymorph is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with Polymorph.  If not, see <https://www.gnu.org/licenses/>.
 */

package com.illusivesoulworks.polymorph.common.integration;

import com.illusivesoulworks.polymorph.PolymorphConstants;
import com.illusivesoulworks.polymorph.client.recipe.RecipesWidget;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.recipe.transfer.IRecipeTransferContext;
import mezz.jei.api.recipe.transfer.IRecipeTransferListener;
import mezz.jei.api.recipe.transfer.RecipeTransferResult;
import mezz.jei.api.registration.IRecipeTransferRegistration;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Recipe;

@JeiPlugin
public final class PolymorphJeiPlugin implements IModPlugin {

  @Override
  public ResourceLocation getPluginUid() {
    return new ResourceLocation(PolymorphConstants.MOD_ID, "recipe_transfer");
  }

  @Override
  public void registerRecipeTransferHandlers(IRecipeTransferRegistration registration) {
    registration.addRecipeTransferListener(new PolymorphRecipeTransferListener());
  }

  private static final class PolymorphRecipeTransferListener
      implements IRecipeTransferListener {

    @Override
    public void afterRecipeTransfer(IRecipeTransferContext<?, ?> context,
                                    RecipeTransferResult result) {
      if (result == RecipeTransferResult.SUCCESS &&
          context.getRecipe() instanceof Recipe<?> recipe) {
        RecipesWidget.get().ifPresent(widget -> widget.selectRecipe(recipe.getId()));
      }
    }
  }
}
