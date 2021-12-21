package top.theillusivec4.polymorph.common;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.concurrent.ConcurrentHashMap;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.Pair;
import net.minecraft.world.World;
import top.theillusivec4.polymorph.api.PolymorphApi;
import top.theillusivec4.polymorph.api.common.base.PolymorphCommon;
import top.theillusivec4.polymorph.api.common.base.PolymorphPacketDistributor;
import top.theillusivec4.polymorph.api.common.base.RecipePair;
import top.theillusivec4.polymorph.api.common.component.BlockEntityRecipeData;
import top.theillusivec4.polymorph.common.integration.AbstractCompatibilityModule;
import top.theillusivec4.polymorph.common.integration.PolymorphIntegrations;

public class CommonEventsListener {

  private static final Map<BlockEntity, BlockEntityRecipeData> TICKABLE_TILES =
      new ConcurrentHashMap<>();

  public static void setup() {
    ServerLifecycleEvents.SERVER_STARTING.register(CommonEventsListener::serverStarting);
    ServerLifecycleEvents.SERVER_STOPPED.register(CommonEventsListener::serverStopped);
    ServerTickEvents.END_WORLD_TICK.register(CommonEventsListener::worldTick);
  }

  public static void openScreenHandler(ServerPlayerEntity player) {
    ScreenHandler screenHandler = player.currentScreenHandler;
    PolymorphCommon commonApi = PolymorphApi.common();
    commonApi.getRecipeDataFromBlockEntity(screenHandler).ifPresent(
        recipeData -> {
          PolymorphPacketDistributor packetDistributor = commonApi.getPacketDistributor();

          if (recipeData.isFailing() || recipeData.isEmpty(null)) {
            packetDistributor.sendRecipesListS2C(player);
          } else {
            Pair<SortedSet<RecipePair>, Identifier> data = recipeData.getPacketData();
            packetDistributor.sendRecipesListS2C(player, data.getLeft(),
                data.getRight());
          }
        });

    for (AbstractCompatibilityModule integration : PolymorphIntegrations.get()) {

      if (integration.openScreenHandler(screenHandler, player)) {
        return;
      }
    }
  }

  private static void worldTick(final ServerWorld serverWorld) {
    List<BlockEntity> toRemove = new ArrayList<>();

    for (Map.Entry<BlockEntity, BlockEntityRecipeData> entry : TICKABLE_TILES.entrySet()) {
      BlockEntity be = entry.getKey();
      World beWorld = be.getWorld();

      if (be.isRemoved() || beWorld == null || beWorld.isClient()) {
        toRemove.add(be);
      } else {
        entry.getValue().tick();
      }
    }

    for (BlockEntity be : toRemove) {
      TICKABLE_TILES.remove(be);
    }
  }

  private static void serverStopped(final MinecraftServer server) {
    PolymorphApi.common().setServer(null);
    TICKABLE_TILES.clear();
  }

  private static void serverStarting(final MinecraftServer server) {
    PolymorphApi.common().setServer(server);
    TICKABLE_TILES.clear();
  }
}
