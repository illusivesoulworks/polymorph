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
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.Drawable;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.VertexFormat;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.text.StringVisitable;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.Language;
import net.minecraft.util.math.Matrix4f;
import org.lwjgl.opengl.GL11;
import top.theillusivec4.polymorph.api.common.base.RecipePair;
import top.theillusivec4.polymorph.mixin.core.AccessorHandledScreen;
import top.theillusivec4.polymorph.mixin.core.AccessorScreen;

public class SelectionWidget extends DrawableHelper implements Drawable, Element {

  private final Consumer<Identifier> onSelect;
  private final HandledScreen<?> handledScreen;
  private final List<OutputWidget> outputWidgets = new ArrayList<>();

  private int xOffset;
  private int yOffset;
  private OutputWidget hoveredButton;
  private boolean active = false;
  private int x;
  private int y;
  private int lastX;
  private int lastY;

  public SelectionWidget(int x, int y, int xOffset, int yOffset, Consumer<Identifier> onSelect,
                         HandledScreen<?> screen) {
    this.setPosition(x, y);
    this.onSelect = onSelect;
    this.handledScreen = screen;
    this.xOffset = xOffset;
    this.yOffset = yOffset;
  }

  public void setPosition(int x, int y) {
    this.x = x;
    this.y = y;
    this.updateButtonPositions();
  }

  public void setOffsets(int pX, int pY) {
    this.xOffset = pX;
    this.yOffset = pY;
  }

  public void highlightButton(Identifier pIdentifier) {
    this.outputWidgets.forEach(
        widget -> widget.setHighlighted(widget.getIdentifier().equals(pIdentifier)));
  }

  private void updateButtonPositions() {
    int size = this.outputWidgets.size();
    int xOffset = (int) (-25 * Math.floor((size / 2.0F)));

    if (size % 2 == 0) {
      xOffset += 13;
    }
    int[] pos = {this.x + xOffset, this.y};
    this.outputWidgets.forEach(widget -> {
      widget.setPosition(pos[0], pos[1]);
      pos[0] += 25;
    });
  }

  public List<OutputWidget> getOutputWidgets() {
    return this.outputWidgets;
  }

  public void setRecipeList(Set<RecipePair> pRecipeList) {
    this.outputWidgets.clear();
    pRecipeList.forEach(data -> {
      if (!data.getOutput().isEmpty()) {
        this.outputWidgets.add(new OutputWidget(data));
      }
    });
    this.updateButtonPositions();
  }

  public void setActive(boolean active) {
    this.active = active;
  }

  public boolean isActive() {
    return this.active;
  }

  public void renderTooltip(MatrixStack matrixStack, int mouseX, int mouseY) {
    MinecraftClient mc = MinecraftClient.getInstance();

    if (mc.currentScreen != null && this.hoveredButton != null) {
      this.renderTooltip(matrixStack, this.hoveredButton.getTooltipText(mc.currentScreen), mouseX,
          mouseY);
    }
  }

  @Override
  public void render(MatrixStack matrixStack, int mouseX, int mouseY, float delta) {

    if (this.isActive()) {
      int x = ((AccessorHandledScreen) this.handledScreen).getX() + this.xOffset;
      int y = ((AccessorHandledScreen) this.handledScreen).getY() + this.yOffset;

      if (this.lastX != x || this.lastY != y) {
        this.setPosition(x, y);
        this.lastX = x;
        this.lastY = y;
      }
      this.hoveredButton = null;
      this.outputWidgets.forEach(button -> {
        button.render(matrixStack, mouseX, mouseY, delta);

        if (button.visible && button.isHovered()) {
          this.hoveredButton = button;
        }
      });
      this.renderTooltip(matrixStack, mouseX, mouseY);
    }
  }

  @Override
  public boolean mouseClicked(double mouseX, double mouseY, int button) {

    if (this.isActive()) {

      for (OutputWidget widget : this.outputWidgets) {

        if (widget.mouseClicked(mouseX, mouseY, button)) {
          onSelect.accept(widget.getIdentifier());
          return true;
        }
      }
    }
    return false;
  }

  public static final int DEFAULT_BACKGROUND_COLOR = 0xF0100010;
  public static final int DEFAULT_BORDER_COLOR_START = 0x505000FF;
  public static final int DEFAULT_BORDER_COLOR_END =
      (DEFAULT_BORDER_COLOR_START & 0xFEFEFE) >> 1 | DEFAULT_BORDER_COLOR_START & 0xFF000000;

  public void renderTooltip(MatrixStack matrices, List<Text> textLines, int mouseX, int mouseY) {
    drawHoveringText(matrices, textLines, mouseX, mouseY, this.handledScreen.width,
        this.handledScreen.height, -1,
        DEFAULT_BACKGROUND_COLOR, DEFAULT_BORDER_COLOR_START, DEFAULT_BORDER_COLOR_END,
        ((AccessorScreen) this.handledScreen).getTextRenderer());
  }

  public static void drawHoveringText(MatrixStack mStack, List<? extends StringVisitable> textLines,
                                      int mouseX, int mouseY, int screenWidth, int screenHeight,
                                      int maxTextWidth, int backgroundColor, int borderColorStart,
                                      int borderColorEnd, TextRenderer font) {

    if (!textLines.isEmpty()) {
      RenderSystem.disableDepthTest();
      int tooltipTextWidth = 0;

      for (StringVisitable textLine : textLines) {
        int textLineWidth = font.getWidth(textLine);

        if (textLineWidth > tooltipTextWidth) {
          tooltipTextWidth = textLineWidth;
        }
      }
      boolean needsWrap = false;
      int titleLinesCount = 1;
      int tooltipX = mouseX + 12;

      if (tooltipX + tooltipTextWidth + 4 > screenWidth) {
        tooltipX = mouseX - 16 - tooltipTextWidth;

        if (tooltipX < 4) {

          if (mouseX > screenWidth / 2) {
            tooltipTextWidth = mouseX - 12 - 8;
          } else {
            tooltipTextWidth = screenWidth - 16 - mouseX;
          }
          needsWrap = true;
        }
      }

      if (maxTextWidth > 0 && tooltipTextWidth > maxTextWidth) {
        tooltipTextWidth = maxTextWidth;
        needsWrap = true;
      }

      if (needsWrap) {
        int wrappedTooltipWidth = 0;
        List<StringVisitable> wrappedTextLines = new ArrayList<>();

        for (int i = 0; i < textLines.size(); i++) {
          StringVisitable textLine = textLines.get(i);
          List<StringVisitable> wrappedLine =
              font.getTextHandler().wrapLines(textLine, tooltipTextWidth, Style.EMPTY);

          if (i == 0) {
            titleLinesCount = wrappedLine.size();
          }

          for (StringVisitable line : wrappedLine) {
            int lineWidth = font.getWidth(line);
            if (lineWidth > wrappedTooltipWidth) {
              wrappedTooltipWidth = lineWidth;
            }
            wrappedTextLines.add(line);
          }
        }
        tooltipTextWidth = wrappedTooltipWidth;
        textLines = wrappedTextLines;

        if (mouseX > screenWidth / 2) {
          tooltipX = mouseX - 16 - tooltipTextWidth;
        } else {
          tooltipX = mouseX + 12;
        }
      }
      int tooltipY = mouseY - 12;
      int tooltipHeight = 8;

      if (textLines.size() > 1) {
        tooltipHeight += (textLines.size() - 1) * 10;

        if (textLines.size() > titleLinesCount) {
          tooltipHeight += 2;
        }
      }

      if (tooltipY < 4) {
        tooltipY = 4;
      } else if (tooltipY + tooltipHeight + 4 > screenHeight) {
        tooltipY = screenHeight - tooltipHeight - 4;
      }
      final int zLevel = 900;
      mStack.push();
      Matrix4f mat = mStack.peek().getModel();
      drawGradientRect(mat, zLevel, tooltipX - 3, tooltipY - 4,
          tooltipX + tooltipTextWidth + 3,
          tooltipY - 3, backgroundColor, backgroundColor);
      drawGradientRect(mat, zLevel, tooltipX - 3, tooltipY + tooltipHeight + 3,
          tooltipX + tooltipTextWidth + 3, tooltipY + tooltipHeight + 4, backgroundColor,
          backgroundColor);
      drawGradientRect(mat, zLevel, tooltipX - 3, tooltipY - 3,
          tooltipX + tooltipTextWidth + 3,
          tooltipY + tooltipHeight + 3, backgroundColor, backgroundColor);
      drawGradientRect(mat, zLevel, tooltipX - 4, tooltipY - 3, tooltipX - 3,
          tooltipY + tooltipHeight + 3, backgroundColor, backgroundColor);
      drawGradientRect(mat, zLevel, tooltipX + tooltipTextWidth + 3, tooltipY - 3,
          tooltipX + tooltipTextWidth + 4, tooltipY + tooltipHeight + 3, backgroundColor,
          backgroundColor);
      drawGradientRect(mat, zLevel, tooltipX - 3, tooltipY - 3 + 1, tooltipX - 3 + 1,
          tooltipY + tooltipHeight + 3 - 1, borderColorStart, borderColorEnd);
      drawGradientRect(mat, zLevel, tooltipX + tooltipTextWidth + 2, tooltipY - 3 + 1,
          tooltipX + tooltipTextWidth + 3, tooltipY + tooltipHeight + 3 - 1, borderColorStart,
          borderColorEnd);
      drawGradientRect(mat, zLevel, tooltipX - 3, tooltipY - 3,
          tooltipX + tooltipTextWidth + 3,
          tooltipY - 3 + 1, borderColorStart, borderColorStart);
      drawGradientRect(mat, zLevel, tooltipX - 3, tooltipY + tooltipHeight + 2,
          tooltipX + tooltipTextWidth + 3, tooltipY + tooltipHeight + 3, borderColorEnd,
          borderColorEnd);
      VertexConsumerProvider.Immediate renderType =
          VertexConsumerProvider.immediate(Tessellator.getInstance().getBuffer());
      mStack.translate(0.0D, 0.0D, zLevel);

      for (int lineNumber = 0; lineNumber < textLines.size(); ++lineNumber) {
        StringVisitable line = textLines.get(lineNumber);

        if (line != null) {
          font.draw(Language.getInstance().reorder(line), (float) tooltipX,
              (float) tooltipY, -1, true, mat, renderType, false, 0, 15728880);
        }

        if (lineNumber + 1 == titleLinesCount) {
          tooltipY += 2;
        }
        tooltipY += 10;
      }
      renderType.draw();
      mStack.pop();
      RenderSystem.enableDepthTest();
    }
  }

  public static void drawGradientRect(Matrix4f mat, int zLevel, int left, int top, int right,
                                      int bottom, int startColor, int endColor) {
    float startAlpha = (float) (startColor >> 24 & 255) / 255.0F;
    float startRed = (float) (startColor >> 16 & 255) / 255.0F;
    float startGreen = (float) (startColor >> 8 & 255) / 255.0F;
    float startBlue = (float) (startColor & 255) / 255.0F;
    float endAlpha = (float) (endColor >> 24 & 255) / 255.0F;
    float endRed = (float) (endColor >> 16 & 255) / 255.0F;
    float endGreen = (float) (endColor >> 8 & 255) / 255.0F;
    float endBlue = (float) (endColor & 255) / 255.0F;
    RenderSystem.enableDepthTest();
    RenderSystem.disableTexture();
    RenderSystem.enableBlend();
    RenderSystem.defaultBlendFunc();
    RenderSystem.setShader(GameRenderer::getPositionColorShader);
    Tessellator tessellator = Tessellator.getInstance();
    BufferBuilder buffer = tessellator.getBuffer();
    buffer.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR);
    buffer.vertex(mat, right, top, zLevel).color(startRed, startGreen, startBlue, startAlpha)
        .next();
    buffer.vertex(mat, left, top, zLevel).color(startRed, startGreen, startBlue, startAlpha)
        .next();
    buffer.vertex(mat, left, bottom, zLevel).color(endRed, endGreen, endBlue, endAlpha).next();
    buffer.vertex(mat, right, bottom, zLevel).color(endRed, endGreen, endBlue, endAlpha).next();
    tessellator.draw();
    RenderSystem.disableBlend();
    RenderSystem.enableTexture();
  }
}
