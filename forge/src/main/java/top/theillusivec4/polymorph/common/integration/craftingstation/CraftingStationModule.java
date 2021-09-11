package top.theillusivec4.polymorph.common.integration.craftingstation;

import com.tfar.craftingstation.CraftingStationContainer;
import net.minecraft.inventory.container.Container;

public class CraftingStationModule {

  public static void clearRecipe(Container container) {

    if (container instanceof CraftingStationContainer) {
      ((CraftingStationContainer) container).lastRecipe = null;
    }
  }
}
