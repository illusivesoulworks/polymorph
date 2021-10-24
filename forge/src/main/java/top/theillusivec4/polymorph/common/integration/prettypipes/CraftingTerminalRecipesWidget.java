package top.theillusivec4.polymorph.common.integration.prettypipes;

import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.inventory.container.Slot;
import top.theillusivec4.polymorph.client.recipe.widget.CraftingRecipesWidget;

public class CraftingTerminalRecipesWidget extends CraftingRecipesWidget {

  public CraftingTerminalRecipesWidget(ContainerScreen<?> containerScreen,
                                       CraftingInventory craftingInventory, Slot outputSlot) {
    super(containerScreen, craftingInventory, outputSlot);
  }

  @Override
  public int getXPos() {
    return getOutputSlot().xPos - 22;
  }

  @Override
  public int getYPos() {
    return getOutputSlot().yPos;
  }
}
