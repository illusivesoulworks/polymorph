package top.theillusivec4.polymorph.client.recipe.widget;

import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;

public class FurnaceRecipesWidget extends AbstractProcessorRecipesWidget {

  private final Container container;
  private final IInventory inventory;

  public FurnaceRecipesWidget(ContainerScreen<?> screen) {
    super(screen, 1);
    this.container = screen.getContainer();
    this.inventory = container.inventorySlots.get(0).inventory;
  }

  @Override
  public Slot getOutputSlot() {
    return this.container.inventorySlots.get(2);
  }

  @Override
  protected NonNullList<ItemStack> getInput() {
    return NonNullList.from(ItemStack.EMPTY, inventory.getStackInSlot(0));
  }
}
