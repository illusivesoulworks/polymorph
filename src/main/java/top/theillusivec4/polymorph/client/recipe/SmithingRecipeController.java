package top.theillusivec4.polymorph.client.recipe;

import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.inventory.Inventory;
import net.minecraft.recipe.SmithingRecipe;
import net.minecraft.screen.slot.Slot;

public class SmithingRecipeController extends AbstractRecipeController<Inventory, SmithingRecipe> {

  final Inventory inventory;
  final Slot outputSlot;

  public SmithingRecipeController(HandledScreen<?> screen) {
    super(screen);
    this.outputSlot = screen.getScreenHandler().slots.get(2);
    this.inventory = screen.getScreenHandler().slots.get(0).inventory;
    this.init();
  }

  @Override
  public Inventory getInventory() {
    return this.inventory;
  }

  @Override
  public Slot getOutputSlot() {
    return this.outputSlot;
  }
}
