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

package top.theillusivec4.polymorph.common;

import net.minecraft.world.Container;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.level.block.entity.AbstractFurnaceBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import top.theillusivec4.polymorph.api.PolymorphApi;
import top.theillusivec4.polymorph.api.common.base.IPolymorphCommon;
import top.theillusivec4.polymorph.client.ClientEventsListener;
import top.theillusivec4.polymorph.client.PolymorphClientMod;
import top.theillusivec4.polymorph.common.capability.FurnaceRecipeData;
import top.theillusivec4.polymorph.common.integration.PolymorphIntegrations;
import top.theillusivec4.polymorph.common.network.PolymorphNetwork;
import top.theillusivec4.polymorph.server.PolymorphCommands;

@Mod(PolymorphApi.MOD_ID)
public class PolymorphMod {

  public static final Logger LOGGER = LogManager.getLogger();

  public PolymorphMod() {
    IEventBus eventBus = FMLJavaModLoadingContext.get().getModEventBus();
    eventBus.addListener(this::setup);
    eventBus.addListener(this::clientSetup);
    MinecraftForge.EVENT_BUS.addListener(this::registerCommand);
    PolymorphIntegrations.init();
  }

  private void setup(final FMLCommonSetupEvent pEvent) {
    PolymorphNetwork.setup();
    MinecraftForge.EVENT_BUS.register(new CommonEventsListener());
    IPolymorphCommon commonApi = PolymorphApi.common();
    commonApi.registerTileEntity2RecipeData(tileEntity -> {
      if (tileEntity instanceof AbstractFurnaceBlockEntity) {
        return new FurnaceRecipeData((AbstractFurnaceBlockEntity) tileEntity);
      }
      return null;
    });
    commonApi.registerContainer2TileEntity(container -> {
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

  private void clientSetup(final FMLClientSetupEvent pEvent) {
    PolymorphClientMod.setup();
    MinecraftForge.EVENT_BUS.register(new ClientEventsListener());
    PolymorphIntegrations.clientSetup();
  }

  private void registerCommand(final RegisterCommandsEvent pEvent) {
    PolymorphCommands.register(pEvent.getDispatcher());
  }
}
