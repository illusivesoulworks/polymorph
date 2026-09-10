/*
 * Copyright (C) 2020-2022 Illusive Soulworks
 *
 * Polymorph is free software: you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published
 * by the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Polymorph is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with Polymorph.  If not, see <https://www.gnu.org/licenses/>.
 */

package top.theillusivec4.polymorph.common.integration;

import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.recipe.transfer.IRecipeTransferContext;
import mezz.jei.api.recipe.transfer.IRecipeTransferListener;
import mezz.jei.api.recipe.transfer.RecipeTransferResult;
import mezz.jei.api.registration.IRecipeTransferRegistration;
import net.minecraft.recipe.Recipe;
import net.minecraft.util.Identifier;
import top.theillusivec4.polymorph.api.PolymorphApi;
import top.theillusivec4.polymorph.client.recipe.RecipesWidgetControl;

@JeiPlugin
public final class PolymorphJeiPlugin implements IModPlugin {

  @Override
  public Identifier getPluginUid() {
    return new Identifier(PolymorphApi.MOD_ID, "recipe_transfer");
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
        RecipesWidgetControl.get().ifPresent(widget -> widget.selectRecipe(recipe.getId()));
      }
    }
  }
}
