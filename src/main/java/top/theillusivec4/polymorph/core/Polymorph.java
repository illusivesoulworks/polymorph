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

package top.theillusivec4.polymorph.core;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import top.theillusivec4.polymorph.core.base.client.ClientLoader;
import top.theillusivec4.polymorph.core.base.common.Loader;
import top.theillusivec4.polymorph.loader.client.PolymorphClientLoader;
import top.theillusivec4.polymorph.loader.common.PolymorphLoader;

public class Polymorph {

  public static final String MODID = "polymorph";
  public static final Logger LOGGER = LogManager.getLogger();

  private static Loader loader;
  private static ClientLoader clientLoader;

  public static Loader getLoader() {

    if (loader == null) {
      loader = new PolymorphLoader();
    }
    return loader;
  }

  public static ClientLoader getClientLoader() {

    if (clientLoader == null) {
      clientLoader = new PolymorphClientLoader();
    }
    return clientLoader;
  }
}
