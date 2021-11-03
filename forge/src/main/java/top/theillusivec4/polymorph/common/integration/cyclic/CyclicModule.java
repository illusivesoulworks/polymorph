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

package top.theillusivec4.polymorph.common.integration.cyclic;

import com.lothrazar.cyclic.block.crafter.ContainerCrafter;
import com.lothrazar.cyclic.block.crafter.ScreenCrafter;
import com.lothrazar.cyclic.block.crafter.TileCrafter;
import com.lothrazar.cyclic.item.crafting.CraftingBagContainer;
import java.util.ArrayList;
import java.util.Optional;
import javax.annotation.Nonnull;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;
import top.theillusivec4.polymorph.api.PolymorphApi;
import top.theillusivec4.polymorph.api.common.base.IPolymorphCommon;
import top.theillusivec4.polymorph.common.crafting.RecipeSelection;
import top.theillusivec4.polymorph.common.integration.AbstractCompatibilityModule;
import top.theillusivec4.polymorph.mixin.integration.cyclic.AccessorContainerCrafter;
import top.theillusivec4.polymorph.mixin.integration.cyclic.AccessorTileCrafter;

public class CyclicModule extends AbstractCompatibilityModule {

  @Override
  public void setup() {
    IPolymorphCommon commonApi = PolymorphApi.common();
    commonApi.registerTileEntity2RecipeData(tileEntity -> {
      if (tileEntity instanceof TileCrafter) {
        return new TileCrafterRecipeData((TileCrafter) tileEntity);
      }
      return null;
    });
    commonApi.registerContainer2TileEntity(container -> {
      if (container instanceof ContainerCrafter) {
        return ((AccessorContainerCrafter) container).getTile();
      }
      return null;
    });
  }

  @Override
  public void clientSetup() {
    PolymorphApi.client().registerWidget(containerScreen -> {
      if (containerScreen instanceof ScreenCrafter &&
          containerScreen.getMenu() instanceof ContainerCrafter) {
        ContainerCrafter containerCrafter = (ContainerCrafter) containerScreen.getMenu();
        return new CrafterRecipesWidget(containerScreen, containerCrafter);
      }
      return null;
    });
  }

  @Override
  public boolean openContainer(Container container, ServerPlayerEntity serverPlayerEntity) {

    if (container instanceof CraftingBagContainer) {
      PolymorphApi.common().getRecipeData(serverPlayerEntity)
          .ifPresent(recipeData -> container.slotsChanged(null));
      return true;
    }
    return false;
  }

  @SuppressWarnings("ConstantConditions")
  @Override
  public boolean selectRecipe(TileEntity tileEntity, IRecipe<?> recipe) {

    if (tileEntity instanceof TileCrafter) {
      AccessorTileCrafter tileCrafter = (AccessorTileCrafter) tileEntity;
      tileCrafter.setLastValidRecipe(recipe);
      tileCrafter.setRecipeOutput(recipe.getResultItem());
      LazyOptional<IItemHandler> preview = tileCrafter.getPreview();

      if (preview != null) {
        tileCrafter.callSetPreviewSlot(preview.orElse(null), recipe.getResultItem());
      }
      return true;
    }
    return false;
  }

  @SuppressWarnings("unchecked")
  public static <T extends IRecipe<C>, C extends IInventory> Optional<T> getRecipe(
      ArrayList<ItemStack> stacks, World worldIn, TileEntity te) {

    if (worldIn != null && worldIn.getServer() != null) {
      CraftingInventory craftingInventory =
          new CraftingInventory(new FakeContainer(ContainerType.CRAFTING, 18291239), 3, 3);

      for (int i = 0; i < 3; i++) {

        for (int j = 0; j < 3; j++) {
          int indexInArray = i + j * 3;
          ItemStack stack = stacks.get(indexInArray);
          craftingInventory.setItem(indexInArray, stack.copy());
        }
      }
      return (Optional<T>) RecipeSelection.getTileEntityRecipe(IRecipeType.CRAFTING,
          craftingInventory, worldIn, te);
    } else {
      return Optional.empty();
    }
  }

  private static class FakeContainer extends Container {

    protected FakeContainer(ContainerType<?> type, int id) {
      super(type, id);
    }

    public boolean stillValid(@Nonnull PlayerEntity playerIn) {
      return true;
    }
  }
}
