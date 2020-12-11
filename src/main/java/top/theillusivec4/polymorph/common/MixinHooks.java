/*
 * Copyright (c) 2020 C4
 *
 * This file is part of Polymorph, a mod made for Minecraft.
 *
 * Polymorph is free software: you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published
 * by the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Polymorph is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with Polymorph.  If not, see <https://www.gnu.org/licenses/>.
 */

package top.theillusivec4.polymorph.common;

import net.minecraft.inventory.IInventory;
import net.minecraft.item.crafting.AbstractCookingRecipe;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.tileentity.AbstractFurnaceTileEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import top.theillusivec4.polymorph.Polymorph;
import top.theillusivec4.polymorph.api.PolymorphCapability;
import top.theillusivec4.polymorph.client.selector.CraftingRecipeSelector;

public class MixinHooks {

  public static void sendCraftingUpdate() {
    DistExecutor.safeRunWhenOn(Dist.CLIENT, () -> CraftingRecipeSelector::update);
  }

  public static IRecipe<?> getSelectedRecipe(IInventory inventoryIn) {
    if (inventoryIn instanceof AbstractFurnaceTileEntity) {
      AbstractFurnaceTileEntity te = (AbstractFurnaceTileEntity) inventoryIn;
      te.getCapability(PolymorphCapability.PERSISTENT_SELECTOR).ifPresent(selector -> {
        AbstractCookingRecipe recipe = selector.getSelectedRecipe();
      });
    }
    return null;
  }
}
