package top.theillusivec4.polymorph.api.common.base;

import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

public interface IRecipeData {

  ItemStack getOutput();

  ResourceLocation getResourceLocation();
}
