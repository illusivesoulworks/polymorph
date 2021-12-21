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

package top.theillusivec4.polymorph.mixin.core;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.Mouse;
import net.minecraft.client.gui.screen.Screen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import top.theillusivec4.polymorph.client.ClientEventsListener;

@Mixin(Mouse.class)
public class MixinMouse {

  @Shadow
  MinecraftClient client;
  @Shadow
  double x;
  @Shadow
  double y;

  @Inject(at = @At("HEAD"), method = "method_1611([ZDDI)V", cancellable = true)
  public void mouseClick(boolean[] unused, double mouseX, double mouseY, int button,
                         CallbackInfo cb) {
    Screen screen = this.client.currentScreen;
    double d = this.x * (double) this.client.getWindow().getScaledWidth() / (double) this.client
        .getWindow().getWidth();
    double e = this.y * (double) this.client.getWindow().getScaledHeight() / (double) this.client
        .getWindow().getHeight();

    if (ClientEventsListener.clickScreen(screen, d, e, button)) {
      cb.cancel();
    }
  }
}
