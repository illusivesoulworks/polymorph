package top.theillusivec4.polymorph.common.component;

import net.minecraft.block.entity.AbstractFurnaceBlockEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.collection.DefaultedList;

public class FurnaceRecipeData extends AbstractHighlightedRecipeData<AbstractFurnaceBlockEntity> {

  public FurnaceRecipeData(AbstractFurnaceBlockEntity pOwner) {
    super(pOwner);
  }

  @Override
  protected DefaultedList<ItemStack> getInput() {
    return DefaultedList.copyOf(ItemStack.EMPTY, this.getOwner().getStack(0));
  }

  @Override
  public boolean isEmpty() {
    return this.getInput().get(0).isEmpty();
  }
}
