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

package top.theillusivec4.polymorph.mixin.integration.ironfurnaces;

import ironfurnaces.tileentity.BlockIronFurnaceTileBase;
import ironfurnaces.tileentity.LRUCache;
import java.util.Optional;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.AbstractCookingRecipe;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@SuppressWarnings("unused")
@Mixin(BlockIronFurnaceTileBase.class)
public abstract class MixinBlockIronFurnaceTileBase {

  @Inject(
      at = @At("RETURN"),
      method = "grabRecipe(Lnet/minecraft/item/ItemStack;)Ljava/util/Optional;",
      remap = false
  )
  private void polymorph$grabRecipe(ItemStack pStack,
                                    CallbackInfoReturnable<Optional<AbstractCookingRecipe>> pCir) {

    if (!pStack.isEmpty()) {
      this.getCache().remove(pStack.getItem());
    }
  }

  @Shadow(remap = false)
  abstract LRUCache<Item, Optional<AbstractCookingRecipe>> getCache();
}
