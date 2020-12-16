package top.theillusivec4.polymorph.loader.mixin;

import java.util.List;
import java.util.Set;
import net.fabricmc.loader.api.FabricLoader;
import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;

public class IntegratedMixinPlugin implements IMixinConfigPlugin {

  @Override
  public void onLoad(String mixinPackage) {

  }

  @Override
  public String getRefMapperConfig() {
    return null;
  }

  @Override
  public boolean shouldApplyMixin(String targetClassName, String mixinClassName) {

    if (targetClassName.equals("me.shedaniel.rei.impl.InternalWidgets") &&
        mixinClassName.equals("top.theillusivec4.polymorph.loader.mixin.integration.ReiMixin")) {
      return FabricLoader.getInstance().isModLoaded("roughlyenoughitems-runtime");
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
