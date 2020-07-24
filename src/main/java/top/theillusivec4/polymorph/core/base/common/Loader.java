package top.theillusivec4.polymorph.core.base.common;

import java.io.File;

public interface Loader {

  File getGameDirectory();

  PacketVendor getPacketVendor();

  Accessor getAccessor();

}
