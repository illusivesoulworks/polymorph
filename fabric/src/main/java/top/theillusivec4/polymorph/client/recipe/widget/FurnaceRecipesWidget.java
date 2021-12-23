package top.theillusivec4.polymorph.client.recipe.widget;

import java.util.List;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.screen.slot.Slot;

public class FurnaceRecipesWidget extends PersistentRecipesWidget {

  private final Slot outputSlot;

  public FurnaceRecipesWidget(HandledScreen<?> pHandledScreen) {
    super(pHandledScreen);
    List<Slot> slots = pHandledScreen.getScreenHandler().slots;
    this.outputSlot = slots.get(2);
  }

  @Override
  public Slot getOutputSlot() {
    return this.outputSlot;
  }
}
