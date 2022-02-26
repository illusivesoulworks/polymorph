package top.theillusivec4.polymorph.common.integration.extendedcrafting;

import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.world.inventory.Slot;
import top.theillusivec4.polymorph.client.recipe.widget.PersistentRecipesWidget;

public class AutoTableRecipesWidget extends PersistentRecipesWidget {

  private final Slot outputSlot;

  public AutoTableRecipesWidget(AbstractContainerScreen<?> screen, Slot outputSlot) {
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
    return this.getOutputSlot().y;
  }
}
