/*
 * Copyright (C) 2020-2021 C4
 *
 * This file is part of Polymorph.
 *
 * Polymorph is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Polymorph is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * and the GNU Lesser General Public License along with Polymorph.
 * If not, see <https://www.gnu.org/licenses/>.
 *
 */

package top.theillusivec4.polymorph.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraftforge.client.event.ScreenEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import top.theillusivec4.polymorph.api.client.base.ITickingRecipesWidget;
import top.theillusivec4.polymorph.client.recipe.RecipesWidget;

@SuppressWarnings("unused")
public class ClientEventsListener {

  @SubscribeEvent
  public void tick(TickEvent.ClientTickEvent pEvent) {

    if (pEvent.phase == TickEvent.Phase.END) {
      Minecraft mc = Minecraft.getInstance();
      RecipesWidget.get().ifPresent(widget -> {
        if (mc.player == null || mc.screen == null) {
          RecipesWidget.clear();
        } else if (widget instanceof ITickingRecipesWidget) {
          ((ITickingRecipesWidget) widget).tick();
        }
      });
    }
  }

  @SubscribeEvent
  public void initGui(ScreenEvent.InitScreenEvent.Post pEvent) {
    Screen screen = pEvent.getScreen();

    if (screen instanceof AbstractContainerScreen) {
      RecipesWidget.create((AbstractContainerScreen<?>) screen);
    }
  }

  @SubscribeEvent
  public void render(ScreenEvent.DrawScreenEvent.Post pEvent) {

    if (pEvent.getScreen() instanceof AbstractContainerScreen) {
      RecipesWidget.get().ifPresent(
          recipeController -> recipeController.render(pEvent.getPoseStack(), pEvent.getMouseX(),
              pEvent.getMouseY(), pEvent.getPartialTicks()));
    }
  }

  @SubscribeEvent
  public void mouseClick(ScreenEvent.MouseClickedEvent.Pre pEvent) {

    if (pEvent.getScreen() instanceof AbstractContainerScreen) {
      RecipesWidget.get().ifPresent(recipeController -> {
        if (recipeController.mouseClicked(pEvent.getMouseX(), pEvent.getMouseY(),
            pEvent.getButton())) {
          pEvent.setCanceled(true);
        }
      });
    }
  }
}
