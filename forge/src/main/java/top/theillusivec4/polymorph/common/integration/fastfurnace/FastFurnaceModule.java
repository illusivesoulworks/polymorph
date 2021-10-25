package top.theillusivec4.polymorph.common.integration.fastfurnace;

import net.minecraft.item.crafting.IRecipe;
import net.minecraft.tileentity.AbstractFurnaceTileEntity;
import net.minecraft.tileentity.TileEntity;
import top.theillusivec4.polymorph.common.integration.AbstractCompatibilityModule;
import top.theillusivec4.polymorph.common.util.PolymorphAccessor;

public class FastFurnaceModule extends AbstractCompatibilityModule {

  @Override
  public boolean selectRecipe(TileEntity tileEntity, IRecipe<?> recipe) {

    if (tileEntity instanceof AbstractFurnaceTileEntity) {
      PolymorphAccessor.writeField(tileEntity, "curRecipe", recipe);
    }
    return false;
  }
}
