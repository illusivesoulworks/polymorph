package top.theillusivec4.polymorph.event;

import net.minecraft.client.Minecraft;
import net.minecraft.client.world.ClientWorld;
import net.minecraftforge.event.TickEvent.ClientTickEvent;
import net.minecraftforge.event.TickEvent.Phase;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import top.theillusivec4.polymorph.util.ClientCraftingManager;

public class ClientEventHandler {

  @SubscribeEvent
  public void tick(ClientTickEvent evt) {

    if (evt.phase == Phase.END) {
      ClientWorld world = Minecraft.getInstance().world;

      if (world != null) {
        ClientCraftingManager.getInstance().ifPresent(craftingManager -> {

          if (craftingManager.needsUpdate()) {
            craftingManager.update();
          }
        });
      }
    }
  }
}
