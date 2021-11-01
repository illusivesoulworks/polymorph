package top.theillusivec4.polymorph.common.integration.ironfurnaces;

import ironfurnaces.tileentity.BlockIronFurnaceTileBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import top.theillusivec4.polymorph.common.capability.AbstractHighlightedRecipeData;

public class IronFurnaceRecipeData extends AbstractHighlightedRecipeData<BlockIronFurnaceTileBase> {

  public IronFurnaceRecipeData(BlockIronFurnaceTileBase pOwner) {
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
