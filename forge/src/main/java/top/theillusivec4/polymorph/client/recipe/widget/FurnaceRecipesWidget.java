package top.theillusivec4.polymorph.client.recipe.widget;

import java.util.List;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;

public class FurnaceRecipesWidget extends PersistentRecipesWidget {

  private final Slot outputSlot;
  private final IInventory inventory;

  public FurnaceRecipesWidget(ContainerScreen<?> pContainerScreen) {
    super(pContainerScreen);
    List<Slot> slots = pContainerScreen.getContainer().inventorySlots;
    this.outputSlot = slots.get(2);
    this.inventory = slots.get(0).inventory;
  }

  @Override
  public Slot getOutputSlot() {
    return this.outputSlot;
  }

  @Override
  protected NonNullList<ItemStack> getInput() {
    return NonNullList.from(ItemStack.EMPTY, this.inventory.getStackInSlot(0));
  }
}
