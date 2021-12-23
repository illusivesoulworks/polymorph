package top.theillusivec4.polymorph.client;

import net.fabricmc.api.ClientModInitializer;
import net.minecraft.screen.AbstractFurnaceScreenHandler;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.SmithingScreenHandler;
import top.theillusivec4.polymorph.api.PolymorphApi;
import top.theillusivec4.polymorph.client.recipe.widget.FurnaceRecipesWidget;
import top.theillusivec4.polymorph.client.recipe.widget.PlayerRecipesWidget;
import top.theillusivec4.polymorph.common.integration.PolymorphIntegrations;
import top.theillusivec4.polymorph.common.network.PolymorphClientNetwork;

public class PolymorphClientMod implements ClientModInitializer {

  @Override
  public void onInitializeClient() {
    PolymorphClientNetwork.setup();
    ClientEventsListener.setup();
    PolymorphApi.client().registerWidget(handledScreen -> {
      ScreenHandler screenHandler = handledScreen.getScreenHandler();

      if (screenHandler instanceof SmithingScreenHandler) {
        return new PlayerRecipesWidget(handledScreen, screenHandler.slots.get(2));
      } else if (screenHandler instanceof AbstractFurnaceScreenHandler) {
        return new FurnaceRecipesWidget(handledScreen);
      }
      return null;
    });
    PolymorphIntegrations.clientSetup();
  }
}
