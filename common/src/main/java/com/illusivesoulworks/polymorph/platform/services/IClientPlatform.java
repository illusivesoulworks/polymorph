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

package com.illusivesoulworks.polymorph.platform.services;

import com.illusivesoulworks.polymorph.api.client.widget.SelectionWidget;
import com.mojang.blaze3d.vertex.PoseStack;
import java.util.List;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipPositioner;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

public interface IClientPlatform {

  int getScreenTop(AbstractContainerScreen<?> screen);

  int getScreenLeft(AbstractContainerScreen<?> screen);

  void renderTooltip(ItemStack stack, PoseStack poseStack, List<ClientTooltipComponent> text,
                     ClientTooltipPositioner positioner, int mouseX, int mouseY,
                     AbstractContainerScreen<?> containerScreen, Font font,
                     SelectionWidget.GradientDrawer drawer);
}
