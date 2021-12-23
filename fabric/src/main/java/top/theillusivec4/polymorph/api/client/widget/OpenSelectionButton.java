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

package top.theillusivec4.polymorph.api.client.widget;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.widget.TexturedButtonWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import top.theillusivec4.polymorph.mixin.core.AccessorHandledScreen;

public class OpenSelectionButton extends TexturedButtonWidget {

  private final HandledScreen<?> handledScreen;

  private int xOffset;
  private int yOffset;

  public OpenSelectionButton(HandledScreen<?> pHandledScreen, int pX, int pY, PressAction pOnPress) {
    super(0,0, 16, 16, 0, 0, 17, AbstractRecipesWidget.WIDGETS, 256, 256, pOnPress);
    this.handledScreen = pHandledScreen;
    this.xOffset = pX;
    this.yOffset = pY;
  }

  public void setOffsets(int pX, int pY) {
    this.xOffset = pX;
    this.yOffset = pY;
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
