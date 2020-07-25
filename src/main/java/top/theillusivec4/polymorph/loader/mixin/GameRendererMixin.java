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

package top.theillusivec4.polymorph.loader.mixin;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.math.MatrixStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import top.theillusivec4.polymorph.loader.client.ClientMixinHooks;

@Mixin(GameRenderer.class)
public class GameRendererMixin {

  @Shadow
  MinecraftClient client;

  @Inject(at = @At("TAIL"), method = "render")
  public void render(CallbackInfo cb) {
    int i = (int) (this.client.mouse.getX() * (double) this.client.getWindow().getScaledWidth()
        / (double) this.client.getWindow().getWidth());
    int j = (int) (this.client.mouse.getY() * (double) this.client.getWindow().getScaledHeight()
        / (double) this.client.getWindow().getHeight());
    ClientMixinHooks.renderConflictManager(this.client.currentScreen, new MatrixStack(), i, j,
        this.client.getLastFrameDuration());
  }
}
