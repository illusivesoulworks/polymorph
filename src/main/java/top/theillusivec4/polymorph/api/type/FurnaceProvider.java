package top.theillusivec4.polymorph.api.type;

import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.inventory.Inventory;
import net.minecraft.recipe.AbstractCookingRecipe;
import top.theillusivec4.polymorph.api.PolymorphApi;

public interface FurnaceProvider extends PolyProvider<Inventory, AbstractCookingRecipe> {

  default RecipeSelector<Inventory, AbstractCookingRecipe> createSelector(
      HandledScreen<?> screen) {
    return PolymorphApi.getInstance().createFurnaceSelector(screen, this);
  }
}
