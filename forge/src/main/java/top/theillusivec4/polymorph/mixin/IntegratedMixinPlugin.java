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

package top.theillusivec4.polymorph.mixin;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import net.minecraftforge.fml.loading.FMLLoader;
import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;
import top.theillusivec4.polymorph.common.integration.PolymorphIntegrations;

@SuppressWarnings("unused")
public class IntegratedMixinPlugin implements IMixinConfigPlugin {

  private static final Map<String, String> CLASS_TO_MOD = new HashMap<>();
  private static final Set<String> CONFIG_ACTIVATED = new HashSet<>();

  static {
    CLASS_TO_MOD.put("mezz.jei.", PolymorphIntegrations.Mod.JEI.getId());
    CLASS_TO_MOD.put("net.blay09.mods.craftingcraft.",
        PolymorphIntegrations.Mod.CRAFTINGCRAFT.getId());
    CLASS_TO_MOD.put("com.tfar.craftingstation.",
        PolymorphIntegrations.Mod.CRAFTING_STATION.getId());
    CLASS_TO_MOD.put("de.ellpeck.prettypipes.", PolymorphIntegrations.Mod.PRETTY_PIPES.getId());
    CLASS_TO_MOD.put("com.tom.storagemod.", PolymorphIntegrations.Mod.TOMS_STORAGE.getId());
    CLASS_TO_MOD.put("shadows.fastbench.", PolymorphIntegrations.Mod.FASTWORKBENCH.getId());
    CLASS_TO_MOD.put("com.lothrazar.storagenetwork.",
        PolymorphIntegrations.Mod.SIMPLE_STORAGE_NETWORK.getId());
    CLASS_TO_MOD.put("com.refinedmods.refinedstorage.",
        PolymorphIntegrations.Mod.REFINED_STORAGE.getId());
    CLASS_TO_MOD.put("com.refinedmods.refinedstorageaddons.",
        PolymorphIntegrations.Mod.REFINED_STORAGE_ADDONS.getId());
    CLASS_TO_MOD.put("slimeknights.tconstruct.",
        PolymorphIntegrations.Mod.TINKERS_CONSTRUCT.getId());
    CLASS_TO_MOD.put("com.lothrazar.cyclic.", PolymorphIntegrations.Mod.CYCLIC.getId());
    CLASS_TO_MOD.put("net.p3pp3rf1y.sophisticatedbackpacks.",
        PolymorphIntegrations.Mod.SOPHISTICATED_BACKPACKS.getId());
    CLASS_TO_MOD.put("appeng.", PolymorphIntegrations.Mod.APPLIED_ENERGISTICS_2.getId());
    CLASS_TO_MOD.put("shadows.fastfurnace.", PolymorphIntegrations.Mod.FASTFURNACE.getId());
    CLASS_TO_MOD.put("ironfurnaces.", PolymorphIntegrations.Mod.IRON_FURNACES.getId());
  }

  @Override
  public void onLoad(String mixinPackage) {
    PolymorphIntegrations.loadConfig();
    CONFIG_ACTIVATED.addAll(PolymorphIntegrations.getConfigActivated());
  }

  @Override
  public String getRefMapperConfig() {
    return null;
  }

  @Override
  public boolean shouldApplyMixin(String targetClassName, String mixinClassName) {

    for (Map.Entry<String, String> entry : CLASS_TO_MOD.entrySet()) {

      if (targetClassName.startsWith(entry.getKey())) {
        return CONFIG_ACTIVATED.contains(entry.getValue()) &&
            FMLLoader.getLoadingModList().getModFileById(entry.getValue()) != null;
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
}
