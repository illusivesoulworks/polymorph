package top.theillusivec4.polymorph.api.common.event;

import java.util.List;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.recipe.Recipe;
import top.theillusivec4.polymorph.api.common.component.RecipeData;

public class PolymorphRecipeEvents {

  public static Event<FetchRecipes> FETCH_RECIPES =
      EventFactory.createArrayBacked(FetchRecipes.class,
          (listeners) -> (recipeData, recipeList) -> {
            for (FetchRecipes listener : listeners) {
              listener.modify(recipeData, recipeList);
            }
          });

  public interface FetchRecipes {

    void modify(RecipeData<?> recipeData, List<Recipe<?>> recipes);
  }
}
