package top.theillusivec4.polymorph.common.integration.fastfurnace;

import net.minecraft.item.crafting.IRecipe;
import net.minecraft.tileentity.TileEntity;
import org.apache.commons.lang3.reflect.FieldUtils;
import top.theillusivec4.polymorph.common.PolymorphMod;

public class FastFurnaceModule {

  public static void setCurrentRecipe(TileEntity te, IRecipe<?> recipe) {
    try {
      FieldUtils.writeField(te, "curRecipe", recipe, true);
    } catch (IllegalAccessException e) {
      PolymorphMod.LOGGER.error("Error accessing curRecipe from FastFurnace!");
    } catch (IllegalArgumentException e) {
      PolymorphMod.LOGGER.debug("Cannot find FastFurnace, skipping field override!");
    }
  }
}
