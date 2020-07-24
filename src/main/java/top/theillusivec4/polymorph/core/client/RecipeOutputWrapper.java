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

package top.theillusivec4.polymorph.core.client;

import java.util.Objects;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;

public class RecipeOutputWrapper {

  private final Item item;
  private final int count;
  private final CompoundTag tag;

  public RecipeOutputWrapper(ItemStack stack) {
    this.item = stack.getItem();
    this.count = stack.getCount();
    this.tag = stack.getTag();
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    RecipeOutputWrapper that = (RecipeOutputWrapper) o;
    return count == that.count && item.equals(that.item) && Objects.equals(tag, that.tag);
  }

  @Override
  public int hashCode() {
    return Objects.hash(item, count, tag);
  }
}
