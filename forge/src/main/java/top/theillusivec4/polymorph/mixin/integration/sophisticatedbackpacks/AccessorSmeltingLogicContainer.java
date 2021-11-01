package top.theillusivec4.polymorph.mixin.integration.sophisticatedbackpacks;

import java.util.function.Supplier;
import net.p3pp3rf1y.sophisticatedbackpacks.upgrades.smelting.SmeltingLogic;
import net.p3pp3rf1y.sophisticatedbackpacks.upgrades.smelting.SmeltingLogicContainer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(SmeltingLogicContainer.class)
public interface AccessorSmeltingLogicContainer {

  @Accessor(remap = false)
  Supplier<SmeltingLogic> getSupplySmeltingLogic();
}
