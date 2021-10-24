package top.theillusivec4.polymorph.common.integration.refinedstorage;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.event.server.FMLServerStartedEvent;
import net.minecraftforge.fml.event.server.FMLServerStoppedEvent;
import top.theillusivec4.polymorph.common.integration.AbstractCompatibilityModule;
import top.theillusivec4.polymorph.mixin.util.integration.RefinedStorageHooks;

public class RefinedStorageModule extends AbstractCompatibilityModule {

  @Override
  public void setup() {
    MinecraftForge.EVENT_BUS.addListener(this::serverStarted);
    MinecraftForge.EVENT_BUS.addListener(this::serverStopped);
  }

  private void serverStarted(final FMLServerStartedEvent evt) {
    RefinedStorageHooks.loaded = true;
  }

  private void serverStopped(final FMLServerStoppedEvent evt) {
    RefinedStorageHooks.loaded = false;
  }
}
