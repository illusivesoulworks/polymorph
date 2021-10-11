package top.theillusivec4.polymorph.common.util;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.item.crafting.IRecipe;

public class RecipeCache {

  private final List<IRecipe<?>> lastRecipes;

  public RecipeCache() {
    this.lastRecipes = new ArrayList<>();
  }

  public List<IRecipe<?>> getLastRecipes() {
    return this.lastRecipes;
  }

  public void setLastRecipes(List<IRecipe<?>> recipes) {
    this.lastRecipes.clear();
    this.lastRecipes.addAll(recipes);
  }
}
