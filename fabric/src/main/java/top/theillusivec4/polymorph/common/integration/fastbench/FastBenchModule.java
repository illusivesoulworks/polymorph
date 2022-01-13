package top.theillusivec4.polymorph.common.integration.fastbench;

import net.minecraft.inventory.CraftingResultInventory;
import net.minecraft.recipe.CraftingRecipe;
import net.minecraft.recipe.Recipe;
import net.minecraft.screen.CraftingScreenHandler;
import net.minecraft.screen.PlayerScreenHandler;
import net.minecraft.screen.ScreenHandler;
import top.theillusivec4.polymorph.common.integration.AbstractCompatibilityModule;
import top.theillusivec4.polymorph.mixin.core.AccessorCraftingScreenHandler;
import top.theillusivec4.polymorph.mixin.core.AccessorPlayerScreenHandler;

public class FastBenchModule extends AbstractCompatibilityModule {

  @Override
  public boolean selectRecipe(ScreenHandler screenHandler, Recipe<?> recipe) {

    if (recipe instanceof CraftingRecipe) {
      CraftingResultInventory result = null;

      if (screenHandler instanceof CraftingScreenHandler) {
        AccessorCraftingScreenHandler accessor = (AccessorCraftingScreenHandler) screenHandler;
        result = accessor.getResult();
      } else if (screenHandler instanceof PlayerScreenHandler) {
        AccessorPlayerScreenHandler accessor = (AccessorPlayerScreenHandler) screenHandler;
        result = accessor.getCraftingResult();
      }

      if (result != null) {
        result.setLastRecipe(recipe);
      }
    }
    return false;
  }
}