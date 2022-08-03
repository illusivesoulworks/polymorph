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
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;

public class CommonEventsListener {

  public static void setup() {
    ServerLifecycleEvents.SERVER_STARTING.register(CommonEventsListener::serverStarting);
    ServerLifecycleEvents.SERVER_STOPPED.register(CommonEventsListener::serverStopped);
    ServerTickEvents.END_WORLD_TICK.register(CommonEventsListener::levelTick);
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
