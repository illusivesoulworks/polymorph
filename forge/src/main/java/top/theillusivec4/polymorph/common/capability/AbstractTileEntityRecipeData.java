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
import java.util.HashSet;
import java.util.Set;
import java.util.SortedSet;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import top.theillusivec4.polymorph.api.PolymorphApi;
import top.theillusivec4.polymorph.api.common.base.IRecipePair;
import top.theillusivec4.polymorph.api.common.capability.ITileEntityRecipeData;

public abstract class AbstractTileEntityRecipeData<E extends TileEntity>
    extends AbstractRecipeData<TileEntity> implements ITileEntityRecipeData {

  private NonNullList<ItemStack> lastInput;

  public AbstractTileEntityRecipeData(E pOwner) {
    super(pOwner);
    this.lastInput = NonNullList.create();
  }

  protected abstract NonNullList<ItemStack> getInput();

  @Override
  public void tick() {
    boolean changed = false;
    NonNullList<ItemStack> currentInput = this.getInput();
    this.lastInput = validateList(this.lastInput, currentInput.size());

    for (int i = 0; i < currentInput.size(); i++) {
      ItemStack lastStack = this.lastInput.get(i);
      ItemStack currentStack = currentInput.get(i);

      if (!ItemStack.areItemsEqual(lastStack, currentStack)) {
        changed = true;
      }
      this.lastInput.set(i, currentStack.copy());
    }

    if (changed) {
      this.sendRecipesListToListeners(this.isFailing() || this.isEmpty());
    }
  }

  private NonNullList<ItemStack> validateList(NonNullList<ItemStack> pList, int pSize) {

    if (pList.size() == pSize) {
      return pList;
    } else {
      NonNullList<ItemStack> resized = NonNullList.withSize(pSize, ItemStack.EMPTY);

      for (int i = 0; i < Math.min(resized.size(), pList.size()); i++) {
        resized.set(i, pList.get(i));
      }
      return resized;
    }
  }

  @Override
  public Set<ServerPlayerEntity> getListeners() {
    World world = this.getOwner().getWorld();
    Set<ServerPlayerEntity> players = new HashSet<>();

    if (world instanceof ServerWorld) {

      for (ServerPlayerEntity player : ((ServerWorld) world).getWorldServer().getPlayers()) {
        PolymorphApi.common().getRecipeDataFromTileEntity(player.openContainer)
            .ifPresent(recipeData -> {
              if (recipeData == this) {
                players.add(player);
              }
            });
      }
    }
    return players;
  }

  @SuppressWarnings("unchecked")
  @Override
  public E getOwner() {
    return (E) super.getOwner();
  }

  public boolean isEmpty() {

    for (ItemStack stack : this.getInput()) {

      if (!stack.isEmpty()) {
        return false;
      }
    }
    return true;
  }

  @Override
  public Pair<SortedSet<IRecipePair>, ResourceLocation> getPacketData() {
    return new Pair<>(this.getRecipesList(), null);
  }

  @Override
  public boolean isEmpty(IInventory pInventory) {
    return this.isEmpty();
  }
}
