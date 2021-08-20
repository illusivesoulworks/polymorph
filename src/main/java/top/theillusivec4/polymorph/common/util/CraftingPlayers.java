package top.theillusivec4.polymorph.common.util;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

public class CraftingPlayers {

  private static final Map<UUID, Identifier> PLAYER_TO_RECIPE = new HashMap<>();

  public static void add(ServerPlayerEntity playerEntity, Identifier recipe) {
    PLAYER_TO_RECIPE.put(playerEntity.getUuid(), recipe);
  }

  public static Optional<Identifier> getRecipe(ServerPlayerEntity playerEntity) {
    return Optional.ofNullable(PLAYER_TO_RECIPE.get(playerEntity.getUuid()));
  }

  public static void remove(ServerPlayerEntity playerEntity) {
    PLAYER_TO_RECIPE.remove(playerEntity.getUuid());
  }
}
