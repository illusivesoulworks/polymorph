package top.theillusivec4.polymorph.common.util;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import top.theillusivec4.polymorph.common.network.PolymorphPackets;

public class CraftingPlayers {

  private static final Cache<UUID, Identifier> PLAYER_TO_RECIPE =
      CacheBuilder.newBuilder().maximumSize(1000).expireAfterAccess(1, TimeUnit.HOURS).build();

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
    return Optional.ofNullable(PLAYER_TO_RECIPE.getIfPresent(playerEntity.getUuid()));
  }
}
