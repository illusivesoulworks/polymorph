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

package top.theillusivec4.polymorph.loader.mixin.core;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.Mouse;
import net.minecraft.client.gui.screen.Screen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import top.theillusivec4.polymorph.loader.client.ClientMixinHooks;

@Mixin(Mouse.class)
public class MouseMixin {

  @Inject(at = @At("HEAD"), method = "onMouseButton", cancellable = true)
  public void mouseClick(long window, int button, int action, int mods, CallbackInfo cb) {
    MinecraftClient client = ((MouseAccessor)this).getClient();
    Screen screen = ((MouseAccessor)this).getClient().currentScreen;
    double d = ((MouseAccessor)this).getX() * (double) client.getWindow().getScaledWidth() / (double) client
        .getWindow().getWidth();
    double e = ((MouseAccessor)this).getY() * (double) client.getWindow().getScaledHeight() / (double) client
        .getWindow().getHeight();

    if (ClientMixinHooks.clickSelector(screen, d, e, button)) {
      cb.cancel();
    }
  }
}
