package top.theillusivec4.polymorph.client.recipe.controller;

import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.crafting.ICraftingRecipe;

public class CraftingRecipeController
    extends AbstractRecipeController<CraftingInventory, ICraftingRecipe> {

  final CraftingInventory craftingInventory;
  final Slot outputSlot;

  public CraftingRecipeController(ContainerScreen<?> containerScreen,
                                  CraftingInventory craftingInventory,
                                  Slot outputSlot) {
    super(containerScreen);
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