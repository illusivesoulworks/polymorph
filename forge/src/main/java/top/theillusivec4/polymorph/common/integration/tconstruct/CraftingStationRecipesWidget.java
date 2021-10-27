package top.theillusivec4.polymorph.common.integration.tconstruct;

import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import top.theillusivec4.polymorph.client.recipe.widget.PersistentRecipesWidget;

public class CraftingStationRecipesWidget extends PersistentRecipesWidget {

  private final Slot outputSlot;
  private final CraftingInventory craftingInventory;

  public CraftingStationRecipesWidget(ContainerScreen<?> pContainerScreen,
                                      CraftingInventory pCraftingInventory, Slot pOutputSlot) {
    super(pContainerScreen);
    this.outputSlot = pOutputSlot;
    this.craftingInventory = pCraftingInventory;
  }

  @Override
  public Slot getOutputSlot() {
    return this.outputSlot;
  }

  @Override
  public int getYPos() {
    return getOutputSlot().yPos + 8;
  }

  @Override
  protected NonNullList<ItemStack> getInput() {
    NonNullList<ItemStack> input =
        NonNullList.withSize(this.craftingInventory.getSizeInventory(), ItemStack.EMPTY);

    for (int i = 0; i < this.craftingInventory.getSizeInventory(); i++) {
      input.set(i, this.craftingInventory.getStackInSlot(i));
    }
    return input;
  }
}
