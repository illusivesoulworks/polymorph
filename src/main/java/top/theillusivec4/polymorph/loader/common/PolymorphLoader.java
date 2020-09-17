/*
 * Copyright (c) 2020 C4
 *
 * This file is part of Polymorph, a mod made for Minecraft.
 *
 * Polymorph is free software: you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published
 * by the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Polymorph is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with Polymorph.  If not, see <https://www.gnu.org/licenses/>.
 */

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
    return FabricLoader.getInstance().getGameDir().toFile();
  }
}
