package top.theillusivec4.polymorph.common.network;

import java.util.Optional;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.recipe.Recipe;
import net.minecraft.screen.ForgingScreenHandler;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;
import top.theillusivec4.polymorph.api.PolymorphApi;
import top.theillusivec4.polymorph.common.integration.AbstractCompatibilityModule;
import top.theillusivec4.polymorph.common.integration.PolymorphIntegrations;

public class PolymorphNetwork {

  public static void setup() {
    ServerPlayNetworking.registerGlobalReceiver(PolymorphPackets.PLAYER_SELECT,
        PolymorphNetwork::handlePlayerSelect);
    ServerPlayNetworking.registerGlobalReceiver(PolymorphPackets.PERSISTENT_SELECT,
        PolymorphNetwork::handlePersistentSelect);
    ServerPlayNetworking.registerGlobalReceiver(PolymorphPackets.STACK_SELECT,
        PolymorphNetwork::handleStackSelect);
  }

  private static void handlePlayerSelect(MinecraftServer pServer, ServerPlayerEntity pPlayer,
                                         ServerPlayNetworkHandler pHandler, PacketByteBuf pBuf,
                                         PacketSender pResponseSender) {
    Identifier identifier = pBuf.readIdentifier();
    pServer.execute(() -> {
      ScreenHandler screenHandler = pPlayer.currentScreenHandler;
      pPlayer.world.getRecipeManager().get(identifier).ifPresent(recipe -> {
        PolymorphApi.common().getRecipeData(pPlayer)
            .ifPresent(recipeData -> recipeData.selectRecipe(recipe));

        for (AbstractCompatibilityModule integration : PolymorphIntegrations.get()) {

          if (integration.selectRecipe(screenHandler, recipe)) {
            return;
          }
        }
        screenHandler.onContentChanged(pPlayer.getInventory());

        if (screenHandler instanceof ForgingScreenHandler) {
          ((ForgingScreenHandler) screenHandler).updateResult();
        }
      });
    });
  }

  private static void handlePersistentSelect(MinecraftServer pServer, ServerPlayerEntity pPlayer,
                                             ServerPlayNetworkHandler pHandler, PacketByteBuf pBuf,
                                             PacketSender pResponseSender) {
    Identifier identifier = pBuf.readIdentifier();
    pServer.execute(() -> {
      World world = pPlayer.getEntityWorld();
      Optional<? extends Recipe<?>> maybeRecipe =
          world.getRecipeManager().get(identifier);
      maybeRecipe.ifPresent(recipe -> {
        ScreenHandler screenHandler = pPlayer.currentScreenHandler;
        PolymorphApi.common().getRecipeDataFromBlockEntity(screenHandler)
            .ifPresent(recipeData -> {
              recipeData.selectRecipe(recipe);

              for (AbstractCompatibilityModule integration : PolymorphIntegrations.get()) {

                if (integration.selectRecipe(recipeData.getOwner(), recipe) ||
                    integration.selectRecipe(screenHandler, recipe)) {
                  return;
                }
              }
            });
      });
    });
  }

  private static void handleStackSelect(MinecraftServer pServer, ServerPlayerEntity pPlayer,
                                        ServerPlayNetworkHandler pHandler, PacketByteBuf pBuf,
                                        PacketSender pResponseSender) {
    Identifier identifier = pBuf.readIdentifier();
    pServer.execute(() -> {
      World world = pPlayer.getEntityWorld();
      Optional<? extends Recipe<?>> maybeRecipe =
          world.getRecipeManager().get(identifier);
      maybeRecipe.ifPresent(recipe -> {
        ScreenHandler screenHandler = pPlayer.currentScreenHandler;
        PolymorphApi.common().getRecipeDataFromBlockEntity(screenHandler)
            .ifPresent(recipeData -> {
              recipeData.selectRecipe(recipe);

              for (AbstractCompatibilityModule integration : PolymorphIntegrations.get()) {

                if (integration.selectRecipe(screenHandler, recipe)) {
                  return;
                }
              }
            });
      });
    });
  }
}
