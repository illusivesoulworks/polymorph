package top.theillusivec4.polymorph.common.capability;

import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.AbstractFurnaceTileEntity;
import net.minecraft.util.NonNullList;

public class FurnaceRecipeData extends AbstractHighlightedRecipeData<AbstractFurnaceTileEntity> {

  public FurnaceRecipeData(AbstractFurnaceTileEntity pOwner) {
    super(pOwner);
  }

  @Override
  protected NonNullList<ItemStack> getInput() {
    return NonNullList.from(ItemStack.EMPTY, this.getOwner().getStackInSlot(0));
  }

  @Override
  public boolean isEmpty() {
    return this.getInput().get(0).isEmpty();
  }
}
