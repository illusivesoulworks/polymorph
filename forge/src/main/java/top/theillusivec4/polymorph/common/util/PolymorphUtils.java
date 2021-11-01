package top.theillusivec4.polymorph.common.util;

import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.items.wrapper.RecipeWrapper;

public class PolymorphUtils {

  public static IInventory wrapItems(ItemStack... stacks) {
    return new RecipeWrapper(new ItemStackHandler(NonNullList.from(ItemStack.EMPTY, stacks)));
  }
}
