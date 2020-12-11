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

package top.theillusivec4.polymorph.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.event.TickEvent.ClientTickEvent;
import net.minecraftforge.event.TickEvent.Phase;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import top.theillusivec4.polymorph.client.selector.RecipeSelectorManager;

public class ClientEventHandler {

  @SubscribeEvent
  public void tick(ClientTickEvent evt) {

    if (evt.phase == Phase.END) {
      Minecraft mc = Minecraft.getInstance();

      if (mc.world != null && mc.player != null) {
        RecipeSelectorManager.getSelector().ifPresent(selector -> {
          if (mc.player.openContainer == null) {
            RecipeSelectorManager.clear();
          } else {
            selector.tick();
          }
        });
      }
    }
  }

  @SubscribeEvent
  public void initGui(GuiScreenEvent.InitGuiEvent.Post evt) {
    Screen screen = evt.getGui();

    if (!(screen instanceof ContainerScreen) ||
        !RecipeSelectorManager.tryCreate((ContainerScreen<?>) screen)) {
      RecipeSelectorManager.clear();
    }
  }

  @SubscribeEvent
  public void render(GuiScreenEvent.DrawScreenEvent.Post evt) {

    if (evt.getGui() instanceof ContainerScreen) {
      RecipeSelectorManager.getSelector().ifPresent(selector -> selector
          .render(evt.getMatrixStack(), evt.getMouseX(), evt.getMouseY(),
              evt.getRenderPartialTicks()));
    }
  }

  @SubscribeEvent
  public void mouseClick(GuiScreenEvent.MouseClickedEvent.Pre evt) {

    if (evt.getGui() instanceof ContainerScreen) {
      RecipeSelectorManager.getSelector().ifPresent(selector -> {
        selector.markUpdatePosition();

        if (selector.mouseClicked(evt.getMouseX(), evt.getMouseY(), evt.getButton())) {
          evt.setCanceled(true);
        }
      });
    }
  }
}
