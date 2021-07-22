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

package top.theillusivec4.polymorph.core;

import java.util.Comparator;
import java.util.Optional;
import net.minecraft.block.entity.AbstractFurnaceBlockEntity;
import net.minecraft.block.entity.BlastFurnaceBlockEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.SmokerBlockEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.recipe.AbstractCookingRecipe;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeType;
import net.minecraft.screen.AbstractFurnaceScreenHandler;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import net.minecraft.world.World;
import org.apache.commons.lang3.reflect.FieldUtils;
import top.theillusivec4.polymorph.api.type.PersistentSelector;
import top.theillusivec4.polymorph.loader.common.PolymorphLoader;
import top.theillusivec4.polymorph.loader.mixin.core.AbstractFurnaceScreenHandlerAccessor;

public class FurnaceSelector implements PersistentSelector {

  private final AbstractFurnaceBlockEntity parent;

  private AbstractCookingRecipe selectedRecipe;
  private ItemStack lastFailedInput = ItemStack.EMPTY;
  private String savedRecipe = "";

  public FurnaceSelector(AbstractFurnaceBlockEntity tileEntity) {
    this.parent = tileEntity;
  }

  @SuppressWarnings("unchecked")
  @Override
  public Optional<Recipe<?>> fetchRecipe(World world) {
    ItemStack input = parent.getStack(0);

    if (input == lastFailedInput) {
      return Optional.empty();
    }

    if (!savedRecipe.isEmpty()) {
      Optional<Recipe<?>> saved =
          (Optional<Recipe<?>>) world.getRecipeManager().get(new Identifier(savedRecipe));

      if (!saved.isPresent() || !((Recipe<Inventory>) saved.get()).matches(parent, world)) {
        savedRecipe = "";
      } else {
        this.setSelectedRecipe(saved.get());
        savedRecipe = "";
        return saved;
      }
    }
    Optional<Recipe<?>> maybeRecipe = world.getRecipeManager().values().stream()
        .filter((val) -> val.getType() == this.getRecipeType()).flatMap((val) -> Util
            .stream(this.getRecipeType().match((Recipe<Inventory>) val, world, parent)))
        .min(Comparator.comparing((recipe) -> recipe.getOutput().getTranslationKey()))
        .map((val) -> {
          this.setSelectedRecipe(val);
          return val;
        });

    if (!maybeRecipe.isPresent()) {
      lastFailedInput = input;
    }
    return maybeRecipe;
  }

  @Override
  public RecipeType<? extends Recipe<?>> getRecipeType() {
    if (this.parent instanceof SmokerBlockEntity) {
      return RecipeType.SMOKING;
    } else if (this.parent instanceof BlastFurnaceBlockEntity) {
      return RecipeType.BLASTING;
    } else {
      return RecipeType.SMELTING;
    }
  }

  @Override
  public Optional<Recipe<?>> getSelectedRecipe() {
    return Optional.ofNullable(selectedRecipe);
  }

  @Override
  public void setSavedRecipe(String recipe) {
    this.savedRecipe = recipe;
  }

  @Override
  public void setSelectedRecipe(Recipe<?> recipe) {
    this.selectedRecipe = (AbstractCookingRecipe) recipe;
    World world = this.parent.getWorld();

    if (world instanceof ServerWorld) {

      if (Polymorph.getLoader().isFastFurnaceLoaded()) {
        try {
          FieldUtils.writeField(this.parent, "cachedRecipe", this.selectedRecipe, true);
        } catch (IllegalAccessException e) {
          Polymorph.LOGGER.error("Error accessing cachedRecipe from FastFurnace!");
        } catch (IllegalArgumentException e) {
          Polymorph.LOGGER.debug("Cannot find FastFurnace, skipping field override!");
        }
      }
      ((ServerWorld) world).getPlayers().forEach(player -> {
        if (player.currentScreenHandler instanceof AbstractFurnaceScreenHandler &&
            ((AbstractFurnaceScreenHandlerAccessor) player.currentScreenHandler)
                .getInventory() == this.parent) {
          Polymorph.getLoader().getPacketVendor()
              .highlightRecipe(recipe.getId().toString(), player);
        }
      });
    }
  }

  @Override
  public BlockEntity getParent() {
    return this.parent;
  }

  @Override
  public void readFromNbt(NbtCompound compoundTag) {

    if (compoundTag.contains("SelectedRecipe")) {
      this.setSavedRecipe(compoundTag.getString("SelectedRecipe"));
    }
  }

  @Override
  public void writeToNbt(NbtCompound compoundTag) {

    if (this.selectedRecipe != null) {
      compoundTag.putString("SelectedRecipe", this.selectedRecipe.getId().toString());
    }
  }
}
