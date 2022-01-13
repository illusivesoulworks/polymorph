package top.theillusivec4.polymorph.common.integration.occultism;

import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.inventory.container.Slot;
import top.theillusivec4.polymorph.client.recipe.widget.PlayerRecipesWidget;

public class StorageControllerWidget extends PlayerRecipesWidget {

  public StorageControllerWidget(ContainerScreen<?> containerScreen, Slot outputSlot) {
    super(containerScreen, outputSlot);
  }

  @Override
  public int getYPos() {
    return this.getOutputSlot().yPos + 22;
  }
}
