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

package top.theillusivec4.polymorph.api.client.base;

import com.mojang.blaze3d.matrix.MatrixStack;
import java.util.Set;
import net.minecraft.inventory.container.Slot;
import net.minecraft.util.ResourceLocation;
import top.theillusivec4.polymorph.api.client.widget.SelectionWidget;
import top.theillusivec4.polymorph.api.common.base.IRecipePair;

public interface IRecipesWidget {

  void initChildWidgets();

  void selectRecipe(ResourceLocation pResourceLocation);

  void highlightRecipe(ResourceLocation pResourceLocation);

  void setRecipesList(Set<IRecipePair> pRecipesList, ResourceLocation pSelected);

  void render(MatrixStack pMatrixStack, int pMouseX, int pMouseY, float pRenderPartialTicks);

  boolean mouseClicked(double pMouseX, double pMouseY, int pButton);

  Slot getOutputSlot();

  SelectionWidget getSelectionWidget();

  int getXPos();

  int getYPos();
}
