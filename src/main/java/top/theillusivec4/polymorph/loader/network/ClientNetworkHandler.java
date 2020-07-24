package top.theillusivec4.polymorph.loader.network;

import net.fabricmc.fabric.api.network.ClientSidePacketRegistry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import top.theillusivec4.polymorph.api.PolymorphApi;
import top.theillusivec4.polymorph.core.Polymorph;
import top.theillusivec4.polymorph.core.client.RecipeSelectionManager;

public class ClientNetworkHandler {

  public static void setup() {
    ClientSidePacketRegistry.INSTANCE
        .register(NetworkPackets.SYNC_OUTPUT, (((packetContext, packetByteBuf) -> {
          ItemStack stack = packetByteBuf.readItemStack();
          packetContext.getTaskQueue().execute(() -> {
            PlayerEntity playerEntity = packetContext.getPlayer();
            ScreenHandler screenHandler = playerEntity.currentScreenHandler;
            PolymorphApi.getProvider(screenHandler).ifPresent(provider -> {
              Slot slot = provider.getOutputSlot();
              slot.inventory.setStack(slot.id, stack);
            });
            RecipeSelectionManager.getInstance().ifPresent(manager -> {
              manager.unlockUpdates();
              manager.refreshRecipes(playerEntity.world);
            });
          });
        })));
  }
}
