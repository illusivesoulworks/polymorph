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

package top.theillusivec4.polymorph.api;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import net.minecraft.screen.CraftingScreenHandler;
import net.minecraft.screen.PlayerScreenHandler;
import net.minecraft.screen.ScreenHandler;

public class PolymorphApi {

  private static final Map<Class<? extends ScreenHandler>, Function<? extends ScreenHandler, PolyProvider>> providerFunctions = new HashMap<>();

  public static <T extends ScreenHandler> void addProvider(Class<T> clazz,
      Function<T, PolyProvider> providerFunction) {
    providerFunctions.put(clazz, providerFunction);
  }

  @SuppressWarnings("unchecked")
  public static <T extends ScreenHandler> Optional<PolyProvider> getProvider(T screenHandler) {
    Function<T, PolyProvider> providerFunction = (Function<T, PolyProvider>) providerFunctions
        .get(screenHandler.getClass());

    if (providerFunction == null) {

      if (screenHandler instanceof CraftingScreenHandler) {
        providerFunction = (Function<T, PolyProvider>) providerFunctions
            .get(CraftingScreenHandler.class);
      } else if (screenHandler instanceof PlayerScreenHandler) {
        providerFunction = (Function<T, PolyProvider>) providerFunctions
            .get(PlayerScreenHandler.class);
      }
    }
    return providerFunction != null ? Optional.of(providerFunction.apply(screenHandler))
        : Optional.empty();
  }
}
