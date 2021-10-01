package top.theillusivec4.polymorph.common.integration.refinedstorage;

import com.refinedmods.refinedstorage.container.GridContainer;
import com.refinedmods.refinedstorage.tile.grid.GridTile;
import net.minecraft.inventory.CraftResultInventory;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.Slot;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.event.server.FMLServerAboutToStartEvent;
import net.minecraftforge.fml.event.server.FMLServerStartedEvent;
import net.minecraftforge.fml.event.server.FMLServerStoppedEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import top.theillusivec4.polymorph.api.PolymorphApi;
import top.theillusivec4.polymorph.api.PolymorphClientApi;
import top.theillusivec4.polymorph.common.integration.AbstractCompatibilityModule;
import top.theillusivec4.polymorph.common.integration.refinedstorage.network.RefinedStoragePolymorphNetwork;
import top.theillusivec4.polymorph.mixin.util.integration.RefinedStorageHooks;

public class RefinedStorageModule extends AbstractCompatibilityModule {

  @Override
  public void setup() {
    PolymorphApi.getInstance().addTileEntity(tileEntity -> {
      if (tileEntity instanceof GridTile) {
        return new RefinedStorageRecipeSelector((GridTile) tileEntity);
      }
      return null;
    });
    RefinedStoragePolymorphNetwork.register();
    MinecraftForge.EVENT_BUS.addListener(this::serverStarted);
    MinecraftForge.EVENT_BUS.addListener(this::serverStopped);
  }

  private void serverStarted(final FMLServerStartedEvent evt) {
    RefinedStorageHooks.loaded = true;
  }

  private void serverStopped(final FMLServerStoppedEvent evt) {
    RefinedStorageHooks.loaded = false;
  }

  @Override
  public void clientSetup() {
    PolymorphClientApi.getInstance().addRecipeController(containerScreen -> {
      if (containerScreen.getContainer() instanceof GridContainer) {
        Slot resultSlot = null;
        CraftingInventory craftingInventory = null;
        Container container = containerScreen.getContainer();

        for (Slot slot : container.inventorySlots) {

          if (resultSlot == null && slot.inventory instanceof CraftResultInventory) {
            resultSlot = slot;
          } else if (craftingInventory == null && slot.inventory instanceof CraftingInventory) {
            craftingInventory = (CraftingInventory) slot.inventory;
          }

          if (resultSlot != null && craftingInventory != null) {
            break;
          }
        }
        return resultSlot != null && craftingInventory != null ?
            new RefinedStorageRecipeController(containerScreen, craftingInventory, resultSlot) :
            null;
      }
      return null;
    });
  }
}
