package top.theillusivec4.polymorph.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.screen.AbstractFurnaceScreenHandler;
import net.minecraft.screen.SmithingScreenHandler;
import top.theillusivec4.polymorph.api.PolymorphClientApi;
import top.theillusivec4.polymorph.client.recipe.FurnaceRecipeController;
import top.theillusivec4.polymorph.client.recipe.RecipeControllerHub;
import top.theillusivec4.polymorph.client.recipe.SmithingRecipeController;
import top.theillusivec4.polymorph.common.PolymorphMod;
import top.theillusivec4.polymorph.common.network.PolymorphClientNetwork;

public class PolymorphClientMod implements ClientModInitializer {

  @Override
  public void onInitializeClient() {
    PolymorphClientNetwork.setup();
    PolymorphClientApi.getInstance().addRecipeController(handledScreen -> {
      if (handledScreen.getScreenHandler() instanceof SmithingScreenHandler) {
        return new SmithingRecipeController(handledScreen);
      } else if (handledScreen.getScreenHandler() instanceof AbstractFurnaceScreenHandler) {
        return new FurnaceRecipeController(handledScreen);
      }
      return null;
    });
    ClientTickEvents.END_CLIENT_TICK
        .register(client -> RecipeControllerHub.getController().ifPresent(recipeSelector -> {
          if (client.player != null && client.player.currentScreenHandler == null) {
            RecipeControllerHub.clear();
          } else {
            recipeSelector.tick();
          }
        }));
    FabricLoader loader = FabricLoader.getInstance();
    PolymorphMod.INTEGRATIONS.forEach((modid, supplier) -> {

      if (loader.isModLoaded(modid)) {
        supplier.get().clientSetup();
      }
    });
  }
}
