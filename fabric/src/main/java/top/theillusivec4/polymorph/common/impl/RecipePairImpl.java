package top.theillusivec4.polymorph.common.impl;

import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import top.theillusivec4.polymorph.api.common.base.RecipePair;

public class RecipePairImpl implements RecipePair {

  private final ItemStack output;
  private final Identifier identifier;

  public RecipePairImpl(Identifier pIdentifier, ItemStack pOutput) {
    this.identifier = pIdentifier;
    this.output = pOutput;
  }

  @Override
  public ItemStack getOutput() {
    return output;
  }

  @Override
  public Identifier getIdentifier() {
    return identifier;
  }

  @Override
  public int compareTo(RecipePair pOther) {
    ItemStack output1 = this.getOutput();
    ItemStack output2 = pOther.getOutput();

    if (ItemStack.areEqual(output1, output2)) {
      return 0;
    } else {
      int compare = output1.getTranslationKey().compareTo(output2.getTranslationKey());

      if (compare == 0) {
        return output1.getCount() - output2.getCount();
      } else {
        return compare;
      }
    }
  }
}
