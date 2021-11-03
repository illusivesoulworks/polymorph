package top.theillusivec4.polymorph.common.integration.prettypipes;

import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.inventory.container.Slot;
import top.theillusivec4.polymorph.client.recipe.widget.PlayerRecipesWidget;

public class CraftingTerminalRecipesWidget extends PlayerRecipesWidget {

  public CraftingTerminalRecipesWidget(ContainerScreen<?> containerScreen, Slot outputSlot) {
    super(containerScreen, outputSlot);
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
