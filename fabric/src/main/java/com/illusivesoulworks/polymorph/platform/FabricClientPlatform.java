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
import java.util.Iterator;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipPositioner;
import net.minecraft.client.gui.screens.inventory.tooltip.TooltipRenderUtil;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.world.item.ItemStack;
import org.joml.Matrix4f;
import org.joml.Vector2ic;

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
  public void renderTooltip(ItemStack stack, PoseStack poseStack, List<ClientTooltipComponent> text,
                            ClientTooltipPositioner positioner, int mouseX, int mouseY,
                            AbstractContainerScreen<?> containerScreen, Font font,
                            SelectionWidget.GradientDrawer drawer) {

    if (!text.isEmpty()) {
      int k = 0;
      int l = text.size() == 1 ? -2 : 0;

      ClientTooltipComponent clientTooltipComponent;
      for (Iterator var8 = text.iterator(); var8.hasNext();
           l += clientTooltipComponent.getHeight()) {
        clientTooltipComponent = (ClientTooltipComponent) var8.next();
        int m = clientTooltipComponent.getWidth(font);
        if (m > k) {
          k = m;
        }
      }

      Vector2ic vector2ic = positioner.positionTooltip(containerScreen, mouseX, mouseY, k, l);
      int p = vector2ic.x();
      int q = vector2ic.y();
      poseStack.pushPose();
      Tesselator tesselator = Tesselator.getInstance();
      BufferBuilder bufferBuilder = tesselator.getBuilder();
      RenderSystem.setShader(GameRenderer::getPositionColorShader);
      bufferBuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);
      Matrix4f matrix4f = poseStack.last().pose();
      int blitOffset = 901;
      TooltipRenderUtil.renderTooltipBackground(drawer::fillGradient, matrix4f, bufferBuilder, p, q,
          k, l, blitOffset);
      RenderSystem.enableDepthTest();
      RenderSystem.enableBlend();
      RenderSystem.defaultBlendFunc();
      BufferUploader.drawWithShader(bufferBuilder.end());
      MultiBufferSource.BufferSource bufferSource =
          MultiBufferSource.immediate(Tesselator.getInstance().getBuilder());
      poseStack.translate(0.0F, 0.0F, 400.0F);
      int s = q;

      int t;
      ClientTooltipComponent clientTooltipComponent2;
      for (t = 0; t < text.size(); ++t) {
        clientTooltipComponent2 = text.get(t);
        clientTooltipComponent2.renderText(font, p, s, matrix4f, bufferSource);
        s += clientTooltipComponent2.getHeight() + (t == 0 ? 2 : 0);
      }

      bufferSource.endBatch();
      s = q;
      ItemRenderer itemRenderer = Minecraft.getInstance().getItemRenderer();

      for (t = 0; t < text.size(); ++t) {
        clientTooltipComponent2 = text.get(t);
        clientTooltipComponent2.renderImage(font, p, s, poseStack, itemRenderer);
        s += clientTooltipComponent2.getHeight() + (t == 0 ? 2 : 0);
      }

      poseStack.popPose();
    }
  }
}
