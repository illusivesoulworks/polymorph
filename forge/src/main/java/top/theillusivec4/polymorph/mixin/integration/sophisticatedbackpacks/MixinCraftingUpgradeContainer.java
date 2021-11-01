package top.theillusivec4.polymorph.mixin.integration.sophisticatedbackpacks;

import java.util.Optional;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.CraftResultInventory;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.container.CraftingResultSlot;
import net.minecraft.item.crafting.ICraftingRecipe;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.item.crafting.RecipeManager;
import net.minecraft.world.World;
import net.p3pp3rf1y.sophisticatedbackpacks.common.gui.UpgradeContainerBase;
import net.p3pp3rf1y.sophisticatedbackpacks.common.gui.UpgradeContainerType;
import net.p3pp3rf1y.sophisticatedbackpacks.upgrades.crafting.CraftingUpgradeContainer;
import net.p3pp3rf1y.sophisticatedbackpacks.upgrades.crafting.CraftingUpgradeWrapper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import top.theillusivec4.polymorph.common.crafting.RecipeSelection;
import top.theillusivec4.polymorph.mixin.util.integration.SophisticatedBackpacksMixinHooks;

@Mixin(CraftingUpgradeContainer.class)
public abstract class MixinCraftingUpgradeContainer extends
    UpgradeContainerBase<CraftingUpgradeWrapper, CraftingUpgradeContainer> {

  @Shadow(remap = false)
  private ICraftingRecipe lastRecipe;

  protected MixinCraftingUpgradeContainer(PlayerEntity player, int upgradeContainerId,
                                          CraftingUpgradeWrapper upgradeWrapper,
                                          UpgradeContainerType<CraftingUpgradeWrapper, CraftingUpgradeContainer> type) {
    super(player, upgradeContainerId, upgradeWrapper, type);
  }

  @Inject(
      at = @At(value = "HEAD"),
      method = "updateCraftingResult",
      remap = false)
  private void polymorph$updateCraftingResult(World world, PlayerEntity player,
                                              CraftingInventory inventory,
                                              CraftResultInventory inventoryResult,
                                              CraftingResultSlot craftingResultSlot,
                                              CallbackInfo ci) {
    SophisticatedBackpacksMixinHooks.updateCraftingResult(world, this.lastRecipe, inventory,
        player, this.getUpgradeStack());
  }

  @Redirect(
      at = @At(
          value = "INVOKE",
          target = "net/minecraft/item/crafting/RecipeManager.getRecipe(Lnet/minecraft/item/crafting/IRecipeType;Lnet/minecraft/inventory/IInventory;Lnet/minecraft/world/World;)Ljava/util/Optional;"),
      method = "updateCraftingResult",
      remap = false)
  private <C extends IInventory, T extends IRecipe<C>> Optional<T> polymorph$getRecipe(
      RecipeManager recipeManager, IRecipeType<T> type, C inventory, World world,
      World unused, PlayerEntity player, CraftingInventory unused2,
      CraftResultInventory inventoryResult, CraftingResultSlot craftingResultSlot) {
    return RecipeSelection.getStackRecipe(type, inventory, world, this.getUpgradeStack());
  }
}
