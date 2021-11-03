package top.theillusivec4.polymorph.common.integration.tconstruct;

import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.inventory.container.Slot;
import top.theillusivec4.polymorph.client.recipe.widget.PersistentRecipesWidget;

public class CraftingStationRecipesWidget extends PersistentRecipesWidget {

  private final Slot outputSlot;

  public CraftingStationRecipesWidget(ContainerScreen<?> pContainerScreen, Slot pOutputSlot) {
    super(pContainerScreen);
    this.outputSlot = pOutputSlot;
  }

  @Override
  public Slot getOutputSlot() {
    return this.outputSlot;
  }

  @Override
  public int getYPos() {
    return getOutputSlot().yPos + 8;
  }
}
