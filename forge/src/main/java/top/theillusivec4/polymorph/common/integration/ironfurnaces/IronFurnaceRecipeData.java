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

package top.theillusivec4.polymorph.common.integration.ironfurnaces;

import ironfurnaces.tileentity.BlockIronFurnaceTileBase;
import java.util.Optional;
import javax.annotation.Nonnull;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.AbstractCookingRecipe;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.NonNullList;
import top.theillusivec4.polymorph.common.capability.AbstractHighlightedRecipeData;
import top.theillusivec4.polymorph.mixin.integration.ironfurnaces.AccessorBlockIronFurnaceTileBase;

public class IronFurnaceRecipeData extends AbstractHighlightedRecipeData<BlockIronFurnaceTileBase> {

  public IronFurnaceRecipeData(BlockIronFurnaceTileBase pOwner) {
    super(pOwner);
  }

  @Override
  public void selectRecipe(@Nonnull IRecipe<?> pRecipe) {
    super.selectRecipe(pRecipe);
    ItemStack input = this.getInput().get(0);

    if (!input.isEmpty() && pRecipe instanceof AbstractCookingRecipe) {
      ((AccessorBlockIronFurnaceTileBase) this.getOwner()).callGetCache()
          .put(input.getItem(), Optional.of((AbstractCookingRecipe) pRecipe));
    }
  }

  @Override
  protected NonNullList<ItemStack> getInput() {
    return NonNullList.from(ItemStack.EMPTY, this.getOwner().getStackInSlot(0));
  }

  @Override
  public boolean isEmpty() {
    return this.getInput().get(0).isEmpty();
  }
}
