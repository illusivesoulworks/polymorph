package top.theillusivec4.polymorph.common.integration.occultism;

import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.world.inventory.Slot;
import top.theillusivec4.polymorph.client.recipe.widget.PlayerRecipesWidget;

public class StorageControllerWidget extends PlayerRecipesWidget {

  public StorageControllerWidget(AbstractContainerScreen<?> containerScreen, Slot outputSlot) {
    super(containerScreen, outputSlot);
  }

  @Override
  public int getYPos() {
    return this.getOutputSlot().y + 22;
  }
}
