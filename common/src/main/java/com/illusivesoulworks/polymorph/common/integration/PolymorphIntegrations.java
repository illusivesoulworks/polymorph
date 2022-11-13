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
import com.electronwill.nightconfig.core.file.CommentedFileConfig;
import com.google.common.collect.ImmutableSet;
import com.illusivesoulworks.polymorph.platform.Services;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
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
    List<Mod> mods = Arrays.asList(Mod.values(Services.PLATFORM.getLoader()));

    if (mods.isEmpty()) {
      return;
    }
    Collections.sort(mods);

    for (Mod mod : mods) {
      spec.define(mod.getId(), mod.getDefaultValue());
    }
    CommentedFileConfig config =
        CommentedFileConfig.of(
            Services.PLATFORM.getConfigDir().resolve("polymorph-integrations.toml"));
    config.load();
    config.setComment(mods.get(0).getId(),
        " Please be aware that enabling any third-party mod integration introduces instability and performance overheads, caution is strongly advised.\n If crashes or issues arise, disable the related modules as the first step in troubleshooting and report the issue to Polymorph.");

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
    FASTFURNACE("fastfurnace", true, Loader.FORGE),
    FASTWORKBENCH("fastbench", true, Loader.FORGE),
    FASTSUITE("fastsuite", true, Loader.FORGE);

    private final String id;
    private final boolean defaultValue;
    private final Loader[] loaders;

    Mod(String id, Loader defaultLoader, Loader... extraLoaders) {
      this(id, false, defaultLoader, extraLoaders);
    }

    Mod(String id, boolean defaultValue, Loader defaultLoader, Loader... extraLoaders) {
      this.id = id;
      this.defaultValue = defaultValue;
      this.loaders = new Loader[extraLoaders.length + 1];
      this.loaders[0] = defaultLoader;
      System.arraycopy(extraLoaders, 0, this.loaders, 1, this.loaders.length - 1);
    }

    public boolean getDefaultValue() {
      return this.defaultValue;
    }

    public String getId() {
      return this.id;
    }

    public static Mod[] values(Loader loader) {
      return Arrays.stream(Mod.values())
          .filter(mod -> Arrays.stream(mod.loaders).anyMatch(test -> test == loader))
          .toArray(Mod[]::new);
    }
  }

  public enum Loader {
    FABRIC,
    FORGE
  }
}
