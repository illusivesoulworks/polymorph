package top.theillusivec4.polymorph.core.base.common;

import net.minecraft.inventory.CraftingInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Ingredient;
import net.minecraft.screen.CraftingScreenHandler;
import net.minecraft.screen.PlayerScreenHandler;

public interface Accessor {

  CraftingInventory getCraftingInput(PlayerScreenHandler screenHandler);

  CraftingInventory getCraftingInput(CraftingScreenHandler screenHandler);

  ItemStack[] getMatchingStacks(Ingredient ingredient);
}
