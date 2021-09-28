package top.theillusivec4.polymorph.common.integration.fastbench;

import net.minecraft.inventory.CraftingResultInventory;
import net.minecraft.recipe.Recipe;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.server.network.ServerPlayerEntity;

public class FastBenchModule {

  public static void setLastRecipe(ServerPlayerEntity serverPlayerEntity, Recipe<?> recipe) {
    ScreenHandler screenHandler = serverPlayerEntity.currentScreenHandler;

    for (Slot slot : screenHandler.slots) {

      if (slot.inventory instanceof CraftingResultInventory) {
        ((CraftingResultInventory) slot.inventory).setLastRecipe(recipe);
        break;
      }
    }
  }
}
