package top.theillusivec4.polymorph.common.integration.camsbackpacks;

import dev.cammiescorner.camsbackpacks.common.screen.BackpackScreenHandler;
import net.minecraft.screen.PlayerScreenHandler;
import top.theillusivec4.polymorph.api.PolymorphApi;
import top.theillusivec4.polymorph.common.integration.AbstractCompatibilityModule;

public class CamsBackpacksModule extends AbstractCompatibilityModule {

  @Override
  public void clientSetup() {
    PolymorphApi.client().registerWidget(pHandledScreen -> {
      if (pHandledScreen.getScreenHandler() instanceof BackpackScreenHandler ||
          pHandledScreen.getScreenHandler() instanceof PlayerScreenHandler) {
        return new BackpackRecipesWidget(pHandledScreen);
      }
      return null;
    });
  }
}
