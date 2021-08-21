package top.theillusivec4.polymorph.client;

import java.util.HashSet;
import java.util.Set;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.util.Identifier;
import top.theillusivec4.polymorph.client.recipe.RecipeControllerHub;
import top.theillusivec4.polymorph.common.network.PolymorphPackets;

public class PolymorphClientMod implements ClientModInitializer {

  @Override
  public void onInitializeClient() {
    ClientPlayNetworking.registerGlobalReceiver(PolymorphPackets.SEND_RECIPES,
        (minecraftClient, clientPlayNetworkHandler, packetByteBuf, packetSender) -> {
          Set<Identifier> recipes = new HashSet<>();

          while (packetByteBuf.isReadable()) {
            recipes.add(Identifier.tryParse(packetByteBuf.readString(32767)));
          }
          minecraftClient.execute(() -> RecipeControllerHub.getController().ifPresent(
              recipeController -> recipeController
                  .setRecipes(recipes, minecraftClient.world, null)));
        });
    ClientTickEvents.END_CLIENT_TICK
        .register(client -> RecipeControllerHub.getController().ifPresent(recipeSelector -> {
          if (client.player != null && client.player.currentScreenHandler == null) {
            RecipeControllerHub.clear();
          } else {
            recipeSelector.tick();
          }
        }));
  }
}
