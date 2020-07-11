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

package top.theillusivec4.polymorph.common.integrations.craftingstation;

import com.tfar.craftingstation.CraftingStationContainer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.inventory.container.Container;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent.ClientTickEvent;
import net.minecraftforge.event.TickEvent.Phase;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import top.theillusivec4.polymorph.api.PolymorphApi;
import top.theillusivec4.polymorph.api.PolymorphApi.IProvider;
import top.theillusivec4.polymorph.client.RecipeConflictManager;

public class CraftingStationModule {

  public static void setup() {
    PolymorphApi.addProvider(CraftingStationContainer.class, CraftingStationProvider::new);
  }

  public static class CraftingStationProvider implements IProvider {

    private final CraftingStationContainer craftingStationContainer;

    public CraftingStationProvider(CraftingStationContainer craftingStationContainer) {
      this.craftingStationContainer = craftingStationContainer;
    }

    @Override
    public Container getContainer() {
      return this.craftingStationContainer;
    }

    @Override
    public int getXOffset() {
      return 36;
    }

    @Override
    public int getYOffset() {
      return -72;
    }
  }
}
