/*
 * Copyright (C) 2020-2022 Illusive Soulworks
 *
 * Polymorph is free software: you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published
 * by the Free Software Foundation, either version 3 of the License, or
 * any later version.
 *
 * Polymorph is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with Polymorph.  If not, see <https://www.gnu.org/licenses/>.
 */

package com.illusivesoulworks.polymorph.common;

import com.illusivesoulworks.polymorph.api.PolymorphApi;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import org.quiltmc.qsl.lifecycle.api.event.ServerLifecycleEvents;
import org.quiltmc.qsl.lifecycle.api.event.ServerWorldTickEvents;
import org.quiltmc.qsl.networking.api.ServerPlayConnectionEvents;

public class CommonEventsListener {

  public static void setup() {
    ServerLifecycleEvents.STARTING.register(CommonEventsListener::serverStarting);
    ServerLifecycleEvents.STOPPED.register(CommonEventsListener::serverStopped);
    ServerWorldTickEvents.END.register((server, world) -> CommonEventsListener.levelTick(world));
    ServerPlayConnectionEvents.DISCONNECT.register(CommonEventsListener::disconnect);
  }

  private static void disconnect(ServerGamePacketListenerImpl serverGamePacketListener,
                                 MinecraftServer server) {
    PolymorphCommonEvents.playerDisconnected(serverGamePacketListener.getPlayer());
  }

  private static void levelTick(final ServerLevel serverLevel) {
    PolymorphCommonEvents.levelTick(serverLevel);
  }

  private static void serverStopped(final MinecraftServer server) {
    PolymorphApi.common().setServer(null);
  }

  private static void serverStarting(final MinecraftServer server) {
    PolymorphApi.common().setServer(server);
  }
}
