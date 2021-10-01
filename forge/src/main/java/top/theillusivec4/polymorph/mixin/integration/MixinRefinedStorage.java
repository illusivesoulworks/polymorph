package top.theillusivec4.polymorph.mixin.integration;

import com.refinedmods.refinedstorage.apiimpl.network.node.GridNetworkNode;
import com.refinedmods.refinedstorage.apiimpl.network.node.NetworkNode;
import com.refinedmods.refinedstorage.inventory.item.BaseItemHandler;
import java.util.Optional;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.item.crafting.RecipeManager;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import top.theillusivec4.polymorph.mixin.util.integration.RefinedStorageHooks;

@Mixin(GridNetworkNode.class)
public abstract class MixinRefinedStorage extends NetworkNode {

  @Shadow
  private boolean exactPattern;

  @Shadow
  @Final
  private BaseItemHandler patterns;

  protected MixinRefinedStorage(World world, BlockPos pos) {
    super(world, pos);
  }

  @Redirect(at = @At(value = "INVOKE", target = "net/minecraft/item/crafting/RecipeManager.getRecipe(Lnet/minecraft/item/crafting/IRecipeType;Lnet/minecraft/inventory/IInventory;Lnet/minecraft/world/World;)Ljava/util/Optional;"), method = "onCraftingMatrixChanged", remap = false)
  private <C extends IInventory, T extends IRecipe<C>> Optional<T> polymorph$getRecipe(
      RecipeManager recipeManager, IRecipeType<T> type, C inventory, World world) {
    return RefinedStorageHooks.getRecipe(this.pos, recipeManager, type, inventory, world);
  }

  @Inject(at = @At("TAIL"), method = "onCreatePattern", remap = false)
  private void polymorph$onCreatePattern(CallbackInfo ci) {
    RefinedStorageHooks.appendPattern(this.exactPattern, this.patterns.getStackInSlot(1), this.pos,
        this.world);
  }
}
