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

package top.theillusivec4.polymorph.common.integration.refinedstorage;

import com.refinedmods.refinedstorage.api.network.grid.ICraftingGridListener;
import com.refinedmods.refinedstorage.api.network.grid.IGrid;
import com.refinedmods.refinedstorage.apiimpl.network.node.GridNetworkNode;
import com.refinedmods.refinedstorage.blockentity.grid.GridBlockEntity;
import com.refinedmods.refinedstorage.container.GridContainerMenu;
import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import top.theillusivec4.polymorph.api.PolymorphApi;
import top.theillusivec4.polymorph.api.client.base.IPolymorphClient;
import top.theillusivec4.polymorph.api.common.base.IPolymorphCommon;
import top.theillusivec4.polymorph.common.crafting.RecipeSelection;
import top.theillusivec4.polymorph.common.integration.AbstractCompatibilityModule;
import top.theillusivec4.polymorph.mixin.integration.refinedstorage.AccessorGrid;

public class RefinedStorageModule extends AbstractCompatibilityModule {

  @Override
  public void clientSetup() {
    IPolymorphClient clientApi = PolymorphApi.client();
    clientApi.registerWidget(pContainerScreen -> {
      if (pContainerScreen.getMenu() instanceof GridContainerMenu container) {

        if (container.getBlockEntity() instanceof GridBlockEntity) {
          return clientApi.findCraftingResultSlot(pContainerScreen).map(
              slot -> new GridBlockEntityRecipesWidget(pContainerScreen, slot)).orElse(null);
        }
      }
      return null;
    });
  }

  @Override
  public void setup() {
    IPolymorphCommon commonApi = PolymorphApi.common();
    commonApi.registerTileEntity2RecipeData(pTileEntity -> {
      if (pTileEntity instanceof GridBlockEntity) {
        return new GridBlockEntityRecipeData((GridBlockEntity) pTileEntity);
      }
      return null;
    });
    commonApi.registerContainer2TileEntity(pContainer -> {
      if (pContainer instanceof GridContainerMenu) {
        IGrid grid = ((GridContainerMenu) pContainer).getGrid();

        if (grid instanceof GridNetworkNode gridNetworkNode) {
          return gridNetworkNode.getLevel().getBlockEntity(gridNetworkNode.getPos());
        }
      }
      return null;
    });
  }

  @Override
  public boolean selectRecipe(BlockEntity blockEntity, Recipe<?> recipe) {

    if (recipe instanceof CraftingRecipe && blockEntity instanceof GridBlockEntity) {
      IGrid grid = ((GridBlockEntity) blockEntity).getNode();

      if (grid instanceof GridNetworkNode) {
        ((AccessorGrid) grid).setCurrentRecipe((CraftingRecipe) recipe);
        grid.onCraftingMatrixChanged();
        return true;
      }
    }
    return false;
  }

  public static <C extends Container, T extends Recipe<C>> Optional<T> getRecipe(
      RecipeType<T> type, C inventory, Level world, BlockPos pos, int ticks) {

    if (pos != null && ticks > 1) {
      BlockEntity te = world.getBlockEntity(pos);

      if (te != null) {
        return RecipeSelection.getTileEntityRecipe(type, inventory, world, te);
      }
    }
    return world.getRecipeManager().getRecipeFor(type, inventory, world);
  }

  public static void appendPattern(boolean exactPattern, ItemStack stack, Level world, BlockPos pos,
                                   int ticks) {

    if (exactPattern) {
      CompoundTag tag = stack.getTag();

      if (tag == null) {
        stack.setTag(new CompoundTag());
      }

      if (world != null && pos != null && ticks > 1) {
        BlockEntity te = world.getBlockEntity(pos);

        if (te instanceof GridBlockEntity) {
          Optional<CraftingRecipe> recipe =
              RecipeSelection.getTileEntityRecipe(RecipeType.CRAFTING,
                  ((GridBlockEntity) te).getNode().getCraftingMatrix(), world, te);
          recipe.ifPresent(
              rec -> stack.getTag().putString("PolymorphRecipe", rec.getId().toString()));
        }
      }
    }
  }

  @SuppressWarnings("unchecked")
  public static <T extends Recipe<C>, C extends Container> Optional<T> getPatternRecipe(
      ItemStack stack, RecipeType<T> type, C inventory, Level world) {
    CompoundTag tag = stack.getTag();

    if (tag != null) {
      String id = tag.getString("PolymorphRecipe");
      Optional<T> opt = (Optional<T>) world.getRecipeManager().byKey(new ResourceLocation(id));

      if (opt.isPresent()) {
        return opt;
      }
    }
    return world.getRecipeManager().getRecipeFor(type, inventory, world);
  }

  public static <T extends Recipe<C>, C extends Container> Optional<T> getWirelessRecipe(
      RecipeManager unused, RecipeType<T> type, C inventory, Level world,
      ICraftingGridListener listener) {

    if (listener instanceof GridContainerMenu container) {
      return RecipeSelection.getPlayerRecipe(container, type, inventory, world,
          container.getPlayer());
    }
    return world.getRecipeManager().getRecipeFor(type, inventory, world);
  }
}
