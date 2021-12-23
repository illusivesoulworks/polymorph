package top.theillusivec4.polymorph.common.integration;

import net.minecraft.block.entity.BlockEntity;
import net.minecraft.recipe.Recipe;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.network.ServerPlayerEntity;

public abstract class AbstractCompatibilityModule {

  public void setup() {
    // NO-OP
  }

  public void clientSetup() {
    // NO-OP
  }

  public boolean selectRecipe(ScreenHandler pScreenHandler, Recipe<?> pRecipe) {
    return false;
  }

  public boolean selectRecipe(BlockEntity pBlockEntity, Recipe<?> pRecipe) {
    return false;
  }

  public boolean openScreenHandler(ScreenHandler pScreenHandler, ServerPlayerEntity pPlayer) {
    return false;
  }
}
