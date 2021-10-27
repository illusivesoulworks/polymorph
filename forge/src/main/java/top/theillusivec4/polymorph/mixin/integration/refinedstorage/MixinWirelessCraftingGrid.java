package top.theillusivec4.polymorph.mixin.integration.refinedstorage;

import com.refinedmods.refinedstorage.api.network.grid.ICraftingGridListener;
import com.refinedmods.refinedstorageaddons.item.WirelessCraftingGrid;
import java.util.Optional;
import java.util.Set;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.item.crafting.RecipeManager;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import top.theillusivec4.polymorph.mixin.util.integration.RefinedStorageHooks;

@SuppressWarnings("unused")
@Mixin(WirelessCraftingGrid.class)
public class MixinWirelessCraftingGrid {

  @Shadow(remap = false)
  @SuppressWarnings("MismatchedQueryAndUpdateOfCollection")
  private Set<ICraftingGridListener> listeners;

  @Redirect(at = @At(value = "INVOKE", target = "net/minecraft/item/crafting/RecipeManager.getRecipe(Lnet/minecraft/item/crafting/IRecipeType;Lnet/minecraft/inventory/IInventory;Lnet/minecraft/world/World;)Ljava/util/Optional;"), method = "onCraftingMatrixChanged", remap = false)
  private <C extends IInventory, T extends IRecipe<C>> Optional<T> polymorph$getRecipe(
      RecipeManager recipeManager, IRecipeType<T> type, C inventory, World world) {
    return RefinedStorageHooks.getWirelessRecipe(recipeManager, type, inventory, world,
        listeners.stream().findFirst().orElse(null));
  }
}
