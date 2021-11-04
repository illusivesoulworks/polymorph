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

package top.theillusivec4.polymorph.client.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.widget.TexturedButtonWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import top.theillusivec4.polymorph.mixin.core.AccessorHandledScreen;

public class ToggleSelectorButton extends TexturedButtonWidget {

  private final HandledScreen<?> handledScreen;
  private final int xOffset;
  private final int yOffset;

  public ToggleSelectorButton(HandledScreen<?> handledScreen, int xIn, int yIn, int xOffset,
                              int yOffset, int widthIn, int heightIn, int xTexStartIn,
                              int yTexStartIn, int yDiffTextIn, Identifier identifier,
                              PressAction onPressIn) {
    super(xIn, yIn, widthIn, heightIn, xTexStartIn, yTexStartIn, yDiffTextIn, identifier, 256, 256,
        onPressIn);
    this.handledScreen = handledScreen;
    this.xOffset = xOffset;
    this.yOffset = yOffset;
  }

  @Override
  public void renderButton(MatrixStack matrixStack, int p_renderButton_1_, int p_renderButton_2_,
                           float p_renderButton_3_) {
    RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
    this.x = ((AccessorHandledScreen) this.handledScreen).getX() + this.xOffset;
    this.y = ((AccessorHandledScreen) this.handledScreen).getY() + this.yOffset;
    super.renderButton(matrixStack, p_renderButton_1_, p_renderButton_2_, p_renderButton_3_);
  }
}
