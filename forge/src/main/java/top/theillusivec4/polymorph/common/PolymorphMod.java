package top.theillusivec4.polymorph.common;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;
import net.minecraft.tileentity.AbstractFurnaceTileEntity;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import top.theillusivec4.polymorph.api.PolymorphApi;
import top.theillusivec4.polymorph.client.ClientEventsListener;
import top.theillusivec4.polymorph.client.PolymorphClientMod;
import top.theillusivec4.polymorph.common.capability.FurnaceRecipeSelector;
import top.theillusivec4.polymorph.common.capability.PolymorphCapabilityManager;
import top.theillusivec4.polymorph.common.integration.AbstractCompatibilityModule;
import top.theillusivec4.polymorph.common.integration.prettypipes.PrettyPipesModule;
import top.theillusivec4.polymorph.common.integration.refinedstorage.RefinedStorageModule;
import top.theillusivec4.polymorph.common.integration.simplestoragenetwork.SimpleStorageNetworkModule;
import top.theillusivec4.polymorph.common.integration.tomsstorage.TomsStorageModule;
import top.theillusivec4.polymorph.common.network.PolymorphNetwork;
import top.theillusivec4.polymorph.server.PolymorphCommands;

@Mod(PolymorphMod.MOD_ID)
public class PolymorphMod {

  public static final Map<String, Supplier<AbstractCompatibilityModule>> INTEGRATIONS =
      new HashMap<>();

  public static final String MOD_ID = "polymorph";
  public static final Logger LOGGER = LogManager.getLogger();

  public static boolean isFastFurnaceLoaded = false;
  public static boolean isCraftingStationLoaded = false;
  public static boolean isFastBenchLoaded = false;

  static {
    INTEGRATIONS.put("prettypipes", PrettyPipesModule::new);
    INTEGRATIONS.put("toms_storage", TomsStorageModule::new);
    INTEGRATIONS.put("storagenetwork", SimpleStorageNetworkModule::new);
    INTEGRATIONS.put("refinedstorage", RefinedStorageModule::new);
  }

  public PolymorphMod() {
    IEventBus eventBus = FMLJavaModLoadingContext.get().getModEventBus();
    eventBus.addListener(this::setup);
    eventBus.addListener(this::clientSetup);
    MinecraftForge.EVENT_BUS.addListener(this::registerCommand);
    ModList modList = ModList.get();
    isFastFurnaceLoaded = modList.isLoaded("fastfurnace");
    isCraftingStationLoaded = modList.isLoaded("craftingstation");
    isFastBenchLoaded = modList.isLoaded("fastbench");
  }

  private void setup(final FMLCommonSetupEvent evt) {
    PolymorphNetwork.register();
    PolymorphCapabilityManager.register();
    MinecraftForge.EVENT_BUS.register(new CommonEventsListener());
    PolymorphApi.getInstance().addTileEntity(tileEntity -> {

      if (tileEntity instanceof AbstractFurnaceTileEntity) {
        return new FurnaceRecipeSelector((AbstractFurnaceTileEntity) tileEntity);
      }
      return null;
    });
    INTEGRATIONS.forEach((modid, supplier) -> {

      if (ModList.get().isLoaded(modid)) {
        supplier.get().setup();
      }
    });
  }

  private void clientSetup(final FMLClientSetupEvent evt) {
    PolymorphClientMod.setup();
    MinecraftForge.EVENT_BUS.register(new ClientEventsListener());
    INTEGRATIONS.forEach((modid, supplier) -> {

      if (ModList.get().isLoaded(modid)) {
        supplier.get().clientSetup();
      }
    });
  }

  private void registerCommand(final RegisterCommandsEvent evt) {
    PolymorphCommands.register(evt.getDispatcher());
  }

}
