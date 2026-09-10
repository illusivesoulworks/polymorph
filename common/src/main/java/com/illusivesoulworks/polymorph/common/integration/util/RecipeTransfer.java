package com.illusivesoulworks.polymorph.common.integration.util;

import com.illusivesoulworks.polymorph.api.client.PolymorphWidgets;
import com.illusivesoulworks.polymorph.api.client.base.IRecipesWidget;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeHolder;

public class RecipeTransfer {

  public static void selectRecipe(RecipeHolder<?> recipe) {
    selectRecipe(recipe.id());
  }

  public static void selectRecipe(ResourceLocation resourceLocation) {
    IRecipesWidget widget = PolymorphWidgets.getInstance().getCurrentWidget();

    if (widget != null) {
      widget.selectRecipe(resourceLocation);
    }
  }
}
