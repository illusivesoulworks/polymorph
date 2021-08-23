package top.theillusivec4.polymorph.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.loader.api.FabricLoader;
import top.theillusivec4.polymorph.client.recipe.RecipeControllerHub;
import top.theillusivec4.polymorph.common.PolymorphMod;
import top.theillusivec4.polymorph.common.network.PolymorphClientNetwork;

public class PolymorphClientMod implements ClientModInitializer {

  @Override
  public void onInitializeClient() {
    PolymorphClientNetwork.setup();
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
