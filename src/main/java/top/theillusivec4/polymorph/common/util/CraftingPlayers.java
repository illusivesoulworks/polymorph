package top.theillusivec4.polymorph.common.util;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import top.theillusivec4.polymorph.common.network.PolymorphPackets;

public class CraftingPlayers {

  private static final Map<UUID, Identifier> PLAYER_TO_RECIPE = new HashMap<>();

  public static void add(PlayerEntity playerEntity, Identifier recipe) {
    add(playerEntity.getUuid(), recipe);

    if (!playerEntity.getEntityWorld().isClient() && playerEntity.getServer() != null) {

      for (ServerPlayerEntity serverPlayerEntity : PlayerLookup.all(playerEntity.getServer())) {
        PacketByteBuf buf = PacketByteBufs.create();
        buf.writeUuid(playerEntity.getUuid());
        buf.writeIdentifier(recipe);
        ServerPlayNetworking.send(serverPlayerEntity, PolymorphPackets.ADD_CRAFTER, buf);
      }
    }
  }

  public static void add(UUID uuid, Identifier recipe) {
    PLAYER_TO_RECIPE.put(uuid, recipe);
  }

  public static Optional<Identifier> getRecipe(PlayerEntity playerEntity) {
    return Optional.ofNullable(PLAYER_TO_RECIPE.get(playerEntity.getUuid()));
  }

  public static void remove(PlayerEntity playerEntity) {
    remove(playerEntity.getUuid());

    if (!playerEntity.getEntityWorld().isClient() && playerEntity.getServer() != null) {

      for (ServerPlayerEntity serverPlayerEntity : PlayerLookup.all(playerEntity.getServer())) {
        PacketByteBuf buf = PacketByteBufs.create();
        buf.writeUuid(playerEntity.getUuid());
        ServerPlayNetworking.send(serverPlayerEntity, PolymorphPackets.REMOVE_CRAFTER, buf);
      }
    }
  }

  public static void remove(UUID uuid) {
    PLAYER_TO_RECIPE.remove(uuid);
  }
}
