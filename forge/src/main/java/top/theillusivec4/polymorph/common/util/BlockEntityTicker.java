package top.theillusivec4.polymorph.common.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.entity.BlockEntity;
import top.theillusivec4.polymorph.api.common.capability.IBlockEntityRecipeData;

public class BlockEntityTicker {

  private static final Map<BlockEntity, IBlockEntityRecipeData> TICKABLE_BLOCKS =
      new HashMap<>();

  public static void tick() {
    List<BlockEntity> toRemove = new ArrayList<>();

    for (Map.Entry<BlockEntity, IBlockEntityRecipeData> entry : TICKABLE_BLOCKS.entrySet()) {
      BlockEntity be = entry.getKey();

      if (be.isRemoved() || (be.getLevel() != null && be.getLevel().isClientSide())) {
        toRemove.add(be);
      } else {
        entry.getValue().tick();
      }
    }

    for (BlockEntity be : toRemove) {
      TICKABLE_BLOCKS.remove(be);
    }
  }

  public static void add(ServerPlayer serverPlayer, IBlockEntityRecipeData recipeData) {
    IBlockEntityRecipeData data = TICKABLE_BLOCKS.get(recipeData.getOwner());

    if (data != null) {
      data.addListener(serverPlayer);
    } else {
      recipeData.addListener(serverPlayer);
      TICKABLE_BLOCKS.put(recipeData.getOwner(), recipeData);
    }
  }

  public static void remove(ServerPlayer serverPlayer) {
    List<BlockEntity> toRemove = new ArrayList<>();

    for (Map.Entry<BlockEntity, IBlockEntityRecipeData> entry : TICKABLE_BLOCKS.entrySet()) {
      entry.getValue().removeListener(serverPlayer);

      if (entry.getValue().getListeners().isEmpty()) {
        toRemove.add(entry.getKey());
      }
    }

    for (BlockEntity blockEntity : toRemove) {
      TICKABLE_BLOCKS.remove(blockEntity);
    }
  }
}
