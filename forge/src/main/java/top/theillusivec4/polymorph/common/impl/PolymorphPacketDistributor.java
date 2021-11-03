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

package top.theillusivec4.polymorph.common.impl;

import java.util.SortedSet;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.network.PacketDistributor;
import top.theillusivec4.polymorph.api.common.base.IPolymorphPacketDistributor;
import top.theillusivec4.polymorph.api.common.base.IRecipePair;
import top.theillusivec4.polymorph.common.network.PolymorphNetwork;
import top.theillusivec4.polymorph.common.network.client.CPacketPersistentRecipeSelection;
import top.theillusivec4.polymorph.common.network.client.CPacketPlayerRecipeSelection;
import top.theillusivec4.polymorph.common.network.client.CPacketStackRecipeSelection;
import top.theillusivec4.polymorph.common.network.server.SPacketHighlightRecipe;
import top.theillusivec4.polymorph.common.network.server.SPacketPlayerRecipeSync;
import top.theillusivec4.polymorph.common.network.server.SPacketRecipesList;

public class PolymorphPacketDistributor implements IPolymorphPacketDistributor {

  @Override
  public void sendPlayerRecipeSelectionC2S(ResourceLocation pResourceLocation) {
    PolymorphNetwork.get().send(PacketDistributor.SERVER.noArg(),
        new CPacketPlayerRecipeSelection(pResourceLocation));
  }

  @Override
  public void sendPersistentRecipeSelectionC2S(ResourceLocation pResourceLocation) {
    PolymorphNetwork.get().send(PacketDistributor.SERVER.noArg(),
        new CPacketPersistentRecipeSelection(pResourceLocation));
  }

  @Override
  public void sendStackRecipeSelectionC2S(ResourceLocation pResourceLocation) {
    PolymorphNetwork.get().send(PacketDistributor.SERVER.noArg(),
        new CPacketStackRecipeSelection(pResourceLocation));
  }

  @Override
  public void sendRecipesListS2C(ServerPlayerEntity pPlayer) {
    sendRecipesListS2C(pPlayer, null);
  }

  @Override
  public void sendRecipesListS2C(ServerPlayerEntity pPlayer, SortedSet<IRecipePair> pRecipesList) {
    PolymorphNetwork.get().send(PacketDistributor.PLAYER.with(() -> pPlayer),
        new SPacketRecipesList(pRecipesList, null));
  }

  @Override
  public void sendRecipesListS2C(ServerPlayerEntity pPlayer, SortedSet<IRecipePair> pRecipesList,
                                 ResourceLocation pSelected) {
    PolymorphNetwork.get().send(PacketDistributor.PLAYER.with(() -> pPlayer),
        new SPacketRecipesList(pRecipesList, pSelected));
  }

  @Override
  public void sendHighlightRecipeS2C(ServerPlayerEntity pPlayer,
                                     ResourceLocation pResourceLocation) {
    PolymorphNetwork.get().send(PacketDistributor.PLAYER.with(() -> pPlayer),
        new SPacketHighlightRecipe(pResourceLocation));
  }

  @Override
  public void sendPlayerSyncS2C(ServerPlayerEntity pPlayer, SortedSet<IRecipePair> pRecipesList,
                                ResourceLocation pSelected) {
    PolymorphNetwork.get().send(PacketDistributor.PLAYER.with(() -> pPlayer),
        new SPacketPlayerRecipeSync(pRecipesList, pSelected));
  }
}
