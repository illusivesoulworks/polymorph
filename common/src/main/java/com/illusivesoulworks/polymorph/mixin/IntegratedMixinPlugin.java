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

package com.illusivesoulworks.polymorph.mixin;

import com.illusivesoulworks.polymorph.PolymorphConstants;
import com.illusivesoulworks.polymorph.common.integration.PolymorphIntegrations;
import com.illusivesoulworks.polymorph.platform.Services;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.mixin.Mixins;
import org.spongepowered.asm.mixin.extensibility.IMixinConfig;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinErrorHandler;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;

@SuppressWarnings("unused")
public class IntegratedMixinPlugin implements IMixinConfigPlugin, IMixinErrorHandler {

  private static final Map<String, String> CLASS_TO_MOD = new HashMap<>();

  static {
    CLASS_TO_MOD.put("dev.shadowsoffire.fastbench.", PolymorphIntegrations.Mod.FASTWORKBENCH.getId());
    CLASS_TO_MOD.put("dev.shadowsoffire.fastsuite.", PolymorphIntegrations.Mod.FASTSUITE.getId());
  }

  @Override
  public void onLoad(String mixinPackage) {
    Mixins.registerErrorHandlerClass("com.illusivesoulworks.polymorph.mixin.IntegratedMixinPlugin");
    PolymorphIntegrations.loadConfig();
  }

  @Override
  public String getRefMapperConfig() {
    return null;
  }

  @Override
  public boolean shouldApplyMixin(String targetClassName, String mixinClassName) {

    for (Map.Entry<String, String> entry : CLASS_TO_MOD.entrySet()) {
      String modId = entry.getValue();

      if (targetClassName.startsWith(entry.getKey())) {
        return PolymorphIntegrations.isActive(modId) && Services.PLATFORM.isModFileLoaded(modId);
      }
    }
    return true;
  }

  @Override
  public void acceptTargets(Set<String> myTargets, Set<String> otherTargets) {

  }

  @Override
  public List<String> getMixins() {
    return null;
  }

  @Override
  public void preApply(String targetClassName, ClassNode targetClass, String mixinClassName,
                       IMixinInfo mixinInfo) {

  }

  @Override
  public void postApply(String targetClassName, ClassNode targetClass, String mixinClassName,
                        IMixinInfo mixinInfo) {

  }

  @Override
  public ErrorAction onPrepareError(IMixinConfig config, Throwable th, IMixinInfo mixin,
                                    ErrorAction action) {
    return null;
  }

  @Override
  public ErrorAction onApplyError(String targetClassName, Throwable th, IMixinInfo mixin,
                                  ErrorAction action) {

    if (mixin.getConfig().getMixinPackage()
        .startsWith("com.illusivesoulworks.polymorph.mixin.integration")) {
      String modId = "{MOD NOT FOUND - THIS SHOULD NOT HAPPEN}";

      for (Map.Entry<String, String> entry : CLASS_TO_MOD.entrySet()) {
        String id = entry.getValue();

        if (targetClassName.startsWith(entry.getKey())) {
          modId = id;
          break;
        }
      }
      PolymorphIntegrations.disable(modId);
      PolymorphConstants.LOG.error("Polymorph encountered an error while transforming: {}",
          targetClassName);
      PolymorphConstants.LOG.error("The integration module for {} will be disabled.", modId);
      PolymorphConstants.LOG.error(
          "Please report this bug to Polymorph only, do not report this to {}.", modId);
      return ErrorAction.WARN;
    }
    return null;
  }
}
