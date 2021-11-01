package top.theillusivec4.polymorph.mixin.integration.appliedenergistics2;

import appeng.api.storage.ITerminalHost;
import appeng.container.me.items.CraftingTermContainer;
import appeng.container.me.items.ItemTerminalContainer;
import java.util.Optional;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.item.crafting.RecipeManager;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import top.theillusivec4.polymorph.common.crafting.RecipeSelection;

@Mixin(CraftingTermContainer.class)
public abstract class MixinCraftingTermContainer extends ItemTerminalContainer {

  public MixinCraftingTermContainer(int id, PlayerInventory ip, ITerminalHost monitorable) {
    super(id, ip, monitorable);
  }

  @Redirect(
      at = @At(
          value = "INVOKE",
          target = "net/minecraft/item/crafting/RecipeManager.getRecipe(Lnet/minecraft/item/crafting/IRecipeType;Lnet/minecraft/inventory/IInventory;Lnet/minecraft/world/World;)Ljava/util/Optional;"),
      method = "onCraftMatrixChanged")
  private <C extends IInventory, T extends IRecipe<C>> Optional<T> polymorph$getRecipe(
      RecipeManager recipeManager, IRecipeType<T> type, C inventory, World world,
      IInventory unused) {
    return RecipeSelection.getPlayerRecipe(type, inventory, world,
        this.getPlayerInventory().player);
  }
}
