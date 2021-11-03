package top.theillusivec4.polymorph.mixin.integration.sophisticatedbackpacks;

import net.minecraft.inventory.IInventory;
import net.minecraft.item.crafting.ICraftingRecipe;
import net.p3pp3rf1y.sophisticatedbackpacks.upgrades.crafting.CraftingUpgradeContainer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(CraftingUpgradeContainer.class)
public interface AccessorCraftingUpgradeContainer {

  @Accessor(remap = false)
  void setLastRecipe(ICraftingRecipe pRecipe);

  @Invoker(remap = false)
  void callOnCraftMatrixChanged(IInventory pInventory);
}
