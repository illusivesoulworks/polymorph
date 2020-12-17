package top.theillusivec4.polymorph.mixin;

import java.util.List;
import java.util.Set;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.loading.FMLLoader;
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

    if (targetClassName.equals("mezz.jei.transfer.RecipeTransferUtil") &&
        mixinClassName.equals("top.theillusivec4.polymorph.mixin.integration.JeiMixin")) {
      return FMLLoader.getLoadingModList().getModFileById("jei") != null;
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
