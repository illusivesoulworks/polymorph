package top.theillusivec4.polymorph.client;

import java.util.HashSet;
import java.util.Set;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.util.Identifier;
import top.theillusivec4.polymorph.client.recipe.RecipeSelector;
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
          minecraftClient.execute(() -> RecipeSelector.getController().ifPresent(
              recipeController -> recipeController
                  .setRecipes(recipes, minecraftClient.world, null)));
        });
  }
}
