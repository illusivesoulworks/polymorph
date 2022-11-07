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

import com.electronwill.nightconfig.core.ConfigSpec;
import com.electronwill.nightconfig.core.file.FileConfig;
import com.google.common.collect.ImmutableSet;
import com.illusivesoulworks.polymorph.platform.Services;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.level.block.entity.BlockEntity;

public class PolymorphIntegrations {

  private static final Set<String> ACTIVATED = ConcurrentHashMap.newKeySet();
  private static final Map<String, AbstractCompatibilityModule> ACTIVE_INTEGRATIONS =
      new ConcurrentHashMap<>();

  private static final Map<String, Supplier<Supplier<AbstractCompatibilityModule>>> INTEGRATIONS =
      Services.INTEGRATION_PLATFORM.createCompatibilityModules();

  public static void loadConfig() {
    ConfigSpec spec = new ConfigSpec();

    for (Mod mod : Mod.values()) {
      spec.define(mod.getId(), mod.getDefaultValue());
    }
    FileConfig config =
        FileConfig.of(Services.PLATFORM.getGameDir().resolve("polymorph-integrations.toml"));
    config.load();

    if (!spec.isCorrect(config)) {
      spec.correct(config);
    }

    for (Mod mod : Mod.values()) {

      if (config.get(mod.getId())) {
        ACTIVATED.add(mod.getId());
      }
    }
    config.save();
    config.close();
  }

  public static void init() {
    INTEGRATIONS.forEach((modid, supplier) -> {

      if (Services.PLATFORM.isModLoaded(modid)) {
        ACTIVE_INTEGRATIONS.put(modid, supplier.get().get());
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

  public static void selectRecipe(BlockEntity blockEntity, AbstractContainerMenu containerMenu,
                                  Recipe<?> recipe) {

    for (AbstractCompatibilityModule integration : PolymorphIntegrations.get()) {

      if (integration.selectRecipe(blockEntity, recipe) ||
          integration.selectRecipe(containerMenu, recipe)) {
        return;
      }
    }
  }

  public static void selectRecipe(AbstractContainerMenu containerMenu,
                                  Recipe<?> recipe) {

    for (AbstractCompatibilityModule integration : PolymorphIntegrations.get()) {

      if (integration.selectRecipe(containerMenu, recipe)) {
        return;
      }
    }
  }

  public static void openContainer(AbstractContainerMenu containerMenu, ServerPlayer serverPlayer) {

    for (AbstractCompatibilityModule integration : get()) {

      if (integration.openContainer(containerMenu, serverPlayer)) {
        return;
      }
    }
  }

  public static Set<AbstractCompatibilityModule> get() {
    return ImmutableSet.copyOf(ACTIVE_INTEGRATIONS.values());
  }

  public static boolean isActive(String id) {
    return ACTIVATED.contains(id);
  }

  public static void disable(String id) {
    ACTIVATED.remove(id);
    INTEGRATIONS.remove(id);
    AbstractCompatibilityModule module = ACTIVE_INTEGRATIONS.remove(id);

    if (module != null) {
      module.disable();
    }
  }

  public enum Mod {
    FASTFURNACE("fastfurnace", true),
    FASTWORKBENCH("fastbench", true),
    FASTSUITE("fastsuite", true);

    private final String id;
    private final boolean defaultValue;

    Mod(String id) {
      this(id, false);
    }

    Mod(String id, boolean defaultValue) {
      this.id = id;
      this.defaultValue = defaultValue;
    }

    public boolean getDefaultValue() {
      return this.defaultValue;
    }

    public String getId() {
      return this.id;
    }
  }
}
