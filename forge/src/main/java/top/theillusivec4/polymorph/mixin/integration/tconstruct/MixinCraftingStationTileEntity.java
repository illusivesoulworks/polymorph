package top.theillusivec4.polymorph.mixin.integration.tconstruct;

import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.ICraftingRecipe;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.item.crafting.RecipeManager;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import slimeknights.tconstruct.tables.tileentity.table.CraftingStationTileEntity;
import slimeknights.tconstruct.tables.tileentity.table.RetexturedTableTileEntity;
import top.theillusivec4.polymorph.mixin.util.integration.TinkersConstructMixinHooks;

@Mixin(CraftingStationTileEntity.class)
public abstract class MixinCraftingStationTileEntity extends RetexturedTableTileEntity {

  @Shadow
  private ICraftingRecipe lastRecipe;

  public MixinCraftingStationTileEntity(TileEntityType<?> type,
                                        String name, int size) {
    super(type, name, size);
  }

  @Redirect(
      at = @At(
          value = "INVOKE",
          target = "net/minecraft/item/crafting/RecipeManager.getRecipe(Lnet/minecraft/item/crafting/IRecipeType;Lnet/minecraft/inventory/IInventory;Lnet/minecraft/world/World;)Ljava/util/Optional;"),
      method = "calcResult",
      remap = false)
  private <C extends IInventory, T extends IRecipe<C>> Optional<T> polymorph$getRecipe(
      RecipeManager recipeManager, IRecipeType<T> type, C inventory, World world,
      @Nullable PlayerEntity player) {
    return TinkersConstructMixinHooks.getRecipe(type, inventory, world, player, this);
  }

  @Inject(
      at = @At("HEAD"),
      method = "calcResult",
      remap = false)
  private void polymorph$calcResult(@Nullable PlayerEntity player,
                                    CallbackInfoReturnable<ItemStack> cir) {
    TinkersConstructMixinHooks.calcResult(this, player);
  }
}
