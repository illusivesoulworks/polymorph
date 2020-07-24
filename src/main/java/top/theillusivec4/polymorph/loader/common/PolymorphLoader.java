package top.theillusivec4.polymorph.loader.common;

import java.io.File;
import net.fabricmc.loader.api.FabricLoader;
import top.theillusivec4.polymorph.core.base.common.Accessor;
import top.theillusivec4.polymorph.core.base.common.Loader;
import top.theillusivec4.polymorph.core.base.common.PacketVendor;
import top.theillusivec4.polymorph.loader.impl.AccessorImpl;
import top.theillusivec4.polymorph.loader.impl.PacketVendorImpl;

public class PolymorphLoader implements Loader {

  private static final PacketVendor PACKET_VENDOR = new PacketVendorImpl();
  private static final Accessor ACCESSOR = new AccessorImpl();

  @Override
  public PacketVendor getPacketVendor() {
    return PACKET_VENDOR;
  }

  @Override
  public Accessor getAccessor() {
    return ACCESSOR;
  }

  @Override
  public File getGameDirectory() {
    return FabricLoader.getInstance().getGameDirectory();
  }
}
