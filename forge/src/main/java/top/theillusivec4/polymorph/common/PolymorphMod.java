package top.theillusivec4.polymorph.common;

import com.google.common.collect.ImmutableSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
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
import top.theillusivec4.polymorph.common.integration.craftingstation.CraftingStationModule;
import top.theillusivec4.polymorph.common.integration.fastbench.FastBenchModule;
import top.theillusivec4.polymorph.common.integration.fastfurnace.FastFurnaceModule;
import top.theillusivec4.polymorph.common.integration.prettypipes.PrettyPipesModule;
import top.theillusivec4.polymorph.common.integration.refinedstorage.RefinedStorageModule;
import top.theillusivec4.polymorph.common.integration.simplestoragenetwork.SimpleStorageNetworkModule;
import top.theillusivec4.polymorph.common.integration.tinkersconstruct.TinkersConstructModule;
import top.theillusivec4.polymorph.common.integration.tomsstorage.TomsStorageModule;
import top.theillusivec4.polymorph.common.network.PolymorphNetwork;
import top.theillusivec4.polymorph.server.PolymorphCommands;

@Mod(PolymorphMod.MOD_ID)
public class PolymorphMod {

  private static final Map<String, Supplier<AbstractCompatibilityModule>> INTEGRATIONS =
      new HashMap<>();
  private static final Set<AbstractCompatibilityModule> ACTIVE_INTEGRATIONS = new HashSet<>();

  public static final String MOD_ID = "polymorph";
  public static final Logger LOGGER = LogManager.getLogger();

  static {
    INTEGRATIONS.put("prettypipes", PrettyPipesModule::new);
    INTEGRATIONS.put("toms_storage", TomsStorageModule::new);
    INTEGRATIONS.put("storagenetwork", SimpleStorageNetworkModule::new);
    INTEGRATIONS.put("refinedstorage", RefinedStorageModule::new);
    INTEGRATIONS.put("tconstruct", TinkersConstructModule::new);
    INTEGRATIONS.put("fastbench", FastBenchModule::new);
    INTEGRATIONS.put("craftingstation", CraftingStationModule::new);
    INTEGRATIONS.put("fastfurnace", FastFurnaceModule::new);
  }

  public PolymorphMod() {
    IEventBus eventBus = FMLJavaModLoadingContext.get().getModEventBus();
    eventBus.addListener(this::setup);
    eventBus.addListener(this::clientSetup);
    MinecraftForge.EVENT_BUS.addListener(this::registerCommand);
    ModList modList = ModList.get();
    INTEGRATIONS.forEach((modid, supplier) -> {

      if (modList.isLoaded(modid)) {
        ACTIVE_INTEGRATIONS.add(supplier.get());
      }
    });
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

    for (AbstractCompatibilityModule integration : getIntegrations()) {
      integration.setup();
    }
  }

  private void clientSetup(final FMLClientSetupEvent evt) {
    PolymorphClientMod.setup();
    MinecraftForge.EVENT_BUS.register(new ClientEventsListener());

    for (AbstractCompatibilityModule integration : getIntegrations()) {
      integration.clientSetup();
    }
  }

  private void registerCommand(final RegisterCommandsEvent evt) {
    PolymorphCommands.register(evt.getDispatcher());
  }

  public static Set<AbstractCompatibilityModule> getIntegrations() {
    return ImmutableSet.copyOf(ACTIVE_INTEGRATIONS);
  }
}
