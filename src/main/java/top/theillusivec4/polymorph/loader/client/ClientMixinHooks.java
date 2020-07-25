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

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.util.math.MatrixStack;
import top.theillusivec4.polymorph.api.PolymorphApi;
import top.theillusivec4.polymorph.core.client.RecipeSelectionManager;

public class ClientMixinHooks {

  public static void initConflictManager(Screen screen) {
    RecipeSelectionManager conflictManager = null;

    if (screen instanceof HandledScreen) {
      HandledScreen<?> containerScreen = (HandledScreen<?>) screen;
      conflictManager = PolymorphApi.getProvider(containerScreen.getScreenHandler())
          .map(provider -> {

            if (provider.getCraftingInventory() != null && provider.isActive()) {
              return RecipeSelectionManager.createInstance(containerScreen, provider);
            }
            return null;
          }).orElse(null);
    }

    if (conflictManager == null) {
      RecipeSelectionManager.clearInstance();
    }
  }

  public static void renderConflictManager(Screen screen, MatrixStack matrices, int mouseX,
      int mouseY, float delta) {

    if (screen instanceof HandledScreen) {
      RecipeSelectionManager.getInstance()
          .ifPresent(conflictManager -> conflictManager.render(matrices, mouseX, mouseY, delta));
    }
  }

  public static boolean clickConflictManager(Screen screen, double mouseX, double mouseY,
      int button) {

    if (screen instanceof HandledScreen) {
      RecipeSelectionManager.getInstance().ifPresent(RecipeSelectionManager::markPositionChanged);

      return RecipeSelectionManager.getInstance()
          .map(conflictManager -> conflictManager.mouseClicked(mouseX, mouseY, button))
          .orElse(false);
    }
    return false;
  }
}
