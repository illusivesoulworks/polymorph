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

import com.illusivesoulworks.polymorph.PolymorphConstants;
import com.illusivesoulworks.polymorph.api.PolymorphApi;
import com.illusivesoulworks.polymorph.api.common.base.IPolymorphCommon;
import com.illusivesoulworks.polymorph.api.common.base.IRecipePair;
import com.illusivesoulworks.polymorph.api.common.capability.IStackRecipeData;
import com.illusivesoulworks.polymorph.common.impl.RecipePair;
import com.mojang.datafixers.util.Pair;
import dev.onyxstudios.cca.api.v3.item.ItemComponent;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.concurrent.atomic.AtomicReference;
import javax.annotation.Nonnull;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;

public abstract class AbstractStackRecipeDataComponent extends ItemComponent implements IStackRecipeData {

  private final SortedSet<IRecipePair> recipesList;
  private final ItemStack owner;

  private Recipe<?> lastRecipe;
  private Recipe<?> selectedRecipe;
  private ResourceLocation loadedRecipe;
  private boolean isFailing;

  public AbstractStackRecipeDataComponent(ItemStack owner) {
    super(owner);
    this.recipesList = new TreeSet<>();
    this.owner = owner;
  }

  @SuppressWarnings("unchecked")
  @Override
  public <T extends Recipe<C>, C extends Container> Optional<T> getRecipe(RecipeType<T> type,
                                                                          C inventory, Level level,
                                                                          List<T> recipesList) {
    this.getLoadedRecipe().flatMap(id -> level.getRecipeManager().byKey(id))
        .ifPresent(selected -> {
          try {
            if (selected.getType() == type &&
                (((T) selected).matches(inventory, level) || isEmpty(inventory))) {
              this.setSelectedRecipe(selected);
            }
          } catch (ClassCastException e) {
            PolymorphConstants.LOG.error("Recipe {} does not match inventory {}",
                selected.getId(), inventory);
          }
          this.loadedRecipe = null;
        });

    if (this.isEmpty(inventory)) {
      this.setFailing(false);
      this.sendRecipesListToListeners(true);
      return Optional.empty();
    }
    AtomicReference<T> ref = new AtomicReference<>(null);
    this.getLastRecipe().ifPresent(recipe -> {
      try {
        if (recipe.getType() == type && ((T) recipe).matches(inventory, level)) {
          this.getSelectedRecipe().ifPresent(selected -> {
            try {
              if (selected.getType() == type && ((T) selected).matches(inventory, level)) {
                ref.set((T) selected);
              }
            } catch (ClassCastException e) {
              PolymorphConstants.LOG.error("Recipe {} does not match inventory {}",
                  selected.getId(), inventory);
            }
          });
        }
      } catch (ClassCastException e) {
        PolymorphConstants.LOG.error("Recipe {} does not match inventory {}", recipe.getId(),
            inventory);
      }
    });
    T result = ref.get();

    if (result != null) {
      this.setFailing(false);
      this.sendRecipesListToListeners(false);
      return Optional.of(result);
    }
    SortedSet<IRecipePair> newDataset = new TreeSet<>();
    List<T> recipes =
        recipesList.isEmpty() ? level.getRecipeManager().getRecipesFor(type, inventory, level) :
            recipesList;

    if (recipes.isEmpty()) {
      this.setFailing(true);
      this.sendRecipesListToListeners(true);
      return Optional.empty();
    }

    for (T entry : recipes) {
      ResourceLocation id = entry.getId();

      if (ref.get() == null &&
          this.getSelectedRecipe().map(recipe -> recipe.getId().equals(id)).orElse(false)) {
        ref.set(entry);
      }
      newDataset.add(new RecipePair(id, entry.assemble(inventory, level.registryAccess())));
    }
    this.setRecipesList(newDataset);
    result = ref.get();
    result = result != null ? result : recipes.get(0);
    this.lastRecipe = result;
    this.setSelectedRecipe(result);
    this.setFailing(false);
    this.sendRecipesListToListeners(false);
    return Optional.of(result);
  }

  @Override
  public Optional<? extends Recipe<?>> getSelectedRecipe() {
    return Optional.ofNullable(this.selectedRecipe);
  }

  @Override
  public void setSelectedRecipe(@Nonnull Recipe<?> recipe) {
    this.selectedRecipe = recipe;
  }

  public Optional<? extends Recipe<?>> getLastRecipe() {
    return Optional.ofNullable(this.lastRecipe);
  }

  public Optional<ResourceLocation> getLoadedRecipe() {
    return Optional.ofNullable(this.loadedRecipe);
  }

  @Nonnull
  @Override
  public SortedSet<IRecipePair> getRecipesList() {
    return this.recipesList;
  }

  @Override
  public void setRecipesList(@Nonnull SortedSet<IRecipePair> recipesList) {
    this.recipesList.clear();
    this.recipesList.addAll(recipesList);
  }

  @Override
  public boolean isEmpty(Container inventory) {

    if (inventory != null) {

      for (int i = 0; i < inventory.getContainerSize(); i++) {

        if (!inventory.getItem(i).isEmpty()) {
          return false;
        }
      }
    }
    return true;
  }

  @Override
  public ItemStack getOwner() {
    return this.owner;
  }

  @Override
  public void selectRecipe(@Nonnull Recipe<?> recipe) {
    this.setSelectedRecipe(recipe);
  }

  @Override
  public Set<ServerPlayer> getListeners() {
    Set<ServerPlayer> players = new HashSet<>();
    IPolymorphCommon commonApi = PolymorphApi.common();
    commonApi.getServer().ifPresent(server -> {
      for (ServerPlayer player : server.getPlayerList().getPlayers()) {
        commonApi.getRecipeDataFromItemStack(player.containerMenu)
            .ifPresent(recipeData -> {
              if (recipeData == this) {
                players.add(player);
              }
            });
      }
    });
    return players;
  }

  @Override
  public void sendRecipesListToListeners(boolean isEmpty) {
    Pair<SortedSet<IRecipePair>, ResourceLocation> packetData =
        isEmpty ? new Pair<>(new TreeSet<>(), null) : this.getPacketData();

    for (ServerPlayer listener : this.getListeners()) {
      PolymorphApi.common().getPacketDistributor()
          .sendRecipesListS2C(listener, packetData.getFirst(), packetData.getSecond());
    }
  }

  @Override
  public Pair<SortedSet<IRecipePair>, ResourceLocation> getPacketData() {
    return new Pair<>(this.getRecipesList(), null);
  }

  @Override
  public boolean isFailing() {
    return this.isFailing;
  }

  @Override
  public void setFailing(boolean isFailing) {
    this.isFailing = isFailing;
  }

  @Override
  public void readNBT(CompoundTag compoundTag) {

    if (compoundTag.contains("SelectedRecipe")) {
      this.loadedRecipe = new ResourceLocation(compoundTag.getString("SelectedRecipe"));
    }

    if (compoundTag.contains("RecipeDataSet")) {
      Set<IRecipePair> dataset = this.getRecipesList();
      dataset.clear();
      ListTag list = compoundTag.getList("RecipeDataSet", Tag.TAG_COMPOUND);

      for (Tag inbt : list) {
        CompoundTag tag = (CompoundTag) inbt;
        ResourceLocation id = ResourceLocation.tryParse(tag.getString("Id"));
        ItemStack stack = ItemStack.of(tag.getCompound("ItemStack"));
        dataset.add(new RecipePair(id, stack));
      }
    }
  }

  @Nonnull
  @Override
  public CompoundTag writeNBT() {
    CompoundTag nbt = new CompoundTag();
    this.getSelectedRecipe().ifPresent(
        selected -> nbt.putString("SelectedRecipe", this.selectedRecipe.getId().toString()));
    Set<IRecipePair> dataset = this.getRecipesList();

    if (!dataset.isEmpty()) {
      ListTag list = new ListTag();

      for (IRecipePair data : dataset) {
        CompoundTag tag = new CompoundTag();
        tag.put("ItemStack", data.getOutput().save(new CompoundTag()));
        tag.putString("Id", data.getResourceLocation().toString());
        list.add(tag);
      }
      nbt.put("RecipeDataSet", list);
    }
    return nbt;
  }
}
