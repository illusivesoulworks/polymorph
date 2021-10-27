package top.theillusivec4.polymorph.mixin.integration.prettypipes;

import de.ellpeck.prettypipes.terminal.containers.CraftingTerminalContainer;
import de.ellpeck.prettypipes.terminal.containers.ItemTerminalContainer;
import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.item.crafting.RecipeManager;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import top.theillusivec4.polymorph.common.crafting.RecipeSelection;

@SuppressWarnings("unused")
@Mixin(CraftingTerminalContainer.class)
public class MixinCraftingTerminalContainer extends ItemTerminalContainer {

  @Shadow
  @Final
  private PlayerEntity player;

  public MixinCraftingTerminalContainer(@Nullable ContainerType<?> type, int id,
                                        PlayerEntity player, BlockPos pos) {
    super(type, id, player, pos);
  }

  @Redirect(
      at = @At(
          value = "INVOKE",
          target = "net/minecraft/item/crafting/RecipeManager.getRecipe(Lnet/minecraft/item/crafting/IRecipeType;Lnet/minecraft/inventory/IInventory;Lnet/minecraft/world/World;)Ljava/util/Optional;"),
      method = "onCraftMatrixChanged")
  private <C extends IInventory, T extends IRecipe<C>> Optional<T> polymorph$getRecipe(
      RecipeManager recipeManager, IRecipeType<T> type, C inventory, World world,
      IInventory unused) {
    return RecipeSelection.getPlayerRecipe(type, inventory, world, this.player);
  }
}
