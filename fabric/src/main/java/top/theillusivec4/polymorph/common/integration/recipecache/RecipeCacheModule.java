package top.theillusivec4.polymorph.common.integration.recipecache;

import com.biom4st3r.recipecache.DuckFurnace;
import java.util.Optional;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.recipe.Recipe;
import top.theillusivec4.polymorph.common.integration.AbstractCompatibilityModule;

public class RecipeCacheModule extends AbstractCompatibilityModule {

  @SuppressWarnings("unchecked")
  @Override
  public boolean selectRecipe(BlockEntity pBlockEntity, Recipe<?> pRecipe) {

    if (pBlockEntity instanceof DuckFurnace df) {
      df.biom4st3r$setRecipe(Optional.of((Recipe<Inventory>) pRecipe));
      return true;
    }
    return false;
  }
}
