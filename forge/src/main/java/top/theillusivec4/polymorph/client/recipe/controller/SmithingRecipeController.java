package top.theillusivec4.polymorph.client.recipe.controller;

import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.crafting.SmithingRecipe;

public class SmithingRecipeController extends AbstractRecipeController<IInventory, SmithingRecipe> {

  final IInventory inventory;
  final Slot outputSlot;

  public SmithingRecipeController(ContainerScreen<?> screen) {
    super(screen);
    this.outputSlot = screen.getContainer().inventorySlots.get(2);
    this.inventory = screen.getContainer().inventorySlots.get(0).inventory;
    this.init();
  }

  @Override
  public IInventory getInventory() {
    return this.inventory;
  }

  @Override
  public Slot getOutputSlot() {
    return this.outputSlot;
  }
}
