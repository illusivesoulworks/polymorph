package top.theillusivec4.polymorph.common.integration.origins;

import java.util.List;
import java.util.Optional;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.recipe.Recipe;
import net.minecraft.screen.CraftingScreenHandler;
import net.minecraft.util.Identifier;
import top.theillusivec4.polymorph.api.common.component.PlayerRecipeData;
import top.theillusivec4.polymorph.api.common.event.PolymorphRecipeEvents;
import top.theillusivec4.polymorph.common.integration.AbstractCompatibilityModule;

public class OriginsModule extends AbstractCompatibilityModule {

  private static final Identifier MODIFIED_RECIPE = new Identifier("apoli:modified");

  @Override
  public void setup() {
    PolymorphRecipeEvents.FETCH_RECIPES.register((recipeData, recipes) -> {
      if (recipeData instanceof PlayerRecipeData && recipeData.getOwner() instanceof PlayerEntity &&
          ((PlayerRecipeData) recipeData).getScreenHandler() instanceof CraftingScreenHandler) {
        getModifiedRecipe(recipes).ifPresent(recipe -> {
          recipes.clear();
          recipes.add(recipe);
        });
      }
    });
  }

  private static Optional<Recipe<?>> getModifiedRecipe(List<Recipe<?>> recipes) {

    for (Recipe<?> recipe : recipes) {

      if (recipe.getId().equals(MODIFIED_RECIPE)) {
        return Optional.of(recipe);
      }
    }
    return Optional.empty();
  }
}
