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

package top.theillusivec4.polymorph.api.type;

import java.util.Optional;
import javax.annotation.Nonnull;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public interface IPersistentSelector {

  Optional<IRecipe<?>> fetchRecipe(World world);

  IRecipeType<? extends IRecipe<?>> getRecipeType();

  @Nonnull
  Optional<? extends IRecipe<?>> getSelectedRecipe();

  void setSavedRecipe(String recipe);

  void setSelectedRecipe(IRecipe<?> recipe);

  TileEntity getParent();
}
