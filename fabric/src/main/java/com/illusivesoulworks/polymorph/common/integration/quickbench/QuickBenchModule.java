package com.illusivesoulworks.polymorph.common.integration.quickbench;

import com.illusivesoulworks.polymorph.common.integration.AbstractCompatibilityModule;
import com.illusivesoulworks.polymorph.mixin.core.AccessorCraftingMenu;
import com.illusivesoulworks.polymorph.mixin.core.AccessorInventoryMenu;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.CraftingMenu;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.inventory.ResultContainer;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.Recipe;

public class QuickBenchModule extends AbstractCompatibilityModule {

  @Override
  public boolean selectRecipe(AbstractContainerMenu containerMenu, Recipe<?> recipe) {

    if (recipe instanceof CraftingRecipe) {
      ResultContainer result = null;

      if (containerMenu instanceof CraftingMenu craftingMenu) {
        AccessorCraftingMenu accessor = (AccessorCraftingMenu) craftingMenu;
        result = accessor.getResultSlots();
      } else if (containerMenu instanceof InventoryMenu inventoryMenu) {
        AccessorInventoryMenu accessor = (AccessorInventoryMenu) inventoryMenu;
        result = accessor.getResultSlots();
      }

      if (result != null) {
        result.setRecipeUsed(recipe);
      }
    }
    return false;
  }
}
