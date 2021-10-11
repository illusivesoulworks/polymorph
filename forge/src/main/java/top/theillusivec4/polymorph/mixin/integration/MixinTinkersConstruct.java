package top.theillusivec4.polymorph.mixin.integration;

import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.item.crafting.RecipeManager;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import slimeknights.tconstruct.tables.tileentity.table.CraftingStationTileEntity;
import top.theillusivec4.polymorph.common.util.RecipeCache;
import top.theillusivec4.polymorph.mixin.util.integration.TinkersConstructHooks;

@Mixin(CraftingStationTileEntity.class)
public class MixinTinkersConstruct {

  private RecipeCache cache;

  @Inject(at = @At("TAIL"), method = "<init>", remap = false)
  private void polymorph$init(CallbackInfo ci) {
    cache = new RecipeCache();
  }

  @Redirect(at = @At(value = "INVOKE", target = "net/minecraft/item/crafting/RecipeManager.getRecipe(Lnet/minecraft/item/crafting/IRecipeType;Lnet/minecraft/inventory/IInventory;Lnet/minecraft/world/World;)Ljava/util/Optional;"), method = "calcResult", remap = false)
  private <C extends IInventory, T extends IRecipe<C>> Optional<T> polymorph$getRecipe(
      RecipeManager recipeManager, IRecipeType<T> type, C inventory, World world,
      @Nullable PlayerEntity player) {
    return TinkersConstructHooks.getRecipe(recipeManager, type, inventory, world, player, cache);
  }

  @Inject(at = @At("HEAD"), method = "calcResult", remap = false)
  private void polymorph$sendEmptyRecipes(@Nullable PlayerEntity player,
                                          CallbackInfoReturnable<ItemStack> cir) {
    TinkersConstructHooks.sendEmptyRecipes((CraftingStationTileEntity) (Object) this, player);
  }

  @Inject(at = @At(value = "INVOKE", target = "net/minecraft/server/MinecraftServer.getRecipeManager()Lnet/minecraft/item/crafting/RecipeManager;"), method = "calcResult", remap = false)
  private void polymorph$sendRecipes(@Nullable PlayerEntity player,
                                     CallbackInfoReturnable<ItemStack> cir) {
    TinkersConstructHooks.sendRecipes(player, cache.getLastRecipes());
  }
}
