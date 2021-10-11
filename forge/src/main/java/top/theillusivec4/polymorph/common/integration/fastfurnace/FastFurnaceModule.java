package top.theillusivec4.polymorph.common.integration.fastfurnace;

import net.minecraft.item.crafting.IRecipe;
import net.minecraft.tileentity.AbstractFurnaceTileEntity;
import net.minecraft.tileentity.TileEntity;
import org.apache.commons.lang3.reflect.FieldUtils;
import top.theillusivec4.polymorph.common.PolymorphMod;
import top.theillusivec4.polymorph.common.integration.AbstractCompatibilityModule;

public class FastFurnaceModule extends AbstractCompatibilityModule {

  @Override
  public boolean setRecipe(TileEntity tileEntity, IRecipe<?> recipe) {

    if (tileEntity instanceof AbstractFurnaceTileEntity) {
      try {
        FieldUtils.writeField(tileEntity, "curRecipe", recipe, true);
        return true;
      } catch (IllegalAccessException e) {
        PolymorphMod.LOGGER.error("Error accessing curRecipe from FastFurnace!");
      } catch (IllegalArgumentException e) {
        PolymorphMod.LOGGER.debug("Cannot find FastFurnace, skipping field override!");
      }
    }
    return false;
  }
}
