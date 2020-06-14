package top.theillusivec4.polymorph.common.integrations;

import com.tfar.craftingstation.CraftingStationContainer;
import com.tfar.craftingstation.client.CraftingStationScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.inventory.CraftResultInventory;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.inventory.container.Slot;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent.ClientTickEvent;
import net.minecraftforge.event.TickEvent.Phase;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import top.theillusivec4.polymorph.api.PolymorphApi;
import top.theillusivec4.polymorph.api.PolymorphApi.IProvider;
import top.theillusivec4.polymorph.client.RecipeConflictManager;

public class CraftingStationModule {

  public static void setup() {
    PolymorphApi.addProvider(CraftingStationContainer.class, new CraftingStationProvider());
  }

  public static class Client {

    public static void clientSetup() {
      MinecraftForge.EVENT_BUS.register(new Client());
    }

    @SubscribeEvent
    public void tick(ClientTickEvent evt) {

      if (evt.phase == Phase.END) {
        ClientPlayerEntity clientPlayerEntity = Minecraft.getInstance().player;

        if (clientPlayerEntity != null) {
          World world = clientPlayerEntity.world;

          if (world != null
              && clientPlayerEntity.openContainer instanceof CraftingStationContainer) {
            RecipeConflictManager.getInstance()
                .ifPresent(RecipeConflictManager::onCraftMatrixChanged);
          }
        }
      }
    }

    public static RecipeConflictManager<?> getConflictManager(ContainerScreen<?> screen) {
      RecipeConflictManager<?> conflictManager = null;

      if (screen instanceof CraftingStationScreen && screen
          .getContainer() instanceof CraftingStationContainer) {
        CraftingStationScreen craftingStationScreen = (CraftingStationScreen) screen;
        CraftingStationContainer craftingStationContainer = (CraftingStationContainer) screen
            .getContainer();
        conflictManager = PolymorphApi.getProvider(craftingStationContainer)
            .map(provider -> RecipeConflictManager.refreshInstance(craftingStationScreen, provider))
            .orElse(null);
      }
      return conflictManager;
    }
  }

  public static class CraftingStationProvider implements IProvider<CraftingStationContainer> {

    @Override
    public CraftingInventory getCraftingMatrix(CraftingStationContainer container) {

      for (Slot slot : container.inventorySlots) {

        if (slot.inventory instanceof CraftingInventory) {
          return (CraftingInventory) slot.inventory;
        }
      }
      return null;
    }

    @Override
    public Slot getOutputSlot(CraftingStationContainer container) {

      for (Slot slot : container.inventorySlots) {

        if (slot.inventory instanceof CraftResultInventory) {
          return slot;
        }
      }
      return container.inventorySlots.get(0);
    }

    @Override
    public int getXOffset() {
      return 36;
    }

    @Override
    public int getYOffset() {
      return -72;
    }
  }
}
