package top.theillusivec4.polymorph.loader.network;

import net.fabricmc.fabric.api.network.ServerSidePacketRegistry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.server.MinecraftServer;
import top.theillusivec4.polymorph.api.PolymorphApi;
import top.theillusivec4.polymorph.core.client.RecipeSelectionManager;

public class NetworkHandler {

  public static void setup() {
    ServerSidePacketRegistry.INSTANCE
        .register(NetworkPackets.SYNC_OUTPUT, (((packetContext, packetByteBuf) -> {
          ItemStack stack = packetByteBuf.readItemStack();
          packetContext.getTaskQueue().execute(() -> {
            PlayerEntity playerEntity = packetContext.getPlayer();
            MinecraftServer server = playerEntity.getServer();

            if (server != null) {
              ScreenHandler screenHandler = playerEntity.currentScreenHandler;
              PolymorphApi.getProvider(screenHandler).ifPresent(provider -> {
                Slot slot = provider.getOutputSlot();
                slot.inventory.setStack(slot.id, stack);
              });
              RecipeSelectionManager.getInstance().ifPresent(RecipeSelectionManager::unlockUpdates);
            }
          });
        })));
  }

}
