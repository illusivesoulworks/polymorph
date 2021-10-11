package top.theillusivec4.polymorph.common.integration.craftingstation;

import com.tfar.craftingstation.CraftingStationContainer;
import net.minecraft.inventory.container.Container;
import net.minecraft.item.crafting.IRecipe;
import top.theillusivec4.polymorph.common.integration.AbstractCompatibilityModule;

public class CraftingStationModule extends AbstractCompatibilityModule {

  @Override
  public boolean setRecipe(Container container, IRecipe<?> recipe) {

    if (container instanceof CraftingStationContainer) {
      ((CraftingStationContainer) container).lastRecipe = null;
      return true;
    }
    return false;
  }
}
