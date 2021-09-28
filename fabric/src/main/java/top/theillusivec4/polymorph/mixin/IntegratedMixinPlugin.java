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

package top.theillusivec4.polymorph.mixin;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import net.fabricmc.loader.api.FabricLoader;
import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;

public class IntegratedMixinPlugin implements IMixinConfigPlugin {

  private static final Map<String, String> CLASS_TO_MOD = new HashMap<>();

  static {
    CLASS_TO_MOD.put("me.shedaniel.rei.impl.InternalWidgets", "roughlyenoughitems-runtime");
    CLASS_TO_MOD.put("me.shedaniel.istations.containers.CraftingStationScreenHandler",
        "improved-stations");
    CLASS_TO_MOD.put("appeng.container.me.items.CraftingTermContainer", "appliedenergistics2");
    CLASS_TO_MOD.put("appeng.container.me.items.PatternTermContainer", "appliedenergistics2");
    CLASS_TO_MOD.put("tfar.fastbench.MixinHooks", "fastbench");
  }

  @Override
  public void onLoad(String mixinPackage) {

  }

  @Override
  public String getRefMapperConfig() {
    return null;
  }

  @Override
  public boolean shouldApplyMixin(String targetClassName, String mixinClassName) {
    return !CLASS_TO_MOD.containsKey(targetClassName) ||
        FabricLoader.getInstance().isModLoaded(CLASS_TO_MOD.get(targetClassName));
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
