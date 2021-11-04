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

package top.theillusivec4.polymorph.api.client.widget;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.BufferUploader;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.math.Matrix4f;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;
import javax.annotation.Nonnull;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.gui.components.Widget;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.client.ForgeHooksClient;
import net.minecraftforge.client.event.RenderTooltipEvent;
import top.theillusivec4.polymorph.api.common.base.IRecipePair;

public class SelectionWidget extends GuiComponent
    implements Widget, GuiEventListener {

  private final Consumer<ResourceLocation> onSelect;
  private final AbstractContainerScreen<?> containerScreen;
  private final List<OutputWidget> outputWidgets = new ArrayList<>();
  private int xOffset;
  private int yOffset;

  private OutputWidget hoveredButton;
  private boolean active = false;
  private int x;
  private int y;
  private int lastX;
  private int lastY;

  public SelectionWidget(int pX, int pY, int pXOffset, int pYOffset,
                         Consumer<ResourceLocation> pOnSelect,
                         AbstractContainerScreen<?> pContainerScreen) {
    this.setPosition(pX, pY);
    this.onSelect = pOnSelect;
    this.containerScreen = pContainerScreen;
    this.xOffset = pXOffset;
    this.yOffset = pYOffset;
  }

  public void setPosition(int pX, int pY) {
    this.x = pX;
    this.y = pY;
    this.updateButtonPositions();
  }

  public void setOffsets(int pX, int pY) {
    this.xOffset = pX;
    this.yOffset = pY;
  }

  public void highlightButton(ResourceLocation pResourceLocation) {
    this.outputWidgets.forEach(
        widget -> widget.setHighlighted(widget.getResourceLocation().equals(pResourceLocation)));
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
    return outputWidgets;
  }

  public void setRecipeList(Set<IRecipePair> pRecipeList) {
    this.outputWidgets.clear();
    pRecipeList.forEach(data -> {
      if (!data.getOutput().isEmpty()) {
        this.outputWidgets.add(new OutputWidget(data));
      }
    });
    this.updateButtonPositions();
  }

  public void setActive(boolean pActive) {
    this.active = pActive;
  }

  public boolean isActive() {
    return this.active;
  }

  public void renderTooltip(PoseStack pMatrixStack, int pMouseX, int pMouseY) {
    Minecraft mc = Minecraft.getInstance();

    if (mc.screen != null && this.hoveredButton != null) {
      this.renderTooltip(this.hoveredButton.getOutput(), pMatrixStack,
          this.hoveredButton.getTooltipText(mc.screen), pMouseX, pMouseY);
    }
  }

  @Override
  public void render(@Nonnull PoseStack pMatrixStack, int pMouseX, int pMouseY,
                     float pPartialTicks) {

    if (this.isActive()) {
      int x = containerScreen.getGuiLeft() + this.xOffset;
      int y = containerScreen.getGuiTop() + this.yOffset;

      if (this.lastX != x || this.lastY != y) {
        this.setPosition(x, y);
        this.lastX = x;
        this.lastY = y;
      }
      this.hoveredButton = null;
      this.outputWidgets.forEach(button -> {
        button.render(pMatrixStack, pMouseX, pMouseY, pPartialTicks);

        if (button.visible && button.isHovered()) {
          this.hoveredButton = button;
        }
      });
      this.renderTooltip(pMatrixStack, pMouseX, pMouseY);
    }
  }

  @Override
  public boolean mouseClicked(double pMouseX, double pMouseY, int pButton) {

    if (this.isActive()) {

      for (OutputWidget widget : this.outputWidgets) {

        if (widget.mouseClicked(pMouseX, pMouseY, pButton)) {
          onSelect.accept(widget.getResourceLocation());
          return true;
        }
      }
    }
    return false;
  }

  public void renderTooltip(ItemStack pStack, PoseStack pPoseStack,
                            List<Component> pText, int pMouseX, int pMouseY) {
    renderTooltipInternal(pStack, pPoseStack, pText, pMouseX, pMouseY, this.containerScreen,
        this.containerScreen.getMinecraft().font);
  }

  private static void renderTooltipInternal(@Nonnull final ItemStack pStack, PoseStack pPoseStack,
                                            List<Component> pText, int pMouseX, int pMouseY,
                                            Screen pScreen, Font pFontRenderer) {

    if (!pText.isEmpty()) {
      List<ClientTooltipComponent> components =
          net.minecraftforge.client.ForgeHooksClient.gatherTooltipComponents(pStack, pText, pMouseX,
              pScreen.width, pScreen.height, pFontRenderer, pFontRenderer);
      RenderTooltipEvent.Pre preEvent = ForgeHooksClient.preTooltipEvent(pStack, pPoseStack,
          pMouseX, pMouseY, pScreen.width, pScreen.height, components, pFontRenderer,
          pFontRenderer);

      if (preEvent.isCanceled()) {
        return;
      }
      int i = 0;
      int j = pText.size() == 1 ? -2 : 0;

      for (ClientTooltipComponent clienttooltipcomponent : components) {
        int k = clienttooltipcomponent.getWidth(preEvent.getFontRenderer());
        if (k > i) {
          i = k;
        }

        j += clienttooltipcomponent.getHeight();
      }

      int j2 = preEvent.getX() + 12;
      int k2 = preEvent.getY() - 12;
      if (j2 + i > pScreen.width) {
        j2 -= 28 + i;
      }

      if (k2 + j + 6 > pScreen.height) {
        k2 = pScreen.height - j - 6;
      }

      pPoseStack.pushPose();
      ItemRenderer itemRenderer = pScreen.getMinecraft().getItemRenderer();
      float f = itemRenderer.blitOffset;
      int blitOffset = 900;
      itemRenderer.blitOffset = blitOffset;
      Tesselator tesselator = Tesselator.getInstance();
      BufferBuilder bufferbuilder = tesselator.getBuilder();
      RenderSystem.setShader(GameRenderer::getPositionColorShader);
      bufferbuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);
      Matrix4f matrix4f = pPoseStack.last().pose();
      RenderTooltipEvent.Color colorEvent =
          ForgeHooksClient.colorTooltipEvent(pStack, pPoseStack, j2, k2, preEvent.getFontRenderer(),
              components);
      fillGradient(matrix4f, bufferbuilder, j2 - 3, k2 - 4, j2 + i + 3, k2 - 3, blitOffset,
          colorEvent.getBackgroundStart(), colorEvent.getBackgroundStart());
      fillGradient(matrix4f, bufferbuilder, j2 - 3, k2 + j + 3, j2 + i + 3, k2 + j + 4, blitOffset,
          colorEvent.getBackgroundEnd(), colorEvent.getBackgroundEnd());
      fillGradient(matrix4f, bufferbuilder, j2 - 3, k2 - 3, j2 + i + 3, k2 + j + 3, blitOffset,
          colorEvent.getBackgroundStart(), colorEvent.getBackgroundEnd());
      fillGradient(matrix4f, bufferbuilder, j2 - 4, k2 - 3, j2 - 3, k2 + j + 3, blitOffset,
          colorEvent.getBackgroundStart(), colorEvent.getBackgroundEnd());
      fillGradient(matrix4f, bufferbuilder, j2 + i + 3, k2 - 3, j2 + i + 4, k2 + j + 3, blitOffset,
          colorEvent.getBackgroundStart(), colorEvent.getBackgroundEnd());
      fillGradient(matrix4f, bufferbuilder, j2 - 3, k2 - 3 + 1, j2 - 3 + 1, k2 + j + 3 - 1,
          blitOffset,
          colorEvent.getBorderStart(), colorEvent.getBorderEnd());
      fillGradient(matrix4f, bufferbuilder, j2 + i + 2, k2 - 3 + 1, j2 + i + 3, k2 + j + 3 - 1,
          blitOffset,
          colorEvent.getBorderStart(), colorEvent.getBorderEnd());
      fillGradient(matrix4f, bufferbuilder, j2 - 3, k2 - 3, j2 + i + 3, k2 - 3 + 1, blitOffset,
          colorEvent.getBorderStart(), colorEvent.getBorderStart());
      fillGradient(matrix4f, bufferbuilder, j2 - 3, k2 + j + 2, j2 + i + 3, k2 + j + 3, blitOffset,
          colorEvent.getBorderEnd(), colorEvent.getBorderEnd());
      RenderSystem.enableDepthTest();
      RenderSystem.disableTexture();
      RenderSystem.enableBlend();
      RenderSystem.defaultBlendFunc();
      bufferbuilder.end();
      BufferUploader.end(bufferbuilder);
      RenderSystem.disableBlend();
      RenderSystem.enableTexture();
      MultiBufferSource.BufferSource multibuffersource$buffersource =
          MultiBufferSource.immediate(Tesselator.getInstance().getBuilder());
      pPoseStack.translate(0.0D, 0.0D, blitOffset);
      int l1 = k2;

      for (int i2 = 0; i2 < components.size(); ++i2) {
        ClientTooltipComponent clienttooltipcomponent1 = components.get(i2);
        clienttooltipcomponent1.renderText(preEvent.getFontRenderer(), j2, l1, matrix4f,
            multibuffersource$buffersource);
        l1 += clienttooltipcomponent1.getHeight() + (i2 == 0 ? 2 : 0);
      }

      multibuffersource$buffersource.endBatch();
      pPoseStack.popPose();
      l1 = k2;

      for (int l2 = 0; l2 < components.size(); ++l2) {
        ClientTooltipComponent clienttooltipcomponent2 = components.get(l2);
        clienttooltipcomponent2.renderImage(preEvent.getFontRenderer(), j2, l1, pPoseStack,
            itemRenderer, blitOffset, pScreen.getMinecraft().getTextureManager());
        l1 += clienttooltipcomponent2.getHeight() + (l2 == 0 ? 2 : 0);
      }
      itemRenderer.blitOffset = f;
    }
  }
}
