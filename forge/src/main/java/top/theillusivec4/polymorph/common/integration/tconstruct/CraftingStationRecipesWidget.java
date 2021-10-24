package top.theillusivec4.polymorph.common.integration.tconstruct;

import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.inventory.container.Slot;
import top.theillusivec4.polymorph.client.recipe.widget.CraftingRecipesWidget;

public class CraftingStationRecipesWidget extends CraftingRecipesWidget {

  public CraftingStationRecipesWidget(ContainerScreen<?> containerScreen,
                                      CraftingInventory craftingInventory, Slot outputSlot) {
    super(containerScreen, craftingInventory, outputSlot);
  }

  @Override
  public int getYPos() {
    return getOutputSlot().yPos + 8;
  }
}
