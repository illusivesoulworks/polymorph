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

package com.illusivesoulworks.polymorph.common.network.server;

import com.illusivesoulworks.polymorph.api.PolymorphApi;
import com.illusivesoulworks.polymorph.api.client.base.IRecipesWidget;
import com.illusivesoulworks.polymorph.client.recipe.RecipesWidget;
import com.illusivesoulworks.polymorph.common.capability.PolymorphCapabilities;
import java.util.Optional;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.level.block.entity.BlockEntity;

public class ClientPacketHandler {

  public static void handle(SPacketPlayerRecipeSync packet) {
    LocalPlayer clientPlayerEntity = Minecraft.getInstance().player;

    if (clientPlayerEntity != null) {
      PolymorphApi.common().getRecipeData(clientPlayerEntity).ifPresent(recipeData -> {
        recipeData.setRecipesList(packet.getRecipeList());
        clientPlayerEntity.level().getRecipeManager().byKey(packet.getSelected()).ifPresent(
            recipeData::setSelectedRecipe);
      });
    }
  }

  public static void handle(SPacketBlockEntityRecipeSync packet) {
    ClientLevel level = Minecraft.getInstance().level;

    if (level != null) {
      BlockEntity blockEntity = level.getBlockEntity(packet.getBlockPos());

      if (blockEntity != null) {
        PolymorphCapabilities.getRecipeData(blockEntity)
            .ifPresent(recipeData -> level.getRecipeManager().byKey(packet.getSelected())
                .ifPresent(recipeData::setSelectedRecipe));
      }
    }
  }

  public static void handle(SPacketRecipesList packet) {
    LocalPlayer clientPlayerEntity = Minecraft.getInstance().player;

    if (clientPlayerEntity != null) {
      Optional<IRecipesWidget> maybeWidget = RecipesWidget.get();
      maybeWidget.ifPresent(
          widget -> widget.setRecipesList(packet.getRecipeList(), packet.getSelected()));

      if (maybeWidget.isEmpty()) {
        RecipesWidget.enqueueRecipesList(packet.getRecipeList(), packet.getSelected());
      }
    }
  }

  public static void handle(SPacketHighlightRecipe packet) {
    LocalPlayer clientPlayerEntity = Minecraft.getInstance().player;

    if (clientPlayerEntity != null) {
      RecipesWidget.get().ifPresent(widget -> widget.highlightRecipe(packet.getRecipe()));
    }
  }
}
