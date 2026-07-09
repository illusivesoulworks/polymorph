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

import com.illusivesoulworks.polymorph.api.common.base.IRecipePair;
import com.illusivesoulworks.polymorph.common.impl.RecipePair;
import java.util.SortedSet;
import java.util.TreeSet;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;

public record SPacketRecipesList(
    SortedSet<IRecipePair> recipeList,
    ResourceLocation selected) {

  public SPacketRecipesList(SortedSet<IRecipePair> recipeList, ResourceLocation selected) {
    this.recipeList = new TreeSet<>();

    if (recipeList != null) {
      this.recipeList.addAll(recipeList);
    }
    this.selected = selected;
  }

  public SortedSet<IRecipePair> getRecipeList() {
    return this.recipeList;
  }

  public ResourceLocation getSelected() {
    return this.selected;
  }

  public static void encode(SPacketRecipesList packet, FriendlyByteBuf buffer) {

    if (!packet.recipeList.isEmpty()) {
      buffer.writeInt(packet.recipeList.size());

      for (IRecipePair data : packet.recipeList) {
        buffer.writeResourceLocation(data.getResourceLocation());
        buffer.writeItem(data.getOutput());
      }

      if (packet.selected != null) {
        buffer.writeResourceLocation(packet.selected);
      }
    }
  }

  public static SPacketRecipesList decode(FriendlyByteBuf buffer) {
    SortedSet<IRecipePair> recipeDataset = new TreeSet<>();
    ResourceLocation selected = null;

    if (buffer.isReadable()) {
      int size = buffer.readInt();

      for (int i = 0; i < size; i++) {
        recipeDataset.add(new RecipePair(buffer.readResourceLocation(), buffer.readItem()));
      }

      if (buffer.isReadable()) {
        selected = buffer.readResourceLocation();
      }
    }
    return new SPacketRecipesList(recipeDataset, selected);
  }

  public static void handle(SPacketRecipesList packet) {
    ClientPacketHandler.handle(packet);
  }
}
