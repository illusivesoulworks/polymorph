package top.theillusivec4.polymorph.common.integration.fastbench;

import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.CraftResultInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.crafting.IRecipe;

public class FastBenchModule {

  public static void setLastRecipe(ServerPlayerEntity serverPlayerEntity, IRecipe<?> recipe) {
    Container container = serverPlayerEntity.openContainer;

    for (Slot slot : container.inventorySlots) {

      if (slot.inventory instanceof CraftResultInventory) {
        ((CraftResultInventory) slot.inventory).setRecipeUsed(recipe);
        return;
      }
    }
  }
}
