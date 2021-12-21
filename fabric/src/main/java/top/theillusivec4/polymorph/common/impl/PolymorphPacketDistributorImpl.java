package top.theillusivec4.polymorph.common.impl;

import java.util.SortedSet;
import java.util.TreeSet;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import top.theillusivec4.polymorph.api.common.base.PolymorphPacketDistributor;
import top.theillusivec4.polymorph.api.common.base.RecipePair;
import top.theillusivec4.polymorph.common.network.PolymorphPackets;

public class PolymorphPacketDistributorImpl implements PolymorphPacketDistributor {

  @Override
  public void sendPlayerRecipeSelectionC2S(Identifier pIdentifier) {
    PacketByteBuf buf = PacketByteBufs.create();
    buf.writeIdentifier(pIdentifier);
    ClientPlayNetworking.send(PolymorphPackets.PLAYER_SELECT, buf);
  }

  @Override
  public void sendPersistentRecipeSelectionC2S(Identifier pIdentifier) {
    PacketByteBuf buf = PacketByteBufs.create();
    buf.writeIdentifier(pIdentifier);
    ClientPlayNetworking.send(PolymorphPackets.PERSISTENT_SELECT, buf);
  }

  @Override
  public void sendStackRecipeSelectionC2S(Identifier pIdentifier) {
    PacketByteBuf buf = PacketByteBufs.create();
    buf.writeIdentifier(pIdentifier);
    ClientPlayNetworking.send(PolymorphPackets.STACK_SELECT, buf);
  }

  @Override
  public void sendRecipesListS2C(ServerPlayerEntity pPlayer) {
    sendRecipesListS2C(pPlayer, new TreeSet<>());
  }

  @Override
  public void sendRecipesListS2C(ServerPlayerEntity pPlayer, SortedSet<RecipePair> pRecipesList) {
    sendRecipesListS2C(pPlayer, pRecipesList, new Identifier(""));
  }

  @Override
  public void sendRecipesListS2C(ServerPlayerEntity pPlayer, SortedSet<RecipePair> pRecipesList,
                                 Identifier pSelected) {
    PacketByteBuf buf = PacketByteBufs.create();

    if (!pRecipesList.isEmpty()) {
      buf.writeInt(pRecipesList.size());

      for (RecipePair data : pRecipesList) {
        buf.writeIdentifier(data.getIdentifier());
        buf.writeItemStack(data.getOutput());
      }

      if (pSelected != null) {
        buf.writeIdentifier(pSelected);
      }
    }
    ServerPlayNetworking.send(pPlayer, PolymorphPackets.RECIPES_LIST, buf);
  }

  @Override
  public void sendHighlightRecipeS2C(ServerPlayerEntity pPlayer, Identifier pIdentifier) {
    PacketByteBuf buf = PacketByteBufs.create();
    buf.writeIdentifier(pIdentifier);
    ServerPlayNetworking.send(pPlayer, PolymorphPackets.HIGHLIGHT_RECIPE, buf);
  }

  @Override
  public void sendPlayerSyncS2C(ServerPlayerEntity pPlayer, SortedSet<RecipePair> pRecipesList,
                                Identifier pSelected) {
    PacketByteBuf buf = PacketByteBufs.create();

    if (!pRecipesList.isEmpty()) {
      buf.writeInt(pRecipesList.size());

      for (RecipePair data : pRecipesList) {
        buf.writeIdentifier(data.getIdentifier());
        buf.writeItemStack(data.getOutput());
      }

      if (pSelected != null) {
        buf.writeIdentifier(pSelected);
      }
    }
    ServerPlayNetworking.send(pPlayer, PolymorphPackets.RECIPE_SYNC, buf);
  }
}
