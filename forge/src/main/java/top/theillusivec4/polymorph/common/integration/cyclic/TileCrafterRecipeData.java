package top.theillusivec4.polymorph.common.integration.cyclic;

import com.lothrazar.cyclic.block.crafter.TileCrafter;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;
import top.theillusivec4.polymorph.common.capability.AbstractTileEntityRecipeData;
import top.theillusivec4.polymorph.mixin.integration.cyclic.AccessorTileCrafter;

public class TileCrafterRecipeData extends AbstractTileEntityRecipeData<TileCrafter> {

  private LazyOptional<IItemHandler> maybeGrid;

  public TileCrafterRecipeData(TileCrafter pOwner) {
    super(pOwner);
  }

  @Override
  protected NonNullList<ItemStack> getInput() {
    if (this.maybeGrid == null) {
      this.maybeGrid = ((AccessorTileCrafter) this.getOwner()).getGridCap();

      if (this.maybeGrid == null) {
        return NonNullList.create();
      }
    }
    return this.maybeGrid.map(grid -> {
      NonNullList<ItemStack> result = NonNullList.withSize(9, ItemStack.EMPTY);
      for (int i = 0; i < 9; ++i) {
        result.set(i, grid.getStackInSlot(i));
      }
      return result;
    }).orElse(NonNullList.create());
  }
}
