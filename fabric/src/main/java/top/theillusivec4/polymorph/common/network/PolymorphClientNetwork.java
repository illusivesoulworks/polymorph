package top.theillusivec4.polymorph.common.network;

import java.util.Optional;
import java.util.SortedSet;
import java.util.TreeSet;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;
import top.theillusivec4.polymorph.api.PolymorphApi;
import top.theillusivec4.polymorph.api.client.base.RecipesWidget;
import top.theillusivec4.polymorph.api.common.base.RecipePair;
import top.theillusivec4.polymorph.client.recipe.RecipesWidgetControl;
import top.theillusivec4.polymorph.common.impl.RecipePairImpl;

public class PolymorphClientNetwork {

  public static void setup() {
    ClientPlayNetworking.registerGlobalReceiver(PolymorphPackets.HIGHLIGHT_RECIPE,
        PolymorphClientNetwork::highlightRecipe);
    ClientPlayNetworking.registerGlobalReceiver(PolymorphPackets.RECIPE_SYNC,
        PolymorphClientNetwork::syncRecipe);
    ClientPlayNetworking.registerGlobalReceiver(PolymorphPackets.RECIPES_LIST,
        PolymorphClientNetwork::sendRecipesList);
  }

  private static void highlightRecipe(MinecraftClient pClient,
                                      ClientPlayNetworkHandler pHandler, PacketByteBuf pBuf,
                                      PacketSender pSender) {
    Identifier identifier = pBuf.readIdentifier();
    pClient.execute(() -> {
      ClientPlayerEntity player = pClient.player;

      if (player != null) {
        RecipesWidgetControl.get().ifPresent(widget -> widget.highlightRecipe(identifier));
      }
    });
  }

  private static void syncRecipe(MinecraftClient pClient, ClientPlayNetworkHandler pHandler,
                                 PacketByteBuf pBuf, PacketSender pSender) {
    SortedSet<RecipePair> recipeDataset = new TreeSet<>();
    Identifier selected = null;

    if (pBuf.isReadable()) {
      int size = pBuf.readInt();

      for (int i = 0; i < size; i++) {
        recipeDataset.add(new RecipePairImpl(pBuf.readIdentifier(), pBuf.readItemStack()));
      }

      if (pBuf.isReadable()) {
        selected = pBuf.readIdentifier();
      }
    }
    Identifier finalSelected = selected;
    pClient.execute(() -> {
      ClientPlayerEntity player = pClient.player;

      if (player != null) {
        Optional<RecipesWidget> maybeWidget = RecipesWidgetControl.get();
        maybeWidget.ifPresent(
            widget -> widget.setRecipesList(recipeDataset, finalSelected));

        if (!maybeWidget.isPresent()) {
          RecipesWidgetControl.enqueueRecipesList(recipeDataset, finalSelected);
        }
      }
    });
  }

  private static void sendRecipesList(MinecraftClient pClient, ClientPlayNetworkHandler pHandler,
                                      PacketByteBuf pBuf, PacketSender pSender) {
    SortedSet<RecipePair> recipeDataset = new TreeSet<>();
    Identifier selected = null;

    if (pBuf.isReadable()) {
      int size = pBuf.readInt();

      for (int i = 0; i < size; i++) {
        recipeDataset.add(new RecipePairImpl(pBuf.readIdentifier(), pBuf.readItemStack()));
      }

      if (pBuf.isReadable()) {
        selected = pBuf.readIdentifier();
      }
    }
    Identifier finalSelected = selected;
    pClient.execute(() -> {
      ClientPlayerEntity player = pClient.player;

      if (player != null) {
        PolymorphApi.common().getRecipeData(player).ifPresent(recipeData -> {
          recipeData.setRecipesList(recipeDataset);
          player.world.getRecipeManager().get(finalSelected)
              .ifPresent(recipeData::setSelectedRecipe);
        });
      }
    });
  }
}
