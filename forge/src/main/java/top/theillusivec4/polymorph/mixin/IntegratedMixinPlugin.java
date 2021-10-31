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
import net.minecraftforge.fml.loading.FMLLoader;
import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;

public class IntegratedMixinPlugin implements IMixinConfigPlugin {

  private static final Map<String, String> CLASS_TO_MOD = new HashMap<>();

  static {
    CLASS_TO_MOD.put("mezz.jei", "jei");
    CLASS_TO_MOD.put("net.blay09.mods.craftingcraft", "craftingcraft");
    CLASS_TO_MOD.put("com.tfar.craftingstation", "craftingstation");
    CLASS_TO_MOD.put("de.ellpeck.prettypipes", "prettypipes");
    CLASS_TO_MOD.put("com.tom.storagemod", "toms_storage");
    CLASS_TO_MOD.put("shadows.fastbench", "fastbench");
    CLASS_TO_MOD.put("com.lothrazar.storagenetwork", "storagenetwork");
    CLASS_TO_MOD.put("com.refinedmods.refinedstorage", "refinedstorage");
    CLASS_TO_MOD.put("com.refinedmods.refinedstorageaddons", "refinedstorageaddons");
    CLASS_TO_MOD.put("slimeknights.tconstruct", "tconstruct");
    CLASS_TO_MOD.put("com.lothrazar.cyclic", "cyclic");
    CLASS_TO_MOD.put("net.p3pp3rf1y.sophisticatedbackpacks", "sophisticatedbackpacks");
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

    for (Map.Entry<String, String> entry : CLASS_TO_MOD.entrySet()) {

      if (targetClassName.startsWith(entry.getKey())) {
        return FMLLoader.getLoadingModList().getModFileById(entry.getValue()) != null;
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
