package top.theillusivec4.polymorph.mixin.integration.recipecache;

import com.biom4st3r.recipecache.CraftingLookup;
import java.util.Optional;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.inventory.CraftingResultInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.recipe.CraftingRecipe;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeManager;
import net.minecraft.recipe.RecipeType;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import top.theillusivec4.polymorph.api.PolymorphApi;
import top.theillusivec4.polymorph.common.crafting.RecipeSelection;

@Mixin(CraftingLookup.class)
public class MixinCraftingLookup {

  @Inject(at = @At("HEAD"), method = "craftingLookup")
  private void polymorph$craftingLookup(ScreenHandler handler, World world, CraftingInventory input,
                                        PlayerEntity player, Optional<CraftingRecipe> lastRecipe,
                                        CraftingResultInventory result, CallbackInfo ci) {

    if (player instanceof ServerPlayerEntity serverPlayer && input.isEmpty()) {
      PolymorphApi.common().getPacketDistributor().sendRecipesListS2C(serverPlayer);
    }
  }

  @Redirect(at = @At(value = "INVOKE", target = "net/minecraft/recipe/RecipeManager.getFirstMatch(Lnet/minecraft/recipe/RecipeType;Lnet/minecraft/inventory/Inventory;Lnet/minecraft/world/World;)Ljava/util/Optional;"), method = "craftingLookup")
  private <C extends Inventory, T extends Recipe<C>> Optional<T> polymorph$getFirstMatch(
      RecipeManager recipeManager, RecipeType<T> type, C inventory, World world,
      ScreenHandler screenHandler, World unused, CraftingInventory craftingInventory,
      PlayerEntity player, Optional<CraftingRecipe> lastRecipe,
      CraftingResultInventory resultInventory) {
    return RecipeSelection.getPlayerRecipe(screenHandler, type, inventory, world, player);
  }
}
