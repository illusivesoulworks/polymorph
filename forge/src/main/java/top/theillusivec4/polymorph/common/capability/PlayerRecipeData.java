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

package top.theillusivec4.polymorph.common.capability;

import com.mojang.datafixers.util.Pair;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import javax.annotation.Nonnull;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import top.theillusivec4.polymorph.api.PolymorphApi;
import top.theillusivec4.polymorph.api.common.base.IRecipePair;
import top.theillusivec4.polymorph.api.common.capability.IPlayerRecipeData;
import top.theillusivec4.polymorph.client.recipe.RecipesWidget;

public class PlayerRecipeData extends AbstractRecipeData<PlayerEntity> implements
    IPlayerRecipeData {

  public PlayerRecipeData(PlayerEntity pOwner) {
    super(pOwner);
  }

  @Override
  public <T extends IRecipe<C>, C extends IInventory> Optional<T> getRecipe(IRecipeType<T> pType,
                                                                            C pInventory,
                                                                            World pWorld,
                                                                            List<T> pRecipes) {
    Optional<T> maybeRecipe = super.getRecipe(pType, pInventory, pWorld, pRecipes);
    this.syncPlayerRecipeData();
    return maybeRecipe;
  }

  @Override
  public void selectRecipe(@Nonnull IRecipe<?> pRecipe) {
    super.selectRecipe(pRecipe);
    this.syncPlayerRecipeData();
  }

  private void syncPlayerRecipeData() {

    if (this.getOwner() instanceof ServerPlayerEntity) {
      PolymorphApi.common().getPacketDistributor()
          .sendPlayerSyncS2C((ServerPlayerEntity) this.getOwner(), this.getRecipesList(),
              this.getSelectedRecipe().map(IRecipe::getId).orElse(null));
    }
  }

  @Override
  public void sendRecipesListToListeners(boolean pEmpty) {
    Pair<SortedSet<IRecipePair>, ResourceLocation> packetData =
        pEmpty ? new Pair<>(new TreeSet<>(), null) : this.getPacketData();
    PlayerEntity player = this.getOwner();

    if (player.world.isRemote()) {
      RecipesWidget.get().ifPresent(
          widget -> widget.setRecipesList(packetData.getFirst(), packetData.getSecond()));
    } else if (player instanceof ServerPlayerEntity) {
      PolymorphApi.common().getPacketDistributor()
          .sendRecipesListS2C((ServerPlayerEntity) player, packetData.getFirst(),
              packetData.getSecond());
    }
  }

  @Override
  public Set<ServerPlayerEntity> getListeners() {
    PlayerEntity player = this.getOwner();

    if (player instanceof ServerPlayerEntity) {
      return Collections.singleton((ServerPlayerEntity) player);
    } else {
      return new HashSet<>();
    }
  }
}
