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

package com.illusivesoulworks.polymorph.client;

import org.quiltmc.qsl.lifecycle.api.client.event.ClientTickEvents;
import org.quiltmc.qsl.screen.api.client.ScreenEvents;
import org.quiltmc.qsl.screen.api.client.ScreenMouseEvents;

public class ClientEventsListener {

  public static void setup() {
    ClientTickEvents.END.register(client -> PolymorphClientEvents.tick());
    ScreenEvents.AFTER_INIT.register((screen, client, firstInit) -> {
      PolymorphClientEvents.initScreen(screen);
    });
    ScreenEvents.AFTER_RENDER.register(PolymorphClientEvents::render);
    ScreenMouseEvents.BEFORE_MOUSE_CLICK.register(PolymorphClientEvents::mouseClick);
  }
}
