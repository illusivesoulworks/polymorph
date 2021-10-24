package top.theillusivec4.polymorph.common.crafting;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.ResourceLocation;
import top.theillusivec4.polymorph.api.PolymorphApi;

public class CraftingPlayers {

  private static final Cache<UUID, ResourceLocation> PLAYER_TO_RECIPE =
      CacheBuilder.newBuilder().maximumSize(1000).expireAfterAccess(1, TimeUnit.HOURS).build();

  public static void add(PlayerEntity playerEntity, ResourceLocation recipe) {
    add(playerEntity.getUniqueID(), recipe);

    if (playerEntity instanceof ServerPlayerEntity) {
      PolymorphApi.common().getPacketDistributor()
          .sendCraftingActionS2C((ServerPlayerEntity) playerEntity, recipe, true);
    }
  }

  public static void add(UUID uuid, ResourceLocation recipe) {
    PLAYER_TO_RECIPE.put(uuid, recipe);
  }

  public static Optional<ResourceLocation> getRecipe(PlayerEntity playerEntity) {
    return Optional.ofNullable(PLAYER_TO_RECIPE.getIfPresent(playerEntity.getUniqueID()));
  }

  public static void remove(PlayerEntity playerEntity) {
    remove(playerEntity.getUniqueID());

    if (playerEntity instanceof ServerPlayerEntity) {
      PolymorphApi.common().getPacketDistributor()
          .sendCraftingActionS2C((ServerPlayerEntity) playerEntity, new ResourceLocation(""),
              false);
    }
  }

  public static void remove(UUID uuid) {
    PLAYER_TO_RECIPE.invalidate(uuid);
  }
}
