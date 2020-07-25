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

package top.theillusivec4.polymorph.loader.network;

import net.minecraft.util.Identifier;
import top.theillusivec4.polymorph.core.Polymorph;

public class NetworkPackets {

  public static final Identifier SET_RECIPE = new Identifier(Polymorph.MODID, "set_recipe");
  public static final Identifier TRANSFER_RECIPE = new Identifier(Polymorph.MODID, "transfer_recipe");
  public static final Identifier SYNC_OUTPUT = new Identifier(Polymorph.MODID, "sync_output");
}
