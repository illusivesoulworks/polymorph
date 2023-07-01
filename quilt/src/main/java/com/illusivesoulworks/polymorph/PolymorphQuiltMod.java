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

package com.illusivesoulworks.polymorph;

import com.illusivesoulworks.polymorph.common.CommonEventsListener;
import com.illusivesoulworks.polymorph.common.PolymorphQuiltNetwork;
import com.illusivesoulworks.polymorph.common.components.PolymorphQuiltComponents;
import com.illusivesoulworks.polymorph.server.PolymorphCommands;
import org.quiltmc.loader.api.ModContainer;
import org.quiltmc.qsl.base.api.entrypoint.ModInitializer;
import org.quiltmc.qsl.command.api.CommandRegistrationCallback;

public class PolymorphQuiltMod implements ModInitializer {

  @Override
  public void onInitialize(ModContainer modContainer) {
    PolymorphCommonMod.init();
    PolymorphCommonMod.setup();
    PolymorphQuiltNetwork.setup();
    PolymorphQuiltComponents.setup();
    CommandRegistrationCallback.EVENT.register(
        (dispatcher, registryAccess, environment) -> PolymorphCommands.register(dispatcher));
    CommonEventsListener.setup();
  }
}
