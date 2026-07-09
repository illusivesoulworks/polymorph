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

package com.illusivesoulworks.polymorph.common.network.client;

import com.illusivesoulworks.polymorph.api.PolymorphApi;
import com.illusivesoulworks.polymorph.common.integration.AbstractCompatibilityModule;
import com.illusivesoulworks.polymorph.common.integration.PolymorphIntegrations;
import java.util.Optional;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.level.Level;

public record CPacketPersistentRecipeSelection(ResourceLocation recipe) {

  public static void encode(CPacketPersistentRecipeSelection packet, FriendlyByteBuf buffer) {
    buffer.writeResourceLocation(packet.recipe);
  }

  public static CPacketPersistentRecipeSelection decode(FriendlyByteBuf buffer) {
    return new CPacketPersistentRecipeSelection(buffer.readResourceLocation());
  }

  public static void handle(CPacketPersistentRecipeSelection packet, ServerPlayer player) {
    Level world = player.getCommandSenderWorld();
    Optional<? extends Recipe<?>> maybeRecipe =
        world.getRecipeManager().byKey(packet.recipe);
    maybeRecipe.ifPresent(recipe -> {
      AbstractContainerMenu container = player.containerMenu;
      PolymorphApi.common().getRecipeDataFromBlockEntity(container)
          .ifPresent(recipeData -> {
            recipeData.selectRecipe(recipe);
            PolymorphIntegrations.selectRecipe(recipeData.getOwner(), container, recipe);
          });
    });
  }
}
