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

package com.illusivesoulworks.polymorph.client;

import com.illusivesoulworks.polymorph.api.client.base.ITickingRecipesWidget;
import com.illusivesoulworks.polymorph.client.recipe.RecipesWidget;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;

public class PolymorphClientEvents {

  public static void tick() {
    Minecraft mc = Minecraft.getInstance();
    RecipesWidget.get().ifPresent(widget -> {
      if (mc.player == null || mc.screen == null) {
        RecipesWidget.clear();
      } else if (widget instanceof ITickingRecipesWidget) {
        ((ITickingRecipesWidget) widget).tick();
      }
    });
  }

  public static void initScreen(Screen screen) {

    if (screen instanceof AbstractContainerScreen) {
      RecipesWidget.create((AbstractContainerScreen<?>) screen);
    }
  }

  public static void render(Screen screen, GuiGraphics guiGraphics, int mouseX, int mouseY,
                            float partialTicks) {

    if (screen instanceof AbstractContainerScreen) {
      RecipesWidget.get().ifPresent(
          recipeController -> recipeController.render(guiGraphics, mouseX, mouseY, partialTicks));
    }
  }

  public static boolean mouseClick(Screen screen, double mouseX, double mouseY, int button) {

    if (screen instanceof AbstractContainerScreen) {
      return RecipesWidget.get()
          .map(recipeController -> recipeController.mouseClicked(mouseX, mouseY, button))
          .orElse(false);
    }
    return false;
  }
}
