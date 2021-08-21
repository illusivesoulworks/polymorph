package top.theillusivec4.polymorph.common;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.recipe.Recipe;
import net.minecraft.screen.AbstractFurnaceScreenHandler;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import net.minecraft.world.World;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import top.theillusivec4.polymorph.api.PolymorphComponents;
import top.theillusivec4.polymorph.common.network.PolymorphPackets;
import top.theillusivec4.polymorph.common.util.CraftingPlayers;
import top.theillusivec4.polymorph.mixin.core.AccessorAbstractFurnaceScreenHandler;

public class PolymorphMod implements ModInitializer {

  public static final String MOD_ID = "polymorph";
  public static final Logger LOGGER = LogManager.getLogger();

  @Override
  public void onInitialize() {
    ServerPlayNetworking.registerGlobalReceiver(PolymorphPackets.GET_RECIPES,
        (minecraftServer, serverPlayerEntity, serverPlayNetworkHandler, packetByteBuf, packetSender) -> {
          minecraftServer.execute(() -> {
            World world = serverPlayerEntity.getEntityWorld();
            ScreenHandler screenHandler = serverPlayerEntity.currentScreenHandler;

            if (screenHandler instanceof AbstractFurnaceScreenHandler) {
              Inventory inventory =
                  ((AccessorAbstractFurnaceScreenHandler) screenHandler).getInventory();

              if (inventory instanceof BlockEntity) {
                PolymorphComponents.BLOCK_ENTITY_RECIPE_SELECTOR.maybeGet((BlockEntity) inventory)
                    .ifPresent(selector -> {
                      List<? extends Recipe<?>> recipes = world.getRecipeManager().values().stream()
                          .filter((val) -> val.getType() == selector.getRecipeType())
                          .flatMap((val) -> Util.stream(selector.getRecipeType()
                              .get((Recipe<Inventory>) val, world,
                                  (Inventory) selector.getParent())))
                          .sorted(Comparator
                              .comparing((recipe) -> recipe.getOutput().getTranslationKey()))
                          .collect(Collectors.toList());
                      PacketByteBuf buf = PacketByteBufs.create();

                      if (!recipes.isEmpty()) {
                        buf.writeIdentifier(selector.getSelectedRecipe().map(Recipe::getId)
                            .orElse(recipes.get(0).getId()));

                        for (Recipe<?> recipe : recipes) {
                          buf.writeString(recipe.getId().toString());
                        }
                      }
                      ServerPlayNetworking
                          .send(serverPlayerEntity, PolymorphPackets.SEND_RECIPES, buf);
                    });
              }
            }
          });
        });
    ServerPlayNetworking.registerGlobalReceiver(PolymorphPackets.SELECT_CRAFT,
        (minecraftServer, serverPlayerEntity, serverPlayNetworkHandler, packetByteBuf, packetSender) -> {
          Identifier id = packetByteBuf.readIdentifier();
          minecraftServer.execute(() -> {
            CraftingPlayers.add(serverPlayerEntity, id);
            serverPlayerEntity.currentScreenHandler.onContentChanged(serverPlayerEntity.inventory);
          });
        });
    ServerPlayNetworking.registerGlobalReceiver(PolymorphPackets.SELECT_PERSIST,
        (minecraftServer, serverPlayerEntity, serverPlayNetworkHandler, packetByteBuf, packetSender) -> {
          Identifier id = packetByteBuf.readIdentifier();
          minecraftServer.execute(() -> {
            World world = serverPlayerEntity.getEntityWorld();
            Optional<? extends Recipe<?>> recipe = world.getRecipeManager().get(id);
            recipe.ifPresent(res -> {
              ScreenHandler screenHandler = serverPlayerEntity.currentScreenHandler;

              if (screenHandler instanceof AbstractFurnaceScreenHandler) {
                Inventory inventory =
                    ((AccessorAbstractFurnaceScreenHandler) screenHandler).getInventory();

                if (inventory instanceof BlockEntity) {
                  PolymorphComponents.BLOCK_ENTITY_RECIPE_SELECTOR.maybeGet((BlockEntity) inventory)
                      .ifPresent(selector -> selector.setSelectedRecipe(res));
                }
              }
            });
          });
        });
  }
}
