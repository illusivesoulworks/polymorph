package top.theillusivec4.polymorph.loader.common.integration;

import me.shedaniel.rei.api.RecipeDisplay;
import top.theillusivec4.polymorph.core.client.selector.RecipeSelectorManager;

public class ReiMixinHooks {

  public static void setRecipe(RecipeDisplay recipeDisplay) {
    recipeDisplay.getRecipeLocation().ifPresent(RecipeSelectorManager::setPreferredRecipe);
  }
}
