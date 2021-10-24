package top.theillusivec4.polymorph.common.impl;

import java.util.Objects;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import top.theillusivec4.polymorph.api.common.base.IRecipeData;

public class RecipeData implements IRecipeData {

  private final ItemStack output;
  private final ResourceLocation resourceLocation;

  public RecipeData(ResourceLocation pResourceLocation, ItemStack pOutput) {
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
    RecipeData that = (RecipeData) o;
    return ItemStack.areItemStacksEqual(this.getOutput(), that.getOutput());
  }

  @Override
  public int hashCode() {
    return Objects.hash(output);
  }
}
