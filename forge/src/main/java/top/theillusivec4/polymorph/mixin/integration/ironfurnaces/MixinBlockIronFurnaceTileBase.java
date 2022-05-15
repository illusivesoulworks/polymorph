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

import ironfurnaces.tileentity.TileEntityInventory;
import ironfurnaces.tileentity.furnaces.BlockIronFurnaceTileBase;
import ironfurnaces.util.LRUCache;
import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.AbstractCookingRecipe;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import top.theillusivec4.polymorph.common.integration.ironfurnaces.IronFurnacesModule;

@SuppressWarnings("unused")
@Mixin(BlockIronFurnaceTileBase.class)
public abstract class MixinBlockIronFurnaceTileBase extends TileEntityInventory {

  public MixinBlockIronFurnaceTileBase(BlockEntityType<?> tileEntityTypeIn, BlockPos pos,
                                       BlockState state, int sizeInventory) {
    super(tileEntityTypeIn, pos, state, sizeInventory);
  }

  @Inject(
      at = @At("RETURN"),
      method = "grabRecipe()Ljava/util/Optional;",
      remap = false
  )
  private void polymorph$grabRecipe(CallbackInfoReturnable<Optional<AbstractCookingRecipe>> pCir) {
    IronFurnacesModule.grabRecipe(this.getItem(0), this.getCache());
  }

  @Shadow(remap = false)
  abstract LRUCache<Item, Optional<AbstractCookingRecipe>> getCache();
}
