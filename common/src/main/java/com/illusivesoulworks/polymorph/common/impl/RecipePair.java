/*
 * Copyright (C) 2020-2022 Illusive Soulworks
 *
 * Polymorph is free software: you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published
 * by the Free Software Foundation, either version 3 of the License, or
 * any later version.
 *
 * Polymorph is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with Polymorph.  If not, see <https://www.gnu.org/licenses/>.
 */

package com.illusivesoulworks.polymorph.common.impl;

import com.illusivesoulworks.polymorph.api.common.base.IRecipePair;
import javax.annotation.Nonnull;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

public record RecipePair(ResourceLocation resourceLocation,
                         ItemStack output) implements IRecipePair {

  @Override
  public ItemStack getOutput() {
    return output;
  }

  @Override
  public ResourceLocation getResourceLocation() {
    return resourceLocation;
  }

  @Override
  public int compareTo(@Nonnull IRecipePair other) {
    ItemStack output1 = this.getOutput();
    ItemStack output2 = other.getOutput();

    // Sort vanilla recipes after modded recipes if they appear in the same list
    if (this.getResourceLocation().getNamespace().equals("minecraft") &&
        !other.getResourceLocation().getNamespace().equals("minecraft")) {
      return 1;
    } else if (!this.getResourceLocation().getNamespace().equals("minecraft") &&
        other.getResourceLocation().getNamespace().equals("minecraft")) {
      return -1;
    }
    int compare = output1.getDescriptionId().compareTo(output2.getDescriptionId());

    if (compare == 0) {
      int diff = output1.getCount() - output2.getCount();

      if (diff == 0) {
        String tag1 = output1.getTag() != null ? output1.getTag().getAsString() : "";
        String tag2 = output2.getTag() != null ? output2.getTag().getAsString() : "";
        return tag1.compareTo(tag2);
      } else {
        return diff;
      }
    } else {
      return compare;
    }
  }
}
