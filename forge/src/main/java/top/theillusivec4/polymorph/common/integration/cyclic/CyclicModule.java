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
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import top.theillusivec4.polymorph.api.PolymorphApi;
import top.theillusivec4.polymorph.api.common.base.IPolymorphCommon;
import top.theillusivec4.polymorph.common.crafting.RecipeSelection;
import top.theillusivec4.polymorph.common.integration.AbstractCompatibilityModule;
import top.theillusivec4.polymorph.mixin.integration.cyclic.AccessorContainerCrafter;

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
          containerScreen.getMenu() instanceof ContainerCrafter containerCrafter) {
        return new CrafterRecipesWidget(containerScreen, containerCrafter);
      }
      return null;
    });
  }

  @Override
  public boolean openContainer(AbstractContainerMenu container, ServerPlayer serverPlayerEntity) {

    if (container instanceof CraftingBagContainer) {
      PolymorphApi.common().getRecipeData(serverPlayerEntity)
          .ifPresent(recipeData -> container.slotsChanged(null));
      return true;
    }
    return false;
  }

  @SuppressWarnings("unchecked")
  public static <T extends Recipe<C>, C extends Container> Optional<T> getRecipe(
      ArrayList<ItemStack> stacks, Level worldIn, BlockEntity te) {

    if (worldIn != null && worldIn.getServer() != null) {
      CraftingContainer craftingInventory =
          new CraftingContainer(new FakeContainer(MenuType.CRAFTING, 18291239), 3, 3);

      for (int i = 0; i < 3; i++) {

        for (int j = 0; j < 3; j++) {
          int indexInArray = i + j * 3;
          ItemStack stack = stacks.get(indexInArray);
          craftingInventory.setItem(indexInArray, stack.copy());
        }
      }
      return (Optional<T>) RecipeSelection.getTileEntityRecipe(RecipeType.CRAFTING,
          craftingInventory, worldIn, te);
    } else {
      return Optional.empty();
    }
  }

  private static class FakeContainer extends AbstractContainerMenu {

    protected FakeContainer(MenuType<?> type, int id) {
      super(type, id);
    }

    public boolean stillValid(@Nonnull Player playerIn) {
      return true;
    }
  }
}
