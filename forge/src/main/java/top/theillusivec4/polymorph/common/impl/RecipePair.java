package top.theillusivec4.polymorph.common.impl;

import java.util.Objects;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import top.theillusivec4.polymorph.api.common.base.IRecipePair;

public class RecipePair implements IRecipePair {

  private final ItemStack output;
  private final ResourceLocation resourceLocation;

  public RecipePair(ResourceLocation pResourceLocation, ItemStack pOutput) {
    this.resourceLocation = pResourceLocation;
    this.output = pOutput;
  }

  @Override
  public ItemStack getOutput() {
    return output;
  }

  @Override
  public ResourceLocation getResourceLocation() {
    return resourceLocation;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    RecipePair that = (RecipePair) o;
    return ItemStack.areItemStacksEqual(this.getOutput(), that.getOutput());
  }

  @Override
  public int hashCode() {
    return Objects.hash(output);
  }
}
