package top.theillusivec4.polymorph.api.common.base;

import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;

public interface RecipePair extends Comparable<RecipePair> {

  ItemStack getOutput();

  Identifier getIdentifier();
}
