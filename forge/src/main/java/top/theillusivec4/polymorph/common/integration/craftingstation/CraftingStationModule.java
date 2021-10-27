package top.theillusivec4.polymorph.common.integration.craftingstation;

import com.tfar.craftingstation.CraftingStationContainer;
import com.tfar.craftingstation.client.CraftingStationScreen;
import net.minecraft.inventory.container.Container;
import net.minecraft.item.crafting.ICraftingRecipe;
import net.minecraft.item.crafting.IRecipe;
import top.theillusivec4.polymorph.api.PolymorphApi;
import top.theillusivec4.polymorph.client.recipe.widget.PlayerRecipesWidget;
import top.theillusivec4.polymorph.common.integration.AbstractCompatibilityModule;

public class CraftingStationModule extends AbstractCompatibilityModule {

  @Override
  public void clientSetup() {
    PolymorphApi.client().registerWidget(containerScreen -> {
      if (containerScreen instanceof CraftingStationScreen &&
          containerScreen.getContainer() instanceof CraftingStationContainer) {
        return new PlayerRecipesWidget(containerScreen, containerScreen.getContainer().getSlot(0));
      }
      return null;
    });
  }

  @Override
  public boolean selectRecipe(Container container, IRecipe<?> recipe) {

    if (container instanceof CraftingStationContainer && recipe instanceof ICraftingRecipe) {
      CraftingStationContainer craftingStationContainer = (CraftingStationContainer) container;
      craftingStationContainer.lastRecipe = (ICraftingRecipe) recipe;
      craftingStationContainer.craftMatrix.onCraftMatrixChanged();
      return true;
    }
    return false;
  }
}
