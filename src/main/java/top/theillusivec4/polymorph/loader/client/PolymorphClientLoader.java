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

import top.theillusivec4.polymorph.core.base.client.ClientAccessor;
import top.theillusivec4.polymorph.core.base.client.ClientLoader;
import top.theillusivec4.polymorph.loader.impl.ClientAccessorImpl;

public class PolymorphClientLoader implements ClientLoader {

  private static final ClientAccessor ACCESSOR = new ClientAccessorImpl();

  @Override
  public ClientAccessor getClientAccessor() {
    return ACCESSOR;
  }
}
