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

package top.theillusivec4.polymorph.mixin.integration.cyclic;

import com.lothrazar.cyclic.block.crafter.TileCrafter;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(TileCrafter.class)
public interface AccessorTileCrafter {

  @Accessor(remap = false)
  LazyOptional<IItemHandler> getGridCap();

  @Accessor(remap = false)
  LazyOptional<IItemHandler> getPreview();

  @Accessor(remap = false)
  void setLastValidRecipe(Recipe<?> pLastValidRecipe);

  @Accessor(remap = false)
  void setRecipeOutput(ItemStack pRecipeOutput);

  @Invoker(remap = false)
  void callSetPreviewSlot(IItemHandler pPreviewHandler, ItemStack pStack);
}
