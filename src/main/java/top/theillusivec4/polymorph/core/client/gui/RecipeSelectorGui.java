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

package top.theillusivec4.polymorph.core.client.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.Drawable;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Recipe;
import net.minecraft.text.StringVisitable;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Language;
import net.minecraft.util.math.Matrix4f;
import org.lwjgl.opengl.GL11;
import top.theillusivec4.polymorph.loader.mixin.core.ScreenAccessor;

public class RecipeSelectorGui<I extends Inventory, R extends Recipe<I>> extends DrawableHelper
    implements Drawable, Element {

  private final Consumer<R> select;
  private final I inventory;
  private final Screen screen;
  private final List<RecipeOutputWidget<I, R>> buttons = new ArrayList<>();

  private RecipeOutputWidget<I, R> hoveredButton;
  private boolean visible = false;
  private int x;
  private int y;
  private List<R> recipes = new ArrayList<>();

  public RecipeSelectorGui(int x, int y, I inventory, Consumer<R> select, Screen screen) {
    this.setPosition(x, y);
    this.select = select;
    this.inventory = inventory;
    this.screen = screen;
  }

  public void setPosition(int x, int y) {
    this.x = x;
    this.y = y;
    this.updateButtonPositions();
  }

  public void highlightButton(String recipe) {
    this.buttons.forEach(button -> {
      String id = button.recipe.getId().toString();
      button.highlighted = id.equals(recipe);
    });
  }

  private void updateButtonPositions() {
    int size = recipes.size();
    int xOffset = (int) (-25 * Math.floor((size / 2.0F)));

    if (size % 2 == 0) {
      xOffset += 13;
    }
    int[] pos = {this.x + xOffset, this.y};
    this.buttons.forEach(button -> {
      button.setPosition(pos[0], pos[1]);
      pos[0] += 25;
    });
  }

  public List<RecipeOutputWidget<I, R>> getButtons() {
    return buttons;
  }

  public void setRecipes(List<R> recipes) {
    this.recipes = recipes;
    this.buttons.clear();
    recipes
        .forEach(recipe -> this.buttons.add(new RecipeOutputWidget<>(inventory, recipe)));
    this.updateButtonPositions();
  }

  public void setVisible(boolean visible) {
    this.visible = visible;
  }

  public boolean isVisible() {
    return this.visible;
  }

  public void renderTooltip(MatrixStack matrixStack, int p_193721_1_, int p_193721_2_) {
    MinecraftClient mc = MinecraftClient.getInstance();

    if (mc.currentScreen != null && this.hoveredButton != null) {
      this.renderTooltip(this.hoveredButton.getOutput(), matrixStack,
          this.hoveredButton.getTooltipText(mc.currentScreen),
          p_193721_1_, p_193721_2_);
    }
  }

  @Override
  public void render(MatrixStack matrixStack, int p_render_1_, int p_render_2_,
                     float p_render_3_) {

    if (this.isVisible()) {
      this.hoveredButton = null;
      buttons.forEach(button -> {
        button.render(matrixStack, p_render_1_, p_render_2_, p_render_3_);

        if (button.visible && button.isHovered()) {
          this.hoveredButton = button;
        }
      });
      this.renderTooltip(matrixStack, p_render_1_, p_render_2_);
    }
  }

  @Override
  public boolean mouseClicked(double p_mouseClicked_1_, double p_mouseClicked_3_,
                              int p_mouseClicked_5_) {

    if (this.isVisible()) {

      for (RecipeOutputWidget<I, R> button : this.buttons) {

        if (button.mouseClicked(p_mouseClicked_1_, p_mouseClicked_3_, p_mouseClicked_5_)) {
          select.accept(button.recipe);
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

  public void renderTooltip(ItemStack stack, MatrixStack p_243308_1_, List<Text> p_243308_2_,
                            int p_243308_3_, int p_243308_4_) {
    drawHoveringText(stack, p_243308_1_, p_243308_2_, p_243308_3_, p_243308_4_, this.screen.width,
        this.screen.height, -1, DEFAULT_BACKGROUND_COLOR,
        DEFAULT_BORDER_COLOR_START, DEFAULT_BORDER_COLOR_END,
        ((ScreenAccessor) this.screen).getTextRenderer());
  }

  @SuppressWarnings("deprecation")
  public static void drawHoveringText(final ItemStack stack, MatrixStack mStack,
                                      List<? extends StringVisitable> textLines, int mouseX,
                                      int mouseY, int screenWidth, int screenHeight,
                                      int maxTextWidth, int backgroundColor, int borderColorStart,
                                      int borderColorEnd, TextRenderer font) {
    if (!textLines.isEmpty()) {
      RenderSystem.disableRescaleNormal();
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
      RenderSystem.enableRescaleNormal();
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
    RenderSystem.shadeModel(GL11.GL_SMOOTH);
    Tessellator tessellator = Tessellator.getInstance();
    BufferBuilder buffer = tessellator.getBuffer();
    buffer.begin(GL11.GL_QUADS, VertexFormats.POSITION_COLOR);
    buffer.vertex(mat, right, top, zLevel).color(startRed, startGreen, startBlue, startAlpha)
        .next();
    buffer.vertex(mat, left, top, zLevel).color(startRed, startGreen, startBlue, startAlpha)
        .next();
    buffer.vertex(mat, left, bottom, zLevel).color(endRed, endGreen, endBlue, endAlpha).next();
    buffer.vertex(mat, right, bottom, zLevel).color(endRed, endGreen, endBlue, endAlpha).next();
    tessellator.draw();
    RenderSystem.shadeModel(GL11.GL_FLAT);
    RenderSystem.disableBlend();
    RenderSystem.enableTexture();
  }
}
