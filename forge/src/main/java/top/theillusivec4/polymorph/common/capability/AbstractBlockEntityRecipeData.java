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
import net.minecraft.core.NonNullList;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import top.theillusivec4.polymorph.api.PolymorphApi;
import top.theillusivec4.polymorph.api.common.base.IRecipePair;
import top.theillusivec4.polymorph.api.common.capability.IBlockEntityRecipeData;

public abstract class AbstractBlockEntityRecipeData<E extends BlockEntity>
    extends AbstractRecipeData<BlockEntity> implements IBlockEntityRecipeData {

  private NonNullList<ItemStack> lastInput;

  public AbstractBlockEntityRecipeData(E pOwner) {
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

      if (!ItemStack.isSame(lastStack, currentStack)) {
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
  public Set<ServerPlayer> getListeners() {
    Level world = this.getOwner().getLevel();
    Set<ServerPlayer> players = new HashSet<>();

    if (world instanceof ServerLevel) {

      for (ServerPlayer player : ((ServerLevel) world).getPlayers((serverPlayer -> true))) {
        PolymorphApi.common().getRecipeDataFromTileEntity(player.containerMenu)
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
  public boolean isEmpty(Container pInventory) {
    return this.isEmpty();
  }
}
