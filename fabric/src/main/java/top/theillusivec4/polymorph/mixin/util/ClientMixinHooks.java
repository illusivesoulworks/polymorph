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

package top.theillusivec4.polymorph.mixin.util;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.util.math.MatrixStack;
import top.theillusivec4.polymorph.client.recipe.RecipeControllerHub;

public class ClientMixinHooks {

  public static void initSelector(Screen screen) {

    if (!(screen instanceof HandledScreen) ||
        !RecipeControllerHub.startController((HandledScreen<?>) screen)) {
      RecipeControllerHub.clear();
    }
  }

  public static void renderSelector(Screen screen, MatrixStack matrices, int mouseX, int mouseY,
                                    float delta) {

    if (screen instanceof HandledScreen) {
      RecipeControllerHub.getController()
          .ifPresent(recipeSelector -> recipeSelector.render(matrices, mouseX, mouseY, delta));
    }
  }

  public static boolean clickSelector(Screen screen, double mouseX, double mouseY, int button) {

    if (screen instanceof HandledScreen) {
      return RecipeControllerHub.getController()
          .map(selector -> selector.mouseClicked(mouseX, mouseY, button))
          .orElse(false);
    }
    return false;
  }
}
