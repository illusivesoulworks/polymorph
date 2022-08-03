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

package com.illusivesoulworks.polymorph.mixin.core;

import com.illusivesoulworks.polymorph.api.PolymorphApi;
import com.illusivesoulworks.polymorph.api.client.base.IPolymorphClient;
import com.illusivesoulworks.polymorph.api.common.base.IPolymorphCommon;
import com.illusivesoulworks.polymorph.client.impl.PolymorphClient;
import com.illusivesoulworks.polymorph.common.impl.PolymorphCommon;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@SuppressWarnings("unused")
@Mixin(PolymorphApi.class)
public class MixinPolymorphApi {

  @Inject(
      at = @At("HEAD"),
      method = "common",
      remap = false,
      cancellable = true
  )
  private static void polymorph$common(CallbackInfoReturnable<IPolymorphCommon> cir) {
    cir.setReturnValue(PolymorphCommon.get());
  }

  @Inject(
      at = @At("HEAD"),
      method = "client",
      remap = false,
      cancellable = true
  )
  private static void polymorph$client(CallbackInfoReturnable<IPolymorphClient> cir) {
    cir.setReturnValue(PolymorphClient.get());
  }
}
