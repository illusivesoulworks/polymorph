package top.theillusivec4.polymorph.common.integration.fastfurnace;

import net.minecraft.block.entity.BlockEntity;
import net.minecraft.recipe.Recipe;
import org.apache.commons.lang3.reflect.FieldUtils;
import top.theillusivec4.polymorph.common.PolymorphMod;
import top.theillusivec4.polymorph.common.integration.AbstractCompatibilityModule;

public class FastFurnaceModule extends AbstractCompatibilityModule {

  @Override
  public boolean selectRecipe(BlockEntity blockEntity, Recipe<?> recipe) {

    try {
      FieldUtils.writeField(blockEntity, "cachedRecipe", recipe, true);
    } catch (IllegalAccessException e) {
      PolymorphMod.LOGGER.error("Error accessing cachedRecipe from FastFurnace!");
    } catch (IllegalArgumentException e) {
      PolymorphMod.LOGGER.debug("Cannot find FastFurnace, skipping field override!");
    }
    return false;
  }
}
