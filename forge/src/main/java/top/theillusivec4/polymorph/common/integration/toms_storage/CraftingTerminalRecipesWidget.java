package top.theillusivec4.polymorph.common.integration.toms_storage;

import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.inventory.container.Slot;
import top.theillusivec4.polymorph.client.recipe.widget.PersistentRecipesWidget;

public class CraftingTerminalRecipesWidget extends PersistentRecipesWidget {

  private final Slot outputSlot;

  public CraftingTerminalRecipesWidget(ContainerScreen<?> containerScreen, Slot outputSlot) {
    super(containerScreen);
    this.outputSlot = outputSlot;
  }

  @Override
  public Slot getOutputSlot() {
    return this.outputSlot;
  }
}
