package top.theillusivec4.polymorph.common.integration.extendedcrafting;

import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.inventory.container.Slot;
import top.theillusivec4.polymorph.client.recipe.widget.PersistentRecipesWidget;

public class AutoTableRecipesWidget extends PersistentRecipesWidget {

  private final Slot outputSlot;

  public AutoTableRecipesWidget(ContainerScreen<?> screen, Slot outputSlot) {
    super(screen);
    this.outputSlot = outputSlot;
  }

  @Override
  public Slot getOutputSlot() {
    return this.outputSlot;
  }

  @Override
  public int getXPos() {
    return super.getXPos() + 22;
  }

  @Override
  public int getYPos() {
    return this.getOutputSlot().yPos;
  }
}
