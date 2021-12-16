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

package top.theillusivec4.polymorph.client.recipe.widget;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.container.Slot;
import net.minecraft.util.ResourceLocation;
import top.theillusivec4.polymorph.api.PolymorphApi;
import top.theillusivec4.polymorph.api.client.widget.AbstractRecipesWidget;
import top.theillusivec4.polymorph.api.common.base.IPolymorphCommon;

public class PlayerRecipesWidget extends AbstractRecipesWidget {

  final Slot outputSlot;

  public PlayerRecipesWidget(ContainerScreen<?> containerScreen, Slot outputSlot) {
    super(containerScreen);
    this.outputSlot = outputSlot;
  }

  @Override
  public void selectRecipe(ResourceLocation pResourceLocation) {
    IPolymorphCommon commonApi = PolymorphApi.common();
    PlayerEntity player = Minecraft.getInstance().player;

    if (player != null) {
      player.world.getRecipeManager().getRecipe(pResourceLocation).ifPresent(
          recipe -> commonApi.getRecipeData(player)
              .ifPresent(recipeData -> recipeData.selectRecipe(recipe)));
    }
    commonApi.getPacketDistributor().sendPlayerRecipeSelectionC2S(pResourceLocation);
  }

  @Override
  public Slot getOutputSlot() {
    return this.outputSlot;
  }
}
