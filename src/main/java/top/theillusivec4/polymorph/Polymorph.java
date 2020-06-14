package top.theillusivec4.polymorph;

import net.minecraft.inventory.container.PlayerContainer;
import net.minecraft.inventory.container.WorkbenchContainer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.server.FMLServerStartingEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import top.theillusivec4.polymorph.api.PolymorphApi;
import top.theillusivec4.polymorph.client.ClientEventHandler;
import top.theillusivec4.polymorph.common.integrations.CraftingStationModule;
import top.theillusivec4.polymorph.common.integrations.FastWorkbenchModule;
import top.theillusivec4.polymorph.common.network.NetworkHandler;
import top.theillusivec4.polymorph.common.provider.InventoryProvider;
import top.theillusivec4.polymorph.common.provider.WorkbenchProvider;
import top.theillusivec4.polymorph.server.PolymorphCommand;

@Mod(Polymorph.MODID)
public class Polymorph {

  public static final String MODID = "polymorph";
  public static final Logger LOGGER = LogManager.getLogger();

  public static boolean isFastBenchLoaded = false;
  public static boolean isCraftingStationLoaded = false;

  public Polymorph() {
    IEventBus eventBus = FMLJavaModLoadingContext.get().getModEventBus();
    eventBus.addListener(this::setup);
    eventBus.addListener(this::clientSetup);
    MinecraftForge.EVENT_BUS.addListener(this::serverStarting);

    isFastBenchLoaded = ModList.get().isLoaded("fastbench");
    isCraftingStationLoaded = ModList.get().isLoaded("craftingstation");
  }

  private void setup(final FMLCommonSetupEvent evt) {
    NetworkHandler.register();
    PolymorphApi.addProvider(WorkbenchContainer.class, new WorkbenchProvider());
    PolymorphApi.addProvider(PlayerContainer.class, new InventoryProvider());

    if (isFastBenchLoaded) {
      FastWorkbenchModule.setup();
    }

    if (isCraftingStationLoaded) {
      CraftingStationModule.setup();
    }
  }

  private void clientSetup(final FMLClientSetupEvent evt) {
    MinecraftForge.EVENT_BUS.register(new ClientEventHandler());

    if (isCraftingStationLoaded) {
      CraftingStationModule.Client.clientSetup();
    }
  }

  private void serverStarting(final FMLServerStartingEvent evt) {
    PolymorphCommand.register(evt.getCommandDispatcher());
  }
}
