/*
 * Copyright (c) 2020 C4
 *
 * This file is part of Polymorph, a mod made for Minecraft.
 *
 * Polymorph is free software: you can redistribute it and/or modify it under the terms of the GNU
 * Lesser General Public License as published by the Free Software Foundation, either version 3 of
 * the License, or any later version.
 *
 * Polymorph is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without
 * even the implied warranty of MERCHANTABILITY or FITNESS FOR PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with Polymorph.
 * If not, see <https://www.gnu.org/licenses/>.
 */

package top.theillusivec4.polymorph.loader.common;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.inventory.Inventory;
import net.minecraft.recipe.AbstractCookingRecipe;
import net.minecraft.recipe.RecipeManager;
import net.minecraft.recipe.RecipeType;
import net.minecraft.screen.BlastFurnaceScreenHandler;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.SmokerScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.world.World;
import top.theillusivec4.polymorph.api.type.FurnaceProvider;
import top.theillusivec4.polymorph.loader.common.integration.CompatibilityModule;
import top.theillusivec4.polymorph.loader.common.integration.fabricfurnaces.FabricFurnacesModule;
import top.theillusivec4.polymorph.loader.common.integration.ironfurnaces.IronFurnacesModule;
import top.theillusivec4.polymorph.loader.network.NetworkHandler;
import top.theillusivec4.polymorph.loader.server.PolymorphCommands;

public class PolymorphMod implements ModInitializer {

  private static final Map<String, Supplier<CompatibilityModule>> INTEGRATIONS = new HashMap<>();
  private static final List<CompatibilityModule> ACTIVE_INTEGRATIONS = new ArrayList<>();

  public static boolean isFastFurnaceLoaded = false;
  public static boolean isOriginsClassesLoaded = false;

  static {
//    INTEGRATIONS.put("ironfurnaces", IronFurnacesModule::new);
    INTEGRATIONS.put("fabric-furnaces", FabricFurnacesModule::new);
  }

  public PolymorphMod() {
    FabricLoader loader = FabricLoader.getInstance();
    isFastFurnaceLoaded = loader.isModLoaded("fastfurnace");
    isOriginsClassesLoaded = loader.isModLoaded("origins-classes");
    INTEGRATIONS.forEach((modid, supplier) -> {

      if (loader.isModLoaded(modid)) {
        ACTIVE_INTEGRATIONS.add(supplier.get());
      }
    });
  }

  @Override
  public void onInitialize() {
    NetworkHandler.setup();
    CommandRegistrationCallback.EVENT
        .register((commandDispatcher, b) -> PolymorphCommands.register(commandDispatcher));
    ACTIVE_INTEGRATIONS.forEach(CompatibilityModule::setup);
  }
}
