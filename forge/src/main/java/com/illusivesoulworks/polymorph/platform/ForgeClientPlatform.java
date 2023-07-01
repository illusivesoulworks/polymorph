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
import com.illusivesoulworks.polymorph.platform.services.IClientPlatform;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.BufferUploader;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;
import java.util.List;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipPositioner;
import net.minecraft.client.gui.screens.inventory.tooltip.TooltipRenderUtil;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.client.ForgeHooksClient;
import net.minecraftforge.client.event.RenderTooltipEvent;
import org.joml.Matrix4f;
import org.joml.Vector2ic;

public class ForgeClientPlatform implements IClientPlatform {

  @Override
  public int getScreenTop(AbstractContainerScreen<?> screen) {
    return screen.getGuiTop();
  }

  @Override
  public int getScreenLeft(AbstractContainerScreen<?> screen) {
    return screen.getGuiLeft();
  }

  @Override
  public void renderTooltip(ItemStack stack, PoseStack poseStack, List<ClientTooltipComponent> text,
                            ClientTooltipPositioner positioner, int mouseX, int mouseY,
                            AbstractContainerScreen<?> containerScreen, Font font,
                            SelectionWidget.GradientDrawer drawer) {

    if (!text.isEmpty()) {
      RenderTooltipEvent.Pre preEvent =
          ForgeHooksClient.onRenderTooltipPre(stack, poseStack, mouseX, mouseY,
              containerScreen.width, containerScreen.height, text, font, font);

      if (preEvent.isCanceled()) {
        return;
      }
      int i = 0;
      int j = text.size() == 1 ? -2 : 0;

      for (ClientTooltipComponent clienttooltipcomponent : text) {
        int k = clienttooltipcomponent.getWidth(preEvent.getFont());
        if (k > i) {
          i = k;
        }

        j += clienttooltipcomponent.getHeight();
      }

      Vector2ic
          vector2ic =
          positioner.positionTooltip(containerScreen, preEvent.getX(), preEvent.getY(), i, j);
      int l = vector2ic.x();
      int i1 = vector2ic.y();
      poseStack.pushPose();
      int blitOffset = 901;
      Tesselator tesselator = Tesselator.getInstance();
      BufferBuilder bufferbuilder = tesselator.getBuilder();
      RenderSystem.setShader(GameRenderer::getPositionColorShader);
      bufferbuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);
      Matrix4f matrix4f = poseStack.last().pose();
      RenderTooltipEvent.Color colorEvent =
          ForgeHooksClient.onRenderTooltipColor(stack, poseStack, l, i1, preEvent.getFont(), text);
      TooltipRenderUtil.renderTooltipBackground(drawer::fillGradient, matrix4f, bufferbuilder, l,
          i1, i, j, blitOffset, colorEvent.getBackgroundStart(), colorEvent.getBackgroundEnd(),
          colorEvent.getBorderStart(), colorEvent.getBorderEnd());
      RenderSystem.enableDepthTest();
      RenderSystem.enableBlend();
      RenderSystem.defaultBlendFunc();
      BufferUploader.drawWithShader(bufferbuilder.end());
      MultiBufferSource.BufferSource multibuffersource$buffersource =
          MultiBufferSource.immediate(Tesselator.getInstance().getBuilder());
      poseStack.translate(0.0F, 0.0F, blitOffset);
      int k1 = i1;

      for (int l1 = 0; l1 < text.size(); ++l1) {
        ClientTooltipComponent clienttooltipcomponent1 = text.get(l1);
        clienttooltipcomponent1.renderText(preEvent.getFont(), l, k1, matrix4f,
            multibuffersource$buffersource);
        k1 += clienttooltipcomponent1.getHeight() + (l1 == 0 ? 2 : 0);
      }

      multibuffersource$buffersource.endBatch();
      k1 = i1;
      ItemRenderer itemRenderer = containerScreen.getMinecraft().getItemRenderer();

      for (int i2 = 0; i2 < text.size(); ++i2) {
        ClientTooltipComponent clienttooltipcomponent2 = text.get(i2);
        clienttooltipcomponent2.renderImage(preEvent.getFont(), l, k1, poseStack, itemRenderer);
        k1 += clienttooltipcomponent2.getHeight() + (i2 == 0 ? 2 : 0);
      }
      poseStack.popPose();
    }
  }
}
