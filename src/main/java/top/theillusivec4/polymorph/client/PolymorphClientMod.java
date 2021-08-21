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
          Identifier selected = null;

          if (packetByteBuf.isReadable()) {
            selected = packetByteBuf.readIdentifier();

            while (packetByteBuf.isReadable()) {
              recipes.add(Identifier.tryParse(packetByteBuf.readString(32767)));
            }
          }
          Identifier finalSelected = selected;
          minecraftClient.execute(() -> RecipeControllerHub.getController().ifPresent(
              recipeController -> recipeController
                  .setRecipes(recipes, minecraftClient.world, finalSelected)));
        });
    ClientPlayNetworking.registerGlobalReceiver(PolymorphPackets.HIGHLIGHT_RECIPE,
        (minecraftClient, clientPlayNetworkHandler, packetByteBuf, packetSender) -> {
          Identifier id = packetByteBuf.readIdentifier();
          minecraftClient.execute(() -> RecipeControllerHub.getController()
              .ifPresent(recipeController -> recipeController.highlightRecipe(id.toString())));
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
