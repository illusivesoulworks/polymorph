package top.theillusivec4.polymorph.common.integration.fastfurnace;

import net.minecraft.item.crafting.AbstractCookingRecipe;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.tileentity.TileEntity;
import shadows.fastfurnace.tile.TileFastBlastFurnace;
import shadows.fastfurnace.tile.TileFastFurnace;
import shadows.fastfurnace.tile.TileFastSmoker;
import top.theillusivec4.polymorph.common.integration.AbstractCompatibilityModule;
import top.theillusivec4.polymorph.mixin.integration.fastfurnace.AccessorTileFastFurnace;

public class FastFurnaceModule extends AbstractCompatibilityModule {

  @Override
  public boolean selectRecipe(TileEntity tileEntity, IRecipe<?> recipe) {

    if (recipe instanceof AbstractCookingRecipe &&
        (tileEntity instanceof TileFastFurnace || tileEntity instanceof TileFastSmoker ||
            tileEntity instanceof TileFastBlastFurnace)) {
      ((AccessorTileFastFurnace) tileEntity).setCurRecipe((AbstractCookingRecipe) recipe);
    }
    return false;
  }
}
