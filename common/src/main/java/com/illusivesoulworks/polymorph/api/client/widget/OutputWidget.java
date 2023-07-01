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

package com.illusivesoulworks.polymorph.api.client.widget;

import com.illusivesoulworks.polymorph.api.common.base.IRecipePair;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import java.util.List;
import javax.annotation.Nonnull;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

public class OutputWidget extends AbstractWidget {

  private final ItemStack output;
  private final ResourceLocation resourceLocation;
  private boolean highlighted = false;

  public OutputWidget(IRecipePair recipePair) {
    super(0, 0, 25, 25, Component.empty());
    this.output = recipePair.getOutput();
    this.resourceLocation = recipePair.getResourceLocation();
  }

  @Override
  public void renderWidget(@Nonnull PoseStack poseStack, int mouseX, int mouseY,
                           float partialTicks) {
    Minecraft minecraft = Minecraft.getInstance();
    RenderSystem.setShaderTexture(0, AbstractRecipesWidget.WIDGETS);
    int j = 0;

    if (this.getX() + 25 > mouseX && this.getX() <= mouseX &&
        this.getY() + 25 > mouseY && this.getY() <= mouseY) {
      j += 25;
    }
    blit(poseStack, this.getX(), this.getY(), 600, this.highlighted ? 41 : 16, j, this.width,
        this.height, 256, 256);
    int k = 4;
    ItemRenderer itemRenderer = minecraft.getItemRenderer();
    poseStack.pushPose();
//    poseStack.translate(1);
    itemRenderer.renderAndDecorateItem(poseStack, this.getOutput(), this.getX() + k,
        this.getY() + k);
    itemRenderer.renderGuiItemDecorations(poseStack, minecraft.font, this.getOutput(),
        this.getX() + k, this.getY() + k);
    poseStack.popPose();
  }

  public ItemStack getOutput() {
    return this.output;
  }

  public ResourceLocation getResourceLocation() {
    return this.resourceLocation;
  }

  public void setHighlighted(boolean highlighted) {
    this.highlighted = highlighted;
  }

  public List<ClientTooltipComponent> getTooltipText(Screen screen) {
    return screen.getTooltipFromItem(this.getOutput()).stream().map(Component::getVisualOrderText)
        .map(ClientTooltipComponent::create).toList();
  }

  @Override
  public int getWidth() {
    return 25;
  }

  @Override
  protected void updateWidgetNarration(@Nonnull NarrationElementOutput var1) {

  }

  @Override
  protected boolean isValidClickButton(int button) {
    return button == 0 || button == 1;
  }
}
