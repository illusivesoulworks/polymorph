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

package top.theillusivec4.polymorph.common.network.server;

import java.util.Optional;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import top.theillusivec4.polymorph.api.PolymorphApi;
import top.theillusivec4.polymorph.api.client.base.IRecipesWidget;
import top.theillusivec4.polymorph.client.recipe.RecipesWidget;

public class ClientPacketHandler {

  public static void handle(SPacketPlayerRecipeSync pPacket) {
    ClientPlayerEntity clientPlayerEntity = Minecraft.getInstance().player;

    if (clientPlayerEntity != null) {
      PolymorphApi.common().getRecipeData(clientPlayerEntity).ifPresent(recipeData -> {
        recipeData.setRecipesList(pPacket.getRecipeList());
        clientPlayerEntity.world.getRecipeManager().getRecipe(pPacket.getSelected()).ifPresent(
            recipeData::setSelectedRecipe);
      });
    }
  }

  public static void handle(SPacketRecipesList pPacket) {
    ClientPlayerEntity clientPlayerEntity = Minecraft.getInstance().player;

    if (clientPlayerEntity != null) {
      Optional<IRecipesWidget> maybeWidget = RecipesWidget.get();
      maybeWidget.ifPresent(
          widget -> widget.setRecipesList(pPacket.getRecipeList(), pPacket.getSelected()));

      if (!maybeWidget.isPresent()) {
        RecipesWidget.enqueueRecipesList(pPacket.getRecipeList(), pPacket.getSelected());
      }
    }
  }

  public static void handle(SPacketHighlightRecipe pPacket) {
    ClientPlayerEntity clientPlayerEntity = Minecraft.getInstance().player;

    if (clientPlayerEntity != null) {
      RecipesWidget.get().ifPresent(widget -> widget.highlightRecipe(pPacket.getRecipe()));
    }
  }
}
