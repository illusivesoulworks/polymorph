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

package com.illusivesoulworks.polymorph.mixin;

import com.illusivesoulworks.polymorph.common.PolymorphCommonEvents;
import com.mojang.authlib.GameProfile;
import java.util.OptionalInt;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.ProfilePublicKey;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ServerPlayer.class)
public abstract class MixinServerPlayer extends Player {

  public MixinServerPlayer(Level level, BlockPos blockPos, float f, GameProfile gameProfile,
                           @Nullable ProfilePublicKey profilePublicKey) {
    super(level, blockPos, f, gameProfile, profilePublicKey);
  }

  @Inject(
      at = @At("RETURN"),
      method = "openMenu(Lnet/minecraft/world/MenuProvider;)Ljava/util/OptionalInt;")
  private void polymorph$openHandledScreen(CallbackInfoReturnable<OptionalInt> cir) {
    cir.getReturnValue().ifPresent(
        value -> PolymorphCommonEvents.openContainer((ServerPlayer) (Object) this,
            this.containerMenu));
  }
}
