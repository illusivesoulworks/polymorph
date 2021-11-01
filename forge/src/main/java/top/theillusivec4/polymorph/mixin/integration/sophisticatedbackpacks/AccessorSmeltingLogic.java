package top.theillusivec4.polymorph.mixin.integration.sophisticatedbackpacks;

import net.p3pp3rf1y.sophisticatedbackpacks.upgrades.smelting.SmeltingLogic;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(SmeltingLogic.class)
public interface AccessorSmeltingLogic {

  @Accessor(remap = false)
  void setSmeltingRecipeInitialized(boolean pVal);
}
