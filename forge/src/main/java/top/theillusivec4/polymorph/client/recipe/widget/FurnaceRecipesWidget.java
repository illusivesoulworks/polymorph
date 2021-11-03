package top.theillusivec4.polymorph.client.recipe.widget;

import java.util.List;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.inventory.container.Slot;

public class FurnaceRecipesWidget extends PersistentRecipesWidget {

  private final Slot outputSlot;

  public FurnaceRecipesWidget(ContainerScreen<?> pContainerScreen) {
    super(pContainerScreen);
    List<Slot> slots = pContainerScreen.getContainer().inventorySlots;
    this.outputSlot = slots.get(2);
  }

  @Override
  public Slot getOutputSlot() {
    return this.outputSlot;
  }
}
