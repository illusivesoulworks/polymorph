package top.theillusivec4.polymorph.common;

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
import top.theillusivec4.polymorph.common.network.PolymorphNetwork;
import top.theillusivec4.polymorph.server.PolymorphCommands;

@Mod(PolymorphMod.MOD_ID)
public class PolymorphMod {

  public static final String MOD_ID = "polymorph";
  public static final Logger LOGGER = LogManager.getLogger();

  public static boolean isFastFurnaceLoaded = false;

  public PolymorphMod() {
    IEventBus eventBus = FMLJavaModLoadingContext.get().getModEventBus();
    eventBus.addListener(this::setup);
    eventBus.addListener(this::clientSetup);
    MinecraftForge.EVENT_BUS.addListener(this::registerCommand);
    isFastFurnaceLoaded = ModList.get().isLoaded("fastfurnace");
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
  }

  private void clientSetup(final FMLClientSetupEvent evt) {
    PolymorphClientMod.setup();
    MinecraftForge.EVENT_BUS.register(new ClientEventsListener());
  }

  private void registerCommand(final RegisterCommandsEvent evt) {
    PolymorphCommands.register(evt.getDispatcher());
  }

}
