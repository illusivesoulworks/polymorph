package top.theillusivec4.polymorph.loader.common;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback;
import net.minecraft.screen.CraftingScreenHandler;
import net.minecraft.screen.PlayerScreenHandler;
import top.theillusivec4.polymorph.api.PolymorphApi;
import top.theillusivec4.polymorph.core.provider.CraftingProvider;
import top.theillusivec4.polymorph.core.provider.PlayerProvider;
import top.theillusivec4.polymorph.loader.network.NetworkHandler;
import top.theillusivec4.polymorph.loader.server.PolymorphCommands;

public class PolymorphMod implements ModInitializer {

  @Override
  public void onInitialize() {
    PolymorphApi.addProvider(CraftingScreenHandler.class, CraftingProvider::new);
    PolymorphApi.addProvider(PlayerScreenHandler.class, PlayerProvider::new);
    NetworkHandler.setup();
    CommandRegistrationCallback.EVENT
        .register((commandDispatcher, b) -> PolymorphCommands.register(commandDispatcher));
  }
}
