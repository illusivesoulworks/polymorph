package top.theillusivec4.polymorph.loader.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.event.client.ClientTickCallback;
import top.theillusivec4.polymorph.core.client.RecipeSelectionManager;
import top.theillusivec4.polymorph.loader.network.ClientNetworkHandler;

public class PolymorphClientMod implements ClientModInitializer {

  @Override
  public void onInitializeClient() {
    ClientNetworkHandler.setup();
    ClientTickCallback.EVENT.register(minecraftClient -> RecipeSelectionManager.getInstance()
        .ifPresent(RecipeSelectionManager::tick));
  }
}
