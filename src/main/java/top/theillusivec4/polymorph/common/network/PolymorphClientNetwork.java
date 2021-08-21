package top.theillusivec4.polymorph.common.network;

import java.util.HashSet;
import java.util.Set;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.util.Identifier;
import top.theillusivec4.polymorph.client.recipe.RecipeControllerHub;

public class PolymorphClientNetwork {

  public static void setup() {
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
  }
}
