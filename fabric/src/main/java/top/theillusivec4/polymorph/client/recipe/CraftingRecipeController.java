package top.theillusivec4.polymorph.client.recipe;

import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.recipe.CraftingRecipe;
import net.minecraft.screen.slot.Slot;

public class CraftingRecipeController
    extends AbstractRecipeController<CraftingInventory, CraftingRecipe> {

  final CraftingInventory craftingInventory;
  final Slot outputSlot;

  public CraftingRecipeController(HandledScreen<?> handledScreen,
                                  CraftingInventory craftingInventory,
                                  Slot outputSlot) {
    super(handledScreen);
    this.craftingInventory = craftingInventory;
    this.outputSlot = outputSlot;
    this.init();
  }

  @Override
  public CraftingInventory getInventory() {
    return this.craftingInventory;
  }

  @Override
  public Slot getOutputSlot() {
    return this.outputSlot;
  }
}
