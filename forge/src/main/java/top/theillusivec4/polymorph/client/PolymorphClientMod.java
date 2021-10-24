package top.theillusivec4.polymorph.client;

import net.minecraft.inventory.container.AbstractFurnaceContainer;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.SmithingTableContainer;
import top.theillusivec4.polymorph.client.impl.PolymorphClient;
import top.theillusivec4.polymorph.client.recipe.widget.FurnaceRecipesWidget;
import top.theillusivec4.polymorph.client.recipe.widget.SmithingRecipesWidget;

public class PolymorphClientMod {

  public static void setup() {
    PolymorphClient.get().registerWidget(containerScreen -> {
      Container container = containerScreen.getContainer();

      if (container instanceof SmithingTableContainer) {
        return new SmithingRecipesWidget(containerScreen);
      } else if (container instanceof AbstractFurnaceContainer) {
        return new FurnaceRecipesWidget(containerScreen);
      }
      return null;
    });
  }
}
