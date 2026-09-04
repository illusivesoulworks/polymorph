package com.illusivesoulworks.polymorph.common.integration;

import com.illusivesoulworks.polymorph.common.integration.util.RecipeTransfer;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.recipe.transfer.IRecipeTransferContext;
import mezz.jei.api.recipe.transfer.IRecipeTransferListener;
import mezz.jei.api.recipe.transfer.RecipeTransferResult;
import mezz.jei.api.registration.IRecipeTransferRegistration;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeHolder;

@JeiPlugin
public final class PolymorphJeiPlugin implements IModPlugin {

  @Override
  public ResourceLocation getPluginUid() {
    return ResourceLocation.fromNamespaceAndPath("polymorph", "recipe_transfer");
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
          context.getRecipe() instanceof RecipeHolder<?> recipeHolder) {
        RecipeTransfer.selectRecipe(recipeHolder.id());
      }
    }
  }
}
