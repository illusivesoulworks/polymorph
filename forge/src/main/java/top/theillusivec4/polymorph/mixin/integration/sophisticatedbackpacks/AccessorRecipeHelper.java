package top.theillusivec4.polymorph.mixin.integration.sophisticatedbackpacks;

import java.lang.ref.WeakReference;
import net.minecraft.world.World;
import net.p3pp3rf1y.sophisticatedbackpacks.util.RecipeHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(RecipeHelper.class)
public interface AccessorRecipeHelper {

  @Accessor(remap = false)
  static WeakReference<World> getWorld() {
    throw new IllegalStateException("Missing accessor implementation!");
  }
}
