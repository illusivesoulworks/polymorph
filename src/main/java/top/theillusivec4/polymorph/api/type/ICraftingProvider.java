package top.theillusivec4.polymorph.api.type;

import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.ICraftingRecipe;
import top.theillusivec4.polymorph.api.PolymorphApi;

public interface ICraftingProvider extends IPolyProvider<CraftingInventory> {

  default void transfer(PlayerEntity playerIn, ICraftingRecipe recipe) {
    Container container = getContainer();
    Slot slot = getOutputSlot();
    CraftingInventory inventory = getInventory();
    ItemStack itemstack = container.transferStackInSlot(playerIn, slot.slotNumber);

    if (recipe.matches(inventory, playerIn.world)) {
      slot.putStack(recipe.getCraftingResult(inventory));

      while (!itemstack.isEmpty() && ItemStack.areItemsEqual(slot.getStack(), itemstack)) {
        itemstack = container.transferStackInSlot(playerIn, slot.slotNumber);

        if (recipe.matches(inventory, playerIn.world)) {
          slot.putStack(recipe.getCraftingResult(inventory));
        }
      }
    }
  }

  default IRecipeSelector<CraftingInventory, ICraftingRecipe> createSelector(
      ContainerScreen<?> screen) {
    return PolymorphApi.getInstance().createCraftingSelector(screen, this);
  }
}
