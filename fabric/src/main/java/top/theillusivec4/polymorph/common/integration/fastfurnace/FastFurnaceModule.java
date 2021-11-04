package top.theillusivec4.polymorph.common.integration.fastfurnace;

import net.minecraft.block.entity.BlockEntity;
import net.minecraft.recipe.AbstractCookingRecipe;
import org.apache.commons.lang3.reflect.FieldUtils;
import top.theillusivec4.polymorph.common.PolymorphMod;

public class FastFurnaceModule {

  public static void setCachedRecipe(BlockEntity blockEntity, AbstractCookingRecipe recipe) {
    try {
      FieldUtils.writeField(blockEntity, "cachedRecipe", recipe, true);
    } catch (IllegalAccessException e) {
      PolymorphMod.LOGGER.error("Error accessing cachedRecipe from FastFurnace!");
    } catch (IllegalArgumentException e) {
      PolymorphMod.LOGGER.debug("Cannot find FastFurnace, skipping field override!");
    }
  }
}
