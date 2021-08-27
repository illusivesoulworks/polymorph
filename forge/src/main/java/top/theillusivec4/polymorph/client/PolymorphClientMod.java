package top.theillusivec4.polymorph.client;

import net.minecraft.inventory.container.AbstractFurnaceContainer;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.SmithingTableContainer;
import top.theillusivec4.polymorph.api.PolymorphClientApi;
import top.theillusivec4.polymorph.client.recipe.controller.FurnaceRecipeController;
import top.theillusivec4.polymorph.client.recipe.controller.SmithingRecipeController;

public class PolymorphClientMod {

  public static void setup() {
    PolymorphClientApi.getInstance().addRecipeController(containerScreen -> {
      Container container = containerScreen.getContainer();

      if (container instanceof SmithingTableContainer) {
        return new SmithingRecipeController(containerScreen);
      } else if (container instanceof AbstractFurnaceContainer) {
        return new FurnaceRecipeController(containerScreen);
      }
      return null;
    });
  }
}
