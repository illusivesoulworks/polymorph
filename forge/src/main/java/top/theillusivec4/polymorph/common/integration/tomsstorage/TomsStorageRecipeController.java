package top.theillusivec4.polymorph.common.integration.tomsstorage;

import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.inventory.container.Slot;
import top.theillusivec4.polymorph.client.recipe.controller.CraftingRecipeController;

public class TomsStorageRecipeController extends CraftingRecipeController {

  public TomsStorageRecipeController(ContainerScreen<?> containerScreen,
                                     CraftingInventory craftingInventory, Slot outputSlot) {
    super(containerScreen, craftingInventory, outputSlot);
  }
}
