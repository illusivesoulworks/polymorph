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

package top.theillusivec4.polymorph.common.integration;

import com.electronwill.nightconfig.core.ConfigSpec;
import com.electronwill.nightconfig.core.file.FileConfig;
import com.google.common.collect.ImmutableSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.loading.FMLPaths;
import top.theillusivec4.polymorph.common.integration.appliedenergistics2.AppliedEnergisticsModule;
import top.theillusivec4.polymorph.common.integration.craftingstation.CraftingStationModule;
import top.theillusivec4.polymorph.common.integration.cyclic.CyclicModule;
import top.theillusivec4.polymorph.common.integration.fastbench.FastBenchModule;
import top.theillusivec4.polymorph.common.integration.fastfurnace.FastFurnaceModule;
import top.theillusivec4.polymorph.common.integration.ironfurnaces.IronFurnacesModule;
import top.theillusivec4.polymorph.common.integration.prettypipes.PrettyPipesModule;
import top.theillusivec4.polymorph.common.integration.refinedstorage.RefinedStorageModule;
import top.theillusivec4.polymorph.common.integration.refinedstorageaddons.RefinedStorageAddonsModule;
import top.theillusivec4.polymorph.common.integration.sophisticatedbackpacks.SophisticatedBackpacksModule;
import top.theillusivec4.polymorph.common.integration.tconstruct.TinkersConstructModule;
import top.theillusivec4.polymorph.common.integration.toms_storage.TomsStorageModule;

public class PolymorphIntegrations {

  private static final Set<String> CONFIG_ACTIVATED = new HashSet<>();
  private static final Map<String, Supplier<Supplier<AbstractCompatibilityModule>>> INTEGRATIONS =
      new HashMap<>();
  private static final Set<AbstractCompatibilityModule> ACTIVE_INTEGRATIONS = new HashSet<>();

  static {
    INTEGRATIONS.put(Mod.PRETTY_PIPES.getId(), () -> PrettyPipesModule::new);
    INTEGRATIONS.put(Mod.REFINED_STORAGE.getId(), () -> RefinedStorageModule::new);
    INTEGRATIONS.put(Mod.REFINED_STORAGE_ADDONS.getId(), () -> RefinedStorageAddonsModule::new);
    INTEGRATIONS.put(Mod.TINKERS_CONSTRUCT.getId(), () -> TinkersConstructModule::new);
    INTEGRATIONS.put(Mod.FASTWORKBENCH.getId(), () -> FastBenchModule::new);
    INTEGRATIONS.put(Mod.CRAFTING_STATION.getId(), () -> CraftingStationModule::new);
    INTEGRATIONS.put(Mod.FASTFURNACE.getId(), () -> FastFurnaceModule::new);
    INTEGRATIONS.put(Mod.CYCLIC.getId(), () -> CyclicModule::new);
    INTEGRATIONS.put(Mod.TOMS_STORAGE.getId(), () -> TomsStorageModule::new);
    INTEGRATIONS.put(Mod.SOPHISTICATED_BACKPACKS.getId(), () -> SophisticatedBackpacksModule::new);
    INTEGRATIONS.put(Mod.APPLIED_ENERGISTICS_2.getId(), () -> AppliedEnergisticsModule::new);
    INTEGRATIONS.put(Mod.IRON_FURNACES.getId(), () -> IronFurnacesModule::new);
  }

  public static void loadConfig() {
    ConfigSpec spec = new ConfigSpec();

    for (PolymorphIntegrations.Mod mod : PolymorphIntegrations.Mod.values()) {
      spec.define(mod.getId(), true);
    }
    FileConfig config =
        FileConfig.of(FMLPaths.CONFIGDIR.get().resolve("polymorph-integrations.toml"));
    config.load();

    if (!spec.isCorrect(config)) {
      spec.correct(config);
    }

    for (PolymorphIntegrations.Mod mod : PolymorphIntegrations.Mod.values()) {

      if (config.get(mod.getId())) {
        CONFIG_ACTIVATED.add(mod.getId());
      }
    }
    config.save();
    config.close();
  }

  public static void init() {
    ModList modList = ModList.get();
    INTEGRATIONS.forEach((modid, supplier) -> {

      if (CONFIG_ACTIVATED.contains(modid) && modList.isLoaded(modid)) {
        ACTIVE_INTEGRATIONS.add(supplier.get().get());
      }
    });
  }

  public static void setup() {

    for (AbstractCompatibilityModule integration : get()) {
      integration.setup();
    }
  }

  public static void clientSetup() {

    for (AbstractCompatibilityModule integration : get()) {
      integration.clientSetup();
    }
  }

  public static Set<AbstractCompatibilityModule> get() {
    return ImmutableSet.copyOf(ACTIVE_INTEGRATIONS);
  }

  public static Set<String> getConfigActivated() {
    return ImmutableSet.copyOf(CONFIG_ACTIVATED);
  }

  public enum Mod {
    JEI("jei"),
    CRAFTINGCRAFT("craftingcraft"),
    CRAFTING_STATION("craftingstation"),
    PRETTY_PIPES("prettypipes"),
    TOMS_STORAGE("toms_storage"),
    FASTWORKBENCH("fastbench"),
    SIMPLE_STORAGE_NETWORK("storagenetwork"),
    REFINED_STORAGE("refinedstorage"),
    REFINED_STORAGE_ADDONS("refinedstorageaddons"),
    TINKERS_CONSTRUCT("tconstruct"),
    CYCLIC("cyclic"),
    SOPHISTICATED_BACKPACKS("sophisticatedbackpacks"),
    APPLIED_ENERGISTICS_2("appliedenergistics2"),
    IRON_FURNACES("ironfurnaces"),
    FASTFURNACE("fastfurnace");

    private final String id;

    Mod(String pId) {
      this.id = pId;
    }

    public String getId() {
      return this.id;
    }
  }
}
