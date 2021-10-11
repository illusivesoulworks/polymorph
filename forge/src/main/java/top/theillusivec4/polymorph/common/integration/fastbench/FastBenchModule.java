package top.theillusivec4.polymorph.common.integration.fastbench;

import net.minecraft.inventory.CraftResultInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.crafting.IRecipe;
import top.theillusivec4.polymorph.common.integration.AbstractCompatibilityModule;

public class FastBenchModule extends AbstractCompatibilityModule {

  @Override
  public boolean setRecipe(Container container, IRecipe<?> recipe) {

    for (Slot slot : container.inventorySlots) {

      if (slot.inventory instanceof CraftResultInventory) {
        ((CraftResultInventory) slot.inventory).setRecipeUsed(recipe);
        return true;
      }
    }
    return false;
  }
}
