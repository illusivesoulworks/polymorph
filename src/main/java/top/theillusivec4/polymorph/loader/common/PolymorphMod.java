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

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback;
import net.minecraft.screen.CraftingScreenHandler;
import net.minecraft.screen.PlayerScreenHandler;
import top.theillusivec4.polymorph.api.PolymorphApi;
import top.theillusivec4.polymorph.core.provider.CraftingProvider;
import top.theillusivec4.polymorph.core.provider.PlayerProvider;
import top.theillusivec4.polymorph.loader.network.NetworkHandler;
import top.theillusivec4.polymorph.loader.server.PolymorphCommands;

public class PolymorphMod implements ModInitializer {

  @Override
  public void onInitialize() {
    PolymorphApi.addProvider(CraftingScreenHandler.class, CraftingProvider::new);
    PolymorphApi.addProvider(PlayerScreenHandler.class, PlayerProvider::new);
    NetworkHandler.setup();
    CommandRegistrationCallback.EVENT
        .register((commandDispatcher, b) -> PolymorphCommands.register(commandDispatcher));
  }
}
