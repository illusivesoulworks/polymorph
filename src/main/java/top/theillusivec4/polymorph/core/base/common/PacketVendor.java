/*
 * Copyright (c) 2020 C4
 *
 * This file is part of Polymorph, a mod made for Minecraft.
 *
 * Polymorph is free software: you can redistribute it and/or modify it under the terms of the GNU
 * Lesser General Public License as published by the Free Software Foundation, either version 3 of
 * the License, or any later version.
 *
 * Polymorph is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without
 * even the implied warranty of MERCHANTABILITY or FITNESS FOR PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with Polymorph.
 * If not, see <https://www.gnu.org/licenses/>.
 */

package top.theillusivec4.polymorph.core.base.common;

import java.util.List;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;

public interface PacketVendor {

  void sendSetRecipe(String recipeId);

  void sendSetCraftingRecipe(String recipeId);

  void sendTransferRecipe(String recipeId);

  void sendRecipes(List<String> recipes, String selectedRecipe, ServerPlayerEntity player);

  void syncOutput(ItemStack stack, ServerPlayerEntity player);

  void highlightRecipe(String recipeId, ServerPlayerEntity player);

  void fetchRecipes();
}
