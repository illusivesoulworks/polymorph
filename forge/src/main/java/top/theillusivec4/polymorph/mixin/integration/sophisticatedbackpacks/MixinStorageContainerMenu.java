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

package top.theillusivec4.polymorph.mixin.integration.sophisticatedbackpacks;

import java.util.Map;
import net.minecraft.world.entity.player.Player;
import net.p3pp3rf1y.sophisticatedcore.common.gui.StorageContainerMenuBase;
import net.p3pp3rf1y.sophisticatedcore.common.gui.UpgradeContainerBase;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import top.theillusivec4.polymorph.common.integration.sophisticatedbackpacks.SophisticatedBackpacksModule;

@SuppressWarnings("unused")
@Mixin(StorageContainerMenuBase.class)
public class MixinStorageContainerMenu {

  @Shadow(remap = false)
  @Final
  Player player;

  @Shadow(remap = false)
  @Final
  private Map<Integer, UpgradeContainerBase<?, ?>> upgradeContainers;

  @Inject(
      at = @At("RETURN"),
      method = "setOpenTabId(I)V",
      remap = false
  )
  private void polymorph$setOpenTabId(int id, CallbackInfo ci) {
    SophisticatedBackpacksModule.onOpenTab(id, this.player, this.upgradeContainers);
  }
}
