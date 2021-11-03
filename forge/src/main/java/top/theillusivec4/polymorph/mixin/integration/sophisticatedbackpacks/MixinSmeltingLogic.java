package top.theillusivec4.polymorph.mixin.integration.sophisticatedbackpacks;

import java.util.Optional;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipe;
import net.p3pp3rf1y.sophisticatedbackpacks.upgrades.smelting.SmeltingLogic;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import top.theillusivec4.polymorph.common.integration.sophisticatedbackpacks.SophisticatedBackpacksModule;

@SuppressWarnings("unused")
@Mixin(SmeltingLogic.class)
public abstract class MixinSmeltingLogic {

  @Shadow(remap = false)
  @Final
  private ItemStack upgrade;

  @Redirect(
      at = @At(
          value = "INVOKE",
          target = "net/p3pp3rf1y/sophisticatedbackpacks/util/RecipeHelper.getSmeltingRecipe(Lnet/minecraft/item/ItemStack;)Ljava/util/Optional;"),
      method = "getSmeltingRecipe",
      remap = false)
  private Optional<FurnaceRecipe> polymorph$getSmeltingRecipe(ItemStack pStack) {
    return SophisticatedBackpacksModule.getSmeltingRecipe(pStack, this.upgrade);
  }
}
