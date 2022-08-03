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

package com.illusivesoulworks.polymorph.common.integration;

import com.google.common.collect.ImmutableSet;
import com.illusivesoulworks.polymorph.platform.Services;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;

public class PolymorphIntegrations {

  private static final Set<String> CONFIG_ACTIVATED = new HashSet<>();
  private static final Map<String, Supplier<Supplier<AbstractCompatibilityModule>>> INTEGRATIONS =
      new HashMap<>();
  private static final Set<AbstractCompatibilityModule> ACTIVE_INTEGRATIONS = new HashSet<>();

//  public static void loadConfig() {
//    ConfigSpec spec = new ConfigSpec();
//
//    for (Mod mod : Mod.values()) {
//      spec.define(mod.getId(), true);
//    }
//    FileConfig config =
//        FileConfig.of(FMLPaths.CONFIGDIR.get().resolve("polymorph-integrations.toml"));
//    config.load();
//
//    if (!spec.isCorrect(config)) {
//      spec.correct(config);
//    }
//
//    for (Mod mod : Mod.values()) {
//
//      if (config.get(mod.getId())) {
//        CONFIG_ACTIVATED.add(mod.getId());
//      }
//    }
//    config.save();
//    config.close();
//  }

  public static void init() {
    INTEGRATIONS.forEach((modid, supplier) -> {

      if (CONFIG_ACTIVATED.contains(modid) && Services.PLATFORM.isModLoaded(modid)) {
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

//  public static Set<String> getConfigActivated() {
//    return ImmutableSet.copyOf(CONFIG_ACTIVATED);
//  }

//  public enum Mod {
//    JEI("jei"),
//    CRAFTINGCRAFT("craftingcraft"),
//    TOMS_STORAGE("toms_storage"),
//    SIMPLE_STORAGE_NETWORK("storagenetwork"),
//    CYCLIC("cyclic"),
//    SOPHISTICATED_BACKPACKS("sophisticatedbackpacks"),
//    SOPHISTICATED_CORE("sophisticatedcore"),
//    IRON_FURNACES("ironfurnaces"),
//    FASTFURNACE("fastfurnace"),
//    FASTWORKBENCH("fastbench"),
//    PRETTY_PIPES("prettypipes"),
//    REFINED_STORAGE("refinedstorage"),
//    REFINED_STORAGE_ADDONS("refinedstorageaddons"),
//    APPLIED_ENERGISTICS_2("ae2"),
//    EXTENDED_CRAFTING("extendedcrafting"),
//    TINKERS_CONSTRUCT("tconstruct"),
//    OCCULTISM("occultism");
//
//    private final String id;
//
//    Mod(String pId) {
//      this.id = pId;
//    }
//
//    public String getId() {
//      return this.id;
//    }
//  }
}
