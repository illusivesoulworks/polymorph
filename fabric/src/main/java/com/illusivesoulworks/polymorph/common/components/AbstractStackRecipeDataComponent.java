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

package com.illusivesoulworks.polymorph.common.components;

import com.illusivesoulworks.polymorph.api.common.base.IRecipePair;
import com.illusivesoulworks.polymorph.api.common.capability.IStackRecipeData;
import com.illusivesoulworks.polymorph.common.capability.StackRecipeData;
import com.mojang.datafixers.util.Pair;
import dev.onyxstudios.cca.api.v3.item.ItemComponent;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.SortedSet;
import javax.annotation.Nonnull;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;

public abstract class AbstractStackRecipeDataComponent extends ItemComponent implements IStackRecipeData {

  private final StackRecipeData delegate;

  public AbstractStackRecipeDataComponent(ItemStack owner) {
    super(owner);
    this.delegate = new StackRecipeData(owner);
    this.loadFromTag();
  }

  private void loadFromTag() {
    CompoundTag root = this.getRootTag();
    if (root != null) {
      this.delegate.readNBT(root);
    }
  }

  private void saveToTag() {
    CompoundTag tag = this.delegate.writeNBT();
    CompoundTag root = this.getOrCreateRootTag();
    for (String key : new java.util.HashSet<>(root.getAllKeys())) {
      root.remove(key);
    }
    for (String key : tag.getAllKeys()) {
      root.put(key, tag.get(key));
    }
  }

  @Override
  public void onTagInvalidated() {
    super.onTagInvalidated();
    if (this.delegate != null) {
      this.loadFromTag();
    }
  }

  @Override
  public <T extends Recipe<C>, C extends Container> Optional<T> getRecipe(RecipeType<T> type,
                                                                          C inventory, Level level,
                                                                          List<T> recipesList) {
    Optional<T> recipe = this.delegate.getRecipe(type, inventory, level, recipesList);
    this.saveToTag();
    return recipe;
  }

  @Override
  public void selectRecipe(@Nonnull Recipe<?> recipe) {
    this.delegate.selectRecipe(recipe);
    this.saveToTag();
  }

  @Override
  public Optional<? extends Recipe<?>> getSelectedRecipe() {
    return this.delegate.getSelectedRecipe();
  }

  @Override
  public void setSelectedRecipe(@Nonnull Recipe<?> recipe) {
    this.delegate.setSelectedRecipe(recipe);
    this.saveToTag();
  }

  @Nonnull
  @Override
  public SortedSet<IRecipePair> getRecipesList() {
    return this.delegate.getRecipesList();
  }

  @Override
  public void setRecipesList(@Nonnull SortedSet<IRecipePair> recipesList) {
    this.delegate.setRecipesList(recipesList);
    this.saveToTag();
  }

  @Override
  public boolean isEmpty(Container inventory) {
    return this.delegate.isEmpty(inventory);
  }

  @Override
  public ItemStack getOwner() {
    return this.delegate.getOwner();
  }

  @Override
  public Set<ServerPlayer> getListeners() {
    return this.delegate.getListeners();
  }

  @Override
  public void sendRecipesListToListeners(boolean isEmpty) {
    this.delegate.sendRecipesListToListeners(isEmpty);
  }

  @Override
  public Pair<SortedSet<IRecipePair>, ResourceLocation> getPacketData() {
    return this.delegate.getPacketData();
  }

  @Override
  public boolean isFailing() {
    return this.delegate.isFailing();
  }

  @Override
  public void setFailing(boolean isFailing) {
    this.delegate.setFailing(isFailing);
    this.saveToTag();
  }

  @Override
  public void readNBT(CompoundTag compoundTag) {
    this.delegate.readNBT(compoundTag);
    this.saveToTag();
  }

  @Nonnull
  @Override
  public CompoundTag writeNBT() {
    return this.delegate.writeNBT();
  }
}
