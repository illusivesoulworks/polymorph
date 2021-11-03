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
import com.refinedmods.refinedstorage.container.GridContainer;
import com.refinedmods.refinedstorage.tile.grid.GridTile;
import java.util.Optional;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.ICraftingRecipe;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.item.crafting.RecipeManager;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.event.server.FMLServerStartedEvent;
import net.minecraftforge.fml.event.server.FMLServerStoppedEvent;
import top.theillusivec4.polymorph.api.PolymorphApi;
import top.theillusivec4.polymorph.api.client.base.IPolymorphClient;
import top.theillusivec4.polymorph.api.common.base.IPolymorphCommon;
import top.theillusivec4.polymorph.common.crafting.RecipeSelection;
import top.theillusivec4.polymorph.common.integration.AbstractCompatibilityModule;
import top.theillusivec4.polymorph.mixin.integration.refinedstorage.AccessorGrid;

public class RefinedStorageModule extends AbstractCompatibilityModule {

  public static boolean loaded = false;

  @Override
  public void clientSetup() {
    IPolymorphClient clientApi = PolymorphApi.client();
    clientApi.registerWidget(pContainerScreen -> {
      if (pContainerScreen.getContainer() instanceof GridContainer) {
        GridContainer container = (GridContainer) pContainerScreen.getContainer();

        if (container.getTile() instanceof GridTile) {
          return clientApi.findCraftingResultSlot(pContainerScreen).map(
              slot -> new GridTileRecipesWidget(pContainerScreen, slot)).orElse(null);
        }
      }
      return null;
    });
  }

  @Override
  public void setup() {
    IPolymorphCommon commonApi = PolymorphApi.common();
    commonApi.registerTileEntity2RecipeData(pTileEntity -> {
      if (pTileEntity instanceof GridTile) {
        return new GridTileRecipeData((GridTile) pTileEntity);
      }
      return null;
    });
    commonApi.registerContainer2TileEntity(pContainer -> {
      if (pContainer instanceof GridContainer) {
        IGrid grid = ((GridContainer) pContainer).getGrid();

        if (grid instanceof GridNetworkNode) {
          GridNetworkNode gridNetworkNode = (GridNetworkNode) grid;
          return gridNetworkNode.getWorld().getTileEntity(gridNetworkNode.getPos());
        }
      }
      return null;
    });
    MinecraftForge.EVENT_BUS.addListener(this::serverStarted);
    MinecraftForge.EVENT_BUS.addListener(this::serverStopped);
  }

  private void serverStarted(final FMLServerStartedEvent evt) {
    loaded = true;
  }

  private void serverStopped(final FMLServerStoppedEvent evt) {
    loaded = false;
  }

  @Override
  public boolean selectRecipe(TileEntity tileEntity, IRecipe<?> recipe) {

    if (recipe instanceof ICraftingRecipe && tileEntity instanceof GridTile) {
      IGrid grid = ((GridTile) tileEntity).getNode();

      if (grid instanceof GridNetworkNode) {
        ((AccessorGrid) grid).setCurrentRecipe((ICraftingRecipe) recipe);
        grid.onCraftingMatrixChanged();
        return true;
      }
    }
    return false;
  }

  public static <C extends IInventory, T extends IRecipe<C>> Optional<T> getRecipe(
      IRecipeType<T> type, C inventory, World world, BlockPos pos) {

    if (!world.isRemote() && loaded) {
      TileEntity te = world.getTileEntity(pos);

      if (te != null) {
        return RecipeSelection.getTileEntityRecipe(type, inventory, world, te);
      }
    }
    return world.getRecipeManager().getRecipes(type, inventory, world).stream().findFirst();
  }

  public static void appendPattern(boolean exactPattern, ItemStack stack, BlockPos pos,
                                   World world) {

    if (exactPattern) {
      CompoundNBT tag = stack.getTag();

      if (tag == null) {
        stack.setTag(new CompoundNBT());
      }

      if (!world.isRemote() && loaded) {
        TileEntity te = world.getTileEntity(pos);

        if (te instanceof GridTile) {
          Optional<ICraftingRecipe> recipe =
              RecipeSelection.getTileEntityRecipe(IRecipeType.CRAFTING,
                  ((GridTile) te).getNode().getCraftingMatrix(), world, te);
          recipe.ifPresent(
              rec -> stack.getTag().putString("PolymorphRecipe", rec.getId().toString()));
        }
      }
    }
  }

  @SuppressWarnings("unchecked")
  public static <T extends IRecipe<C>, C extends IInventory> Optional<T> getPatternRecipe(
      ItemStack stack, IRecipeType<T> type, C inventory, World world) {
    CompoundNBT tag = stack.getTag();

    if (tag != null) {
      String id = tag.getString("PolymorphRecipe");
      Optional<T> opt = (Optional<T>) world.getRecipeManager().getRecipe(new ResourceLocation(id));

      if (opt.isPresent()) {
        return opt;
      }
    }
    return world.getRecipeManager().getRecipe(type, inventory, world);
  }

  public static <T extends IRecipe<C>, C extends IInventory> Optional<T> getWirelessRecipe(
      RecipeManager unused, IRecipeType<T> type, C inventory, World world,
      ICraftingGridListener listener) {

    if (listener instanceof GridContainer) {
      GridContainer container = (GridContainer) listener;
      return RecipeSelection.getPlayerRecipe(type, inventory, world, container.getPlayer());
    }
    return world.getRecipeManager().getRecipe(type, inventory, world);
  }
}
