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

import java.util.SortedSet;
import java.util.TreeSet;
import java.util.function.Supplier;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.network.NetworkEvent;
import top.theillusivec4.polymorph.api.common.base.IRecipePair;
import top.theillusivec4.polymorph.common.impl.RecipePair;

public class SPacketRecipesList {

  private final SortedSet<IRecipePair> recipeList;
  private final ResourceLocation selected;

  public SPacketRecipesList(SortedSet<IRecipePair> pRecipeList, ResourceLocation pSelected) {
    this.recipeList = new TreeSet<>();

    if (pRecipeList != null) {
      this.recipeList.addAll(pRecipeList);
    }
    this.selected = pSelected;
  }

  public SortedSet<IRecipePair> getRecipeList() {
    return this.recipeList;
  }

  public ResourceLocation getSelected() {
    return this.selected;
  }

  public static void encode(SPacketRecipesList pPacket, PacketBuffer pBuffer) {

    if (!pPacket.recipeList.isEmpty()) {
      pBuffer.writeInt(pPacket.recipeList.size());

      for (IRecipePair data : pPacket.recipeList) {
        pBuffer.writeResourceLocation(data.getResourceLocation());
        pBuffer.writeItem(data.getOutput());
      }

      if (pPacket.selected != null) {
        pBuffer.writeResourceLocation(pPacket.selected);
      }
    }
  }

  public static SPacketRecipesList decode(PacketBuffer pBuffer) {
    SortedSet<IRecipePair> recipeDataset = new TreeSet<>();
    ResourceLocation selected = null;

    if (pBuffer.isReadable()) {
      int size = pBuffer.readInt();

      for (int i = 0; i < size; i++) {
        recipeDataset.add(new RecipePair(pBuffer.readResourceLocation(), pBuffer.readItem()));
      }

      if (pBuffer.isReadable()) {
        selected = pBuffer.readResourceLocation();
      }
    }
    return new SPacketRecipesList(recipeDataset, selected);
  }

  public static void handle(SPacketRecipesList pPacket, Supplier<NetworkEvent.Context> pContext) {
    pContext.get().enqueueWork(() -> DistExecutor.unsafeRunWhenOn(Dist.CLIENT,
        () -> () -> ClientPacketHandler.handle(pPacket)));
    pContext.get().setPacketHandled(true);
  }
}
