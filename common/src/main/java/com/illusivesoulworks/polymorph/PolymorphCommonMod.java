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

package com.illusivesoulworks.polymorph;

import com.illusivesoulworks.polymorph.api.PolymorphApi;
import com.illusivesoulworks.polymorph.api.common.base.IPolymorphCommon;
import com.illusivesoulworks.polymorph.client.impl.PolymorphClient;
import com.illusivesoulworks.polymorph.common.capability.FurnaceRecipeData;
import com.illusivesoulworks.polymorph.common.integration.PolymorphIntegrations;
import net.minecraft.world.Container;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.level.block.entity.AbstractFurnaceBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntity;

public class PolymorphCommonMod {

  public static void init() {
    PolymorphIntegrations.init();
  }

  public static void setup() {
    IPolymorphCommon commonApi = PolymorphApi.common();
    commonApi.registerBlockEntity2RecipeData(blockEntity -> {
      if (blockEntity instanceof AbstractFurnaceBlockEntity) {
        return new FurnaceRecipeData((AbstractFurnaceBlockEntity) blockEntity);
      }
      return null;
    });
    commonApi.registerContainer2BlockEntity(container -> {
      for (Slot inventorySlot : container.slots) {
        Container inventory = inventorySlot.container;

        if (inventory instanceof BlockEntity) {
          return (BlockEntity) inventory;
        }
      }
      return null;
    });
    PolymorphIntegrations.setup();
  }

  public static void clientSetup() {
    PolymorphClient.setup();
    PolymorphIntegrations.clientSetup();
  }
}