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
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.UUID;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
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

  private final Map<UUID, ServerPlayerEntity> listeners = new HashMap<>();

  private NonNullList<Item> lastInput;

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
      Item lastItem = this.lastInput.get(i);
      ItemStack currentStack = currentInput.get(i);
      Item item = Items.AIR;

      if (currentStack.getCount() > 0) {
        item = currentStack.getItem();
      }

      if (lastItem != currentStack.getItem()) {
        changed = true;
      }
      this.lastInput.set(i, item);
    }

    if (changed) {
      this.sendRecipesListToListeners(this.isFailing() || this.isEmpty());
    }
  }

  @Override
  public void addListener(ServerPlayerEntity serverPlayer) {
    this.listeners.put(serverPlayer.getUniqueID(), serverPlayer);
  }

  @Override
  public void removeListener(ServerPlayerEntity serverPlayer) {
    this.listeners.remove(serverPlayer.getUniqueID());
  }

  private NonNullList<Item> validateList(NonNullList<Item> pList, int pSize) {

    if (pList.size() == pSize) {
      return pList;
    } else {
      NonNullList<Item> resized = NonNullList.withSize(pSize, Items.AIR);

      for (int i = 0; i < Math.min(resized.size(), pList.size()); i++) {
        resized.set(i, pList.get(i));
      }
      return resized;
    }
  }

  @Override
  public Set<ServerPlayerEntity> getListeners() {
    return new HashSet<>(this.listeners.values());
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
