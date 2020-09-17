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

package top.theillusivec4.polymorph.loader.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import top.theillusivec4.polymorph.core.client.RecipeSelectionManager;
import top.theillusivec4.polymorph.loader.network.ClientNetworkHandler;

public class PolymorphClientMod implements ClientModInitializer {

  @Override
  public void onInitializeClient() {
    ClientNetworkHandler.setup();
    ClientTickEvents.END_CLIENT_TICK.register(
        client -> RecipeSelectionManager.getInstance().ifPresent(RecipeSelectionManager::tick));
  }
}
