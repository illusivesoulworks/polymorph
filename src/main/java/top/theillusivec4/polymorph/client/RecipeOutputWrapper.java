package top.theillusivec4.polymorph.client;

import java.util.Objects;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;

public class RecipeOutputWrapper {

  private final Item item;
  private final int count;
  private final CompoundNBT tag;

  public RecipeOutputWrapper(ItemStack stack) {
    this.item = stack.getItem();
    this.count = stack.getCount();
    this.tag = stack.getTag();
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    RecipeOutputWrapper that = (RecipeOutputWrapper) o;
    return count == that.count && item.equals(that.item) && Objects.equals(tag, that.tag);
  }

  @Override
  public int hashCode() {
    return Objects.hash(item, count, tag);
  }
}
