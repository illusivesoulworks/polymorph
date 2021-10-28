package top.theillusivec4.polymorph.common.integration.refinedstorage;

import com.refinedmods.refinedstorage.tile.grid.GridTile;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import top.theillusivec4.polymorph.common.capability.AbstractTileEntityRecipeData;

public class GridTileRecipeData extends AbstractTileEntityRecipeData<GridTile> {

  public GridTileRecipeData(GridTile pOwner) {
    super(pOwner);
  }

  @Override
  protected NonNullList<ItemStack> getInput() {
    CraftingInventory craftingInventory = this.getOwner().getNode().getCraftingMatrix();

    if (craftingInventory != null) {
      NonNullList<ItemStack> stacks =
          NonNullList.withSize(craftingInventory.getSizeInventory(), ItemStack.EMPTY);

      for (int i = 0; i < craftingInventory.getSizeInventory(); i++) {
        stacks.set(i, craftingInventory.getStackInSlot(i));
      }
      return stacks;
    }
    return NonNullList.create();
  }
}
