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
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.client.ForgeHooksClient;
import net.minecraftforge.client.event.RenderTooltipEvent;
import org.joml.Matrix4f;

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
  public void renderTooltip(ItemStack stack, PoseStack poseStack, List<Component> text, int mouseX,
                            int mouseY, AbstractContainerScreen<?> containerScreen, Font font,
                            SelectionWidget.GradientDrawer drawer) {

    if (!text.isEmpty()) {
      List<ClientTooltipComponent> components =
          ForgeHooksClient.gatherTooltipComponents(stack, text, mouseX, containerScreen.width,
              containerScreen.height, font, font);
      RenderTooltipEvent.Pre preEvent =
          ForgeHooksClient.onRenderTooltipPre(stack, poseStack, mouseX, mouseY,
              containerScreen.width, containerScreen.height, components, font, font);

      if (preEvent.isCanceled()) {
        return;
      }
      int i = 0;
      int j = text.size() == 1 ? -2 : 0;

      for (ClientTooltipComponent clienttooltipcomponent : components) {
        int k = clienttooltipcomponent.getWidth(preEvent.getFont());

        if (k > i) {
          i = k;
        }
        j += clienttooltipcomponent.getHeight();
      }
      int j2 = preEvent.getX() + 12;
      int k2 = preEvent.getY() - 12;
      if (j2 + i > containerScreen.width) {
        j2 -= 28 + i;
      }

      if (k2 + j + 6 > containerScreen.height) {
        k2 = containerScreen.height - j - 6;
      }

      poseStack.pushPose();
      ItemRenderer itemRenderer = containerScreen.getMinecraft().getItemRenderer();
      float f = itemRenderer.blitOffset;
      int blitOffset = 901;
      itemRenderer.blitOffset = blitOffset;
      Tesselator tesselator = Tesselator.getInstance();
      BufferBuilder bufferbuilder = tesselator.getBuilder();
      RenderSystem.setShader(GameRenderer::getPositionColorShader);
      bufferbuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);
      Matrix4f matrix4f = poseStack.last().pose();
      RenderTooltipEvent.Color colorEvent =
          ForgeHooksClient.onRenderTooltipColor(stack, poseStack, j2, k2, preEvent.getFont(),
              components);
      drawer.fillGradient(matrix4f, bufferbuilder, j2 - 3, k2 - 4, j2 + i + 3, k2 - 3,
          blitOffset, colorEvent.getBackgroundStart(), colorEvent.getBackgroundStart());
      drawer.fillGradient(matrix4f, bufferbuilder, j2 - 3, k2 + j + 3, j2 + i + 3, k2 + j + 4,
          blitOffset, colorEvent.getBackgroundEnd(), colorEvent.getBackgroundEnd());
      drawer.fillGradient(matrix4f, bufferbuilder, j2 - 3, k2 - 3, j2 + i + 3, k2 + j + 3,
          blitOffset, colorEvent.getBackgroundStart(), colorEvent.getBackgroundEnd());
      drawer.fillGradient(matrix4f, bufferbuilder, j2 - 4, k2 - 3, j2 - 3, k2 + j + 3, blitOffset,
          colorEvent.getBackgroundStart(), colorEvent.getBackgroundEnd());
      drawer.fillGradient(matrix4f, bufferbuilder, j2 + i + 3, k2 - 3, j2 + i + 4, k2 + j + 3,
          blitOffset, colorEvent.getBackgroundStart(), colorEvent.getBackgroundEnd());
      drawer.fillGradient(matrix4f, bufferbuilder, j2 - 3, k2 - 3 + 1, j2 - 3 + 1, k2 + j + 3 - 1,
          blitOffset, colorEvent.getBorderStart(), colorEvent.getBorderEnd());
      drawer.fillGradient(matrix4f, bufferbuilder, j2 + i + 2, k2 - 3 + 1, j2 + i + 3,
          k2 + j + 3 - 1, blitOffset, colorEvent.getBorderStart(), colorEvent.getBorderEnd());
      drawer.fillGradient(matrix4f, bufferbuilder, j2 - 3, k2 - 3, j2 + i + 3, k2 - 3 + 1,
          blitOffset, colorEvent.getBorderStart(), colorEvent.getBorderStart());
      drawer.fillGradient(matrix4f, bufferbuilder, j2 - 3, k2 + j + 2, j2 + i + 3, k2 + j + 3,
          blitOffset, colorEvent.getBorderEnd(), colorEvent.getBorderEnd());
      RenderSystem.enableDepthTest();
      RenderSystem.disableTexture();
      RenderSystem.enableBlend();
      RenderSystem.defaultBlendFunc();
      BufferUploader.drawWithShader(bufferbuilder.end());
      RenderSystem.disableBlend();
      RenderSystem.enableTexture();
      MultiBufferSource.BufferSource multibuffersource$buffersource =
          MultiBufferSource.immediate(Tesselator.getInstance().getBuilder());
      poseStack.translate(0.0D, 0.0D, blitOffset);
      int l1 = k2;

      for (int i2 = 0; i2 < components.size(); ++i2) {
        ClientTooltipComponent clienttooltipcomponent1 = components.get(i2);
        clienttooltipcomponent1.renderText(preEvent.getFont(), j2, l1, matrix4f,
            multibuffersource$buffersource);
        l1 += clienttooltipcomponent1.getHeight() + (i2 == 0 ? 2 : 0);
      }
      multibuffersource$buffersource.endBatch();
      poseStack.popPose();
      l1 = k2;

      for (int l2 = 0; l2 < components.size(); ++l2) {
        ClientTooltipComponent clienttooltipcomponent2 = components.get(l2);
        clienttooltipcomponent2.renderImage(preEvent.getFont(), j2, l1, poseStack, itemRenderer,
            blitOffset);
        l1 += clienttooltipcomponent2.getHeight() + (l2 == 0 ? 2 : 0);
      }
      itemRenderer.blitOffset = f;
    }
  }
}
