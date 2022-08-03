/*
 * Copyright (C) 2020-2022 Illusive Soulworks
 *
 * Polymorph is free software: you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published
 * by the Free Software Foundation, either version 3 of the License, or
 * any later version.
 *
 * Polymorph is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with Polymorph.  If not, see <https://www.gnu.org/licenses/>.
 */

package com.illusivesoulworks.polymorph.platform;

import com.illusivesoulworks.polymorph.api.client.widget.SelectionWidget;
import com.illusivesoulworks.polymorph.mixin.AccessorAbstractContainerScreen;
import com.illusivesoulworks.polymorph.platform.services.IClientPlatform;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.BufferUploader;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.math.Matrix4f;
import java.util.List;
import java.util.stream.Collectors;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.item.ItemStack;

public class FabricClientPlatform implements IClientPlatform {

  @Override
  public int getScreenTop(AbstractContainerScreen<?> screen) {
    return ((AccessorAbstractContainerScreen) screen).getTopPos();
  }

  @Override
  public int getScreenLeft(AbstractContainerScreen<?> screen) {
    return ((AccessorAbstractContainerScreen) screen).getLeftPos();
  }

  @Override
  public void renderTooltip(ItemStack stack, PoseStack poseStack, List<Component> text, int mouseX,
                            int mouseY, AbstractContainerScreen<?> containerScreen, Font font,
                            SelectionWidget.GradientDrawer drawer) {

    if (!text.isEmpty()) {
      List<ClientTooltipComponent> components = text.stream()
          .map(Component::getVisualOrderText)
          .map(ClientTooltipComponent::create)
          .collect(Collectors.toList());
      int i = 0;
      int j = components.size() == 1 ? -2 : 0;

      for (ClientTooltipComponent component : components) {
        int k = component.getWidth(font);

        if (k > i) {
          i = k;
        }
        j += component.getHeight();
      }

      int j2 = mouseX + 12;
      int k2 = mouseY - 12;
      if (j2 + i > containerScreen.width) {
        j2 -= 28 + i;
      }

      if (k2 + j + 6 > containerScreen.height) {
        k2 = containerScreen.height - j - 6;
      }

      poseStack.pushPose();
      ItemRenderer itemRenderer = Minecraft.getInstance().getItemRenderer();
      float f = itemRenderer.blitOffset;
      int blitOffset = 901;
      itemRenderer.blitOffset = blitOffset;
      Tesselator tesselator = Tesselator.getInstance();
      BufferBuilder bufferbuilder = tesselator.getBuilder();
      RenderSystem.setShader(GameRenderer::getPositionColorShader);
      bufferbuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);
      Matrix4f matrix = poseStack.last().pose();
      drawer.fillGradient(matrix, bufferbuilder, j2 - 3, k2 - 4, j2 + i + 3, k2 - 3, blitOffset,
          -267386864, -267386864);
      drawer.fillGradient(matrix, bufferbuilder, j2 - 3, k2 + j + 3, j2 + i + 3, k2 + j + 4, blitOffset,
          -267386864, -267386864);
      drawer.fillGradient(matrix, bufferbuilder, j2 - 3, k2 - 3, j2 + i + 3, k2 + j + 3, blitOffset, -267386864,
          -267386864);
      drawer.fillGradient(matrix, bufferbuilder, j2 - 4, k2 - 3, j2 - 3, k2 + j + 3, blitOffset, -267386864,
          -267386864);
      drawer.fillGradient(matrix, bufferbuilder, j2 + i + 3, k2 - 3, j2 + i + 4, k2 + j + 3, blitOffset,
          -267386864, -267386864);
      drawer.fillGradient(matrix, bufferbuilder, j2 - 3, k2 - 3 + 1, j2 - 3 + 1, k2 + j + 3 - 1, blitOffset,
          1347420415, 1344798847);
      drawer.fillGradient(matrix, bufferbuilder, j2 + i + 2, k2 - 3 + 1, j2 + i + 3, k2 + j + 3 - 1, blitOffset,
          1347420415, 1344798847);
      drawer.fillGradient(matrix, bufferbuilder, j2 - 3, k2 - 3, j2 + i + 3, k2 - 3 + 1, blitOffset, 1347420415,
          1347420415);
      drawer.fillGradient(matrix, bufferbuilder, j2 - 3, k2 + j + 2, j2 + i + 3, k2 + j + 3, blitOffset,
          1344798847, 1344798847);
      RenderSystem.enableDepthTest();
      RenderSystem.disableTexture();
      RenderSystem.enableBlend();
      RenderSystem.defaultBlendFunc();
      BufferUploader.drawWithShader(bufferbuilder.end());
      RenderSystem.disableBlend();
      RenderSystem.enableTexture();
      MultiBufferSource.BufferSource bufferSource =
          MultiBufferSource.immediate(Tesselator.getInstance().getBuilder());
      poseStack.translate(0.0D, 0.0D, blitOffset);
      int l1 = k2;

      for (int l2 = 0; l2 < components.size(); ++l2) {
        ClientTooltipComponent component = components.get(l2);
        component.renderText(font, j2, l1, matrix, bufferSource);
        l1 += component.getHeight() + (l2 == 0 ? 2 : 0);
      }
      bufferSource.endBatch();
      poseStack.popPose();
      l1 = k2;

      for (int i2 = 0; i2 < components.size(); ++i2) {
        ClientTooltipComponent component = components.get(i2);
        component.renderImage(font, j2, l1, poseStack, itemRenderer, blitOffset);
        l1 += component.getHeight() + (i2 == 0 ? 2 : 0);
      }
      itemRenderer.blitOffset = f;
    }
  }
}
