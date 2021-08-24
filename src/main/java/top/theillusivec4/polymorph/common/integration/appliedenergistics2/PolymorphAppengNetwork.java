package top.theillusivec4.polymorph.common.integration.appliedenergistics2;

import appeng.container.me.items.CraftingTermContainer;
import appeng.container.me.items.PatternTermContainer;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import top.theillusivec4.polymorph.common.integration.PolymorphCompatibilityPackets;
import top.theillusivec4.polymorph.common.util.CraftingPlayers;
import top.theillusivec4.polymorph.mixin.integration.AccessorCraftingTermContainer;
import top.theillusivec4.polymorph.mixin.integration.AccessorPatternTermContainer;

public class PolymorphAppengNetwork {

  public static void setup() {
    ServerPlayNetworking.registerGlobalReceiver(PolymorphCompatibilityPackets.SELECT_AE2_CRAFT,
        (minecraftServer, serverPlayerEntity, serverPlayNetworkHandler, packetByteBuf, packetSender) -> {
          Identifier id = packetByteBuf.readIdentifier();
          minecraftServer.execute(() -> {
            CraftingPlayers.add(serverPlayerEntity, id);
            ScreenHandler screenHandler = serverPlayerEntity.currentScreenHandler;

            if (screenHandler instanceof CraftingTermContainer) {
              ((AccessorCraftingTermContainer) screenHandler).setCurrentRecipe(null);
              serverPlayerEntity.currentScreenHandler.onContentChanged(
                  serverPlayerEntity.currentScreenHandler.slots.get(0).inventory);
              MinecraftServer server = serverPlayerEntity.getServer();

              if (server != null) {
                PacketByteBuf buf = PacketByteBufs.create();
                buf.writeIdentifier(id);

                for (ServerPlayerEntity playerEntity : PlayerLookup.all(server)) {
                  ServerPlayNetworking.send(playerEntity,
                      PolymorphCompatibilityPackets.UPDATE_AE2_CRAFT, buf);
                }
              }
            }
          });
        });
    ServerPlayNetworking.registerGlobalReceiver(PolymorphCompatibilityPackets.SELECT_AE2_PATTERN,
        (minecraftServer, serverPlayerEntity, serverPlayNetworkHandler, packetByteBuf, packetSender) -> {
          Identifier id = packetByteBuf.readIdentifier();
          minecraftServer.execute(() -> {
            CraftingPlayers.add(serverPlayerEntity, id);
            ScreenHandler screenHandler = serverPlayerEntity.currentScreenHandler;

            if (screenHandler instanceof PatternTermContainer) {
              ((AccessorPatternTermContainer) screenHandler).setCurrentRecipe(null);
              ((AccessorPatternTermContainer) screenHandler).callGetAndUpdateOutput();
              MinecraftServer server = serverPlayerEntity.getServer();

              if (server != null) {
                PacketByteBuf buf = PacketByteBufs.create();
                buf.writeIdentifier(id);

                for (ServerPlayerEntity playerEntity : PlayerLookup.all(server)) {
                  ServerPlayNetworking.send(playerEntity,
                      PolymorphCompatibilityPackets.UPDATE_AE2_PATTERN, buf);
                }
              }
            }
          });
        });
  }
}
