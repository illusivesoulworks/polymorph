/*
 * Copyright (c) 2020 C4
 *
 * This file is part of Polymorph, a mod made for Minecraft.
 *
 * Polymorph is free software: you can redistribute it and/or modify it under the terms of the GNU
 * Lesser General Public License as published by the Free Software Foundation, either version 3 of
 * the License, or any later version.
 *
 * Polymorph is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without
 * even the implied warranty of MERCHANTABILITY or FITNESS FOR PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with Polymorph.
 * If not, see <https://www.gnu.org/licenses/>.
 */

package top.theillusivec4.polymorph.loader.common;

import dev.onyxstudios.cca.api.v3.block.BlockComponentFactoryRegistry;
import dev.onyxstudios.cca.api.v3.block.BlockComponentInitializer;
import java.util.List;
import java.util.Optional;
import net.minecraft.block.entity.AbstractFurnaceBlockEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.recipe.AbstractCookingRecipe;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeManager;
import net.minecraft.recipe.RecipeType;
import net.minecraft.screen.AbstractFurnaceScreenHandler;
import net.minecraft.screen.BlastFurnaceScreenHandler;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.SmokerScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.world.World;
import top.theillusivec4.polymorph.api.PolymorphApi;
import top.theillusivec4.polymorph.api.PolymorphComponent;
import top.theillusivec4.polymorph.api.type.FurnaceProvider;
import top.theillusivec4.polymorph.api.type.PersistentSelector;
import top.theillusivec4.polymorph.core.FurnaceSelector;

public class PolymorphComponentImpl implements BlockComponentInitializer {

  @Override
  public void registerBlockComponentFactories(
      BlockComponentFactoryRegistry blockComponentFactoryRegistry) {
    PolymorphApi.getInstance().addEntityProvider(blockEntity -> {
      if (blockEntity instanceof AbstractFurnaceBlockEntity) {
        return new FurnaceSelector((AbstractFurnaceBlockEntity) blockEntity);
      }
      return null;
    }, screenHandler -> {
      if (screenHandler instanceof AbstractFurnaceScreenHandler) {
        return new SimpleFurnaceProvider(screenHandler);
      }
      return null;
    });
    blockComponentFactoryRegistry
        .registerFor(BlockEntity.class, PolymorphComponent.SELECTOR,
            blockEntity -> PolymorphApi.getInstance().getSelector(blockEntity)
                .orElse(new EmptySelector()));
  }

  public static class SimpleFurnaceProvider implements FurnaceProvider {

    final ScreenHandler container;
    final Inventory input;
    final RecipeType<? extends AbstractCookingRecipe> recipeType;

    public SimpleFurnaceProvider(ScreenHandler container) {
      this.container = container;
      this.input = container.slots.get(0).inventory;
      this.recipeType = this.getRecipeType();
    }

    private RecipeType<? extends AbstractCookingRecipe> getRecipeType() {

      if (this.container instanceof SmokerScreenHandler) {
        return RecipeType.SMOKING;
      } else if (this.container instanceof BlastFurnaceScreenHandler) {
        return RecipeType.BLASTING;
      } else {
        return RecipeType.SMELTING;
      }
    }

    @Override
    public ScreenHandler getScreenHandler() {
      return this.container;
    }

    @Override
    public Inventory getInventory() {
      return this.input;
    }

    @Override
    public List<? extends AbstractCookingRecipe> getRecipes(World world,
                                                            RecipeManager recipeManager) {
      return recipeManager.getAllMatches(this.recipeType, this.getInventory(), world);
    }

    @Override
    public Slot getOutputSlot() {
      return this.container.slots.get(2);
    }
  }

  private static class EmptySelector implements PersistentSelector {

    @Override
    public Optional<Recipe<?>> fetchRecipe(World world) {
      return Optional.empty();
    }

    @Override
    public RecipeType<? extends Recipe<?>> getRecipeType() {
      return null;
    }

    @Override
    public Optional<? extends Recipe<?>> getSelectedRecipe() {
      return Optional.empty();
    }

    @Override
    public void setSavedRecipe(String recipe) {

    }

    @Override
    public void setSelectedRecipe(Recipe<?> recipe) {

    }

    @Override
    public BlockEntity getParent() {
      return null;
    }

    @Override
    public void readFromNbt(CompoundTag tag) {

    }

    @Override
    public void writeToNbt(CompoundTag tag) {

    }
  }
}
