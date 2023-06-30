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
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.concurrent.atomic.AtomicReference;
import javax.annotation.Nonnull;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import top.theillusivec4.polymorph.api.PolymorphApi;
import top.theillusivec4.polymorph.api.common.base.IRecipePair;
import top.theillusivec4.polymorph.api.common.capability.IRecipeData;
import top.theillusivec4.polymorph.common.PolymorphMod;
import top.theillusivec4.polymorph.common.impl.RecipePair;

public abstract class AbstractRecipeData<E> implements IRecipeData<E> {

  private final SortedSet<IRecipePair> recipesList;
  private final E owner;

  private Recipe<?> lastRecipe;
  private Recipe<?> selectedRecipe;
  private ResourceLocation loadedRecipe;
  private boolean isFailing;

  private NonNullList<Item> input;

  public AbstractRecipeData(E pOwner) {
    this.recipesList = new TreeSet<>();
    this.owner = pOwner;
    this.input = NonNullList.create();
  }

  @SuppressWarnings("unchecked")
  @Override
  public <T extends Recipe<C>, C extends Container> Optional<T> getRecipe(RecipeType<T> pType,
                                                                          C pInventory,
                                                                          Level pWorld,
                                                                          List<T> pRecipes) {
    boolean isEmpty = this.isEmpty(pInventory);
    this.getLoadedRecipe().flatMap(id -> pWorld.getRecipeManager().byKey(id))
        .ifPresent(selected -> {
          try {
            if (selected.getType() == pType &&
                (((T) selected).matches(pInventory, pWorld) || isEmpty)) {
              this.setSelectedRecipe(selected);
            }
          } catch (ClassCastException e) {
            PolymorphMod.LOGGER.error("Recipe {} does not match inventory {}",
                selected.getId(), pInventory);
          }
          this.loadedRecipe = null;
        });

    if (isEmpty) {
      this.setFailing(false);
      this.sendRecipesListToListeners(true);
      return Optional.empty();
    }
    AtomicReference<T> ref = new AtomicReference<>(null);
    this.getLastRecipe().ifPresent(recipe -> {
      try {
        if (recipe.getType() == pType && ((T) recipe).matches(pInventory, pWorld)) {
          this.getSelectedRecipe().ifPresent(selected -> {
            try {
              if (selected.getType() == pType && ((T) selected).matches(pInventory, pWorld)) {
                ref.set((T) selected);
              }
            } catch (ClassCastException e) {
              PolymorphMod.LOGGER.error("Recipe {} does not match inventory {}",
                  selected.getId(), pInventory);
            }
          });
        }
      } catch (ClassCastException e) {
        PolymorphMod.LOGGER.error("Recipe {} does not match inventory {}", recipe.getId(),
            pInventory);
      }
    });
    T result = ref.get();

    if (result != null && !(this instanceof AbstractBlockEntityRecipeData)) {
      boolean inputChanged = false;
      int size = pInventory.getContainerSize();
      NonNullList<Item> currentInput = NonNullList.withSize(size, Items.AIR);

      if (size != this.input.size()) {
        inputChanged = true;
      }

      for (int i = 0; i < size; i++) {
        ItemStack stack = pInventory.getItem(i);
        Item item = stack.getItem();

        if (!inputChanged && i < this.input.size() && item != this.input.get(i)) {
          inputChanged = true;
        }

        if (!stack.isEmpty()) {
          currentInput.set(i, item);
        }
      }
      this.input = currentInput;

      if (!inputChanged) {
        this.setFailing(false);
        this.sendRecipesListToListeners(false);
        return Optional.of(result);
      }
    }
    SortedSet<IRecipePair> newDataset = new TreeSet<>();
    List<T> recipes =
        pRecipes.isEmpty() ? pWorld.getRecipeManager().getRecipesFor(pType, pInventory, pWorld) :
            pRecipes;

    if (recipes.isEmpty()) {
      this.setFailing(true);
      this.sendRecipesListToListeners(true);
      return Optional.empty();
    }
    List<T> validRecipes = new ArrayList<>();

    for (T entry : recipes) {
      ResourceLocation id = entry.getId();
      ItemStack crafted = entry.assemble(pInventory);

      if (crafted.isEmpty()) {
        continue;
      }

      if (ref.get() == null &&
          this.getSelectedRecipe().map(recipe -> recipe.getId().equals(id)).orElse(false)) {
        ref.set(entry);
      }
      newDataset.add(new RecipePair(id, crafted));
      validRecipes.add(entry);
    }

    if (validRecipes.isEmpty()) {
      this.setFailing(true);
      this.sendRecipesListToListeners(true);
      return Optional.empty();
    }


    if (result == null) {
      ResourceLocation rl = newDataset.first().getResourceLocation();

      for (T recipe : recipes) {

        if (recipe.getId().equals(rl)) {
          result = recipe;
          break;
        }
      }
    }

    if (result == null) {
      this.setFailing(true);
      this.sendRecipesListToListeners(true);
      return Optional.empty();
    }
    this.setRecipesList(newDataset);
    result = ref.get();
    result = result != null ? result : validRecipes.get(0);
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
  public void setSelectedRecipe(@Nonnull Recipe<?> pRecipe) {
    this.selectedRecipe = pRecipe;
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
  public void setRecipesList(@Nonnull SortedSet<IRecipePair> pData) {
    this.recipesList.clear();
    this.recipesList.addAll(pData);
  }

  @Override
  public boolean isEmpty(Container pInventory) {

    if (pInventory != null) {

      for (int i = 0; i < pInventory.getContainerSize(); i++) {

        if (!pInventory.getItem(i).isEmpty()) {
          return false;
        }
      }
    }
    return true;
  }

  @Override
  public E getOwner() {
    return this.owner;
  }

  @Override
  public void selectRecipe(@Nonnull Recipe<?> pRecipe) {
    this.setSelectedRecipe(pRecipe);
  }

  @Override
  public abstract Set<ServerPlayer> getListeners();

  @Override
  public void sendRecipesListToListeners(boolean pEmpty) {
    Pair<SortedSet<IRecipePair>, ResourceLocation> packetData =
        pEmpty ? new Pair<>(new TreeSet<>(), null) : this.getPacketData();

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
  public void setFailing(boolean pFailing) {
    this.isFailing = pFailing;
  }

  @Override
  public void readNBT(CompoundTag pCompound) {

    if (pCompound.contains("SelectedRecipe")) {
      this.loadedRecipe = new ResourceLocation(pCompound.getString("SelectedRecipe"));
    }

    if (pCompound.contains("RecipeDataSet")) {
      Set<IRecipePair> dataset = this.getRecipesList();
      dataset.clear();
      ListTag list = pCompound.getList("RecipeDataSet", Tag.TAG_COMPOUND);

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
