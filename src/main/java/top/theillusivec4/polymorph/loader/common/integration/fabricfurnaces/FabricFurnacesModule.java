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

package top.theillusivec4.polymorph.loader.common.integration.fabricfurnaces;

import draylar.fabricfurnaces.entity.BaseFurnaceEntity;
import java.util.Comparator;
import java.util.Optional;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.recipe.AbstractCookingRecipe;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeType;
import net.minecraft.screen.AbstractFurnaceScreenHandler;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import net.minecraft.world.World;
import org.apache.commons.lang3.reflect.FieldUtils;
import top.theillusivec4.polymorph.api.PolymorphApi;
import top.theillusivec4.polymorph.api.type.PersistentSelector;
import top.theillusivec4.polymorph.core.Polymorph;
import top.theillusivec4.polymorph.loader.common.PolymorphComponentImpl;
import top.theillusivec4.polymorph.loader.common.PolymorphMod;
import top.theillusivec4.polymorph.loader.common.integration.CompatibilityModule;

public class FabricFurnacesModule extends CompatibilityModule {

  @Override
  public void setup() {
    PolymorphApi.getInstance().addEntityProvider(blockEntity -> {
      if (blockEntity instanceof BaseFurnaceEntity) {
        return new FabricFurnaceSelector((BaseFurnaceEntity) blockEntity);
      }
      return null;
    }, screenHandler -> {
      if (screenHandler instanceof AbstractFurnaceScreenHandler) {
        return new PolymorphComponentImpl.SimpleFurnaceProvider(screenHandler);
      }
      return null;
    });
  }

  public static class FabricFurnaceSelector implements PersistentSelector {

    private final BaseFurnaceEntity parent;

    private AbstractCookingRecipe selectedRecipe;
    private ItemStack lastFailedInput = ItemStack.EMPTY;
    private String savedRecipe = "";

    public FabricFurnaceSelector(BaseFurnaceEntity tileEntity) {
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
              .stream(this.getRecipeType().get((Recipe<Inventory>) val, world, parent)))
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
      try {
        return (RecipeType<? extends Recipe<?>>) FieldUtils
            .readField(this.parent, "recipeType", true);
      } catch (IllegalAccessException e) {
        Polymorph.LOGGER.error("Error accessing recipeType of Fabric Furnaces!");
        e.printStackTrace();
      }
      return RecipeType.SMELTING;
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
        ((ServerWorld) world).getPlayers().forEach(player -> {
          if (player.currentScreenHandler instanceof AbstractFurnaceScreenHandler &&
              player.currentScreenHandler.slots.get(0).inventory == this.parent) {
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
    public void readFromNbt(CompoundTag compoundTag) {

      if (compoundTag.contains("SelectedRecipe")) {
        this.setSavedRecipe(compoundTag.getString("SelectedRecipe"));
      }
    }

    @Override
    public void writeToNbt(CompoundTag compoundTag) {

      if (this.selectedRecipe != null) {
        compoundTag.putString("SelectedRecipe", this.selectedRecipe.getId().toString());
      }
    }
  }
}
