/*
 * Copyright (c) 2020 C4
 *
 * This file is part of Polymorph, a mod made for Minecraft.
 *
 * Polymorph is free software: you can redistribute it and/or modify it under the terms of the GNU
 * Lesser General Public License as published by the Free Software Foundation, either version 3 of
 * the License, or any later version.
 *
 * Polymorph is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without
 * even the implied warranty of MERCHANTABILITY or FITNESS FOR PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with Polymorph.
 * If not, see <https://www.gnu.org/licenses/>.
 */

package top.theillusivec4.polymorph.client.recipe;

import java.util.Optional;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.inventory.Inventory;
import net.minecraft.recipe.Recipe;
import top.theillusivec4.polymorph.api.PolymorphApi;
import top.theillusivec4.polymorph.api.type.RecipeController;

public class RecipeControllerHub {

  private static RecipeController<? extends Inventory, ? extends Recipe<?>> controller = null;

  public static Optional<RecipeController<? extends Inventory, ? extends Recipe<?>>> getController() {
    return Optional.ofNullable(controller);
  }

  public static boolean startController(HandledScreen<?> screen) {
    return PolymorphApi.getInstance().getRecipeController(screen).map(result -> {
      controller = result;
      return true;
    }).orElse(false);
  }

  public static void clear() {
    controller = null;
  }
}
