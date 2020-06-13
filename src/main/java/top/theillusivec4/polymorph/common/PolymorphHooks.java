package top.theillusivec4.polymorph.common;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import top.theillusivec4.polymorph.client.RecipeConflictManager;

public class PolymorphHooks {

  public static void onCraftMatrixChanged() {
    DistExecutor.runWhenOn(Dist.CLIENT, () -> () -> RecipeConflictManager.getInstance()
        .ifPresent(RecipeConflictManager::onCraftMatrixChanged));
  }
}
