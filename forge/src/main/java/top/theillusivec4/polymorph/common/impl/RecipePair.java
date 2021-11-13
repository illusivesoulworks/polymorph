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

package top.theillusivec4.polymorph.common.impl;

import javax.annotation.Nonnull;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import top.theillusivec4.polymorph.api.common.base.IRecipePair;

public class RecipePair implements IRecipePair {

  private final ItemStack output;
  private final ResourceLocation resourceLocation;

  public RecipePair(ResourceLocation pResourceLocation, ItemStack pOutput) {
    this.resourceLocation = pResourceLocation;
    this.output = pOutput;
  }

  @Override
  public ItemStack getOutput() {
    return output;
  }

  @Override
  public ResourceLocation getResourceLocation() {
    return resourceLocation;
  }

  @Override
  public int compareTo(@Nonnull IRecipePair pOther) {
    ItemStack output1 = this.getOutput();
    ItemStack output2 = pOther.getOutput();

    if (ItemStack.areItemStacksEqual(output1, output2)) {
      return 0;
    } else {
      int compare = output1.getTranslationKey().compareTo(output2.getTranslationKey());

      if (compare == 0) {
        return output1.getCount() - output2.getCount();
      } else {
        return compare;
      }
    }
  }
}
