package top.theillusivec4.polymorph.mixin.integration.appliedenergistics2;

import appeng.api.storage.ITerminalHost;
import appeng.container.me.items.ItemTerminalContainer;
import appeng.container.me.items.PatternTermContainer;
import java.util.Optional;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.item.crafting.RecipeManager;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.PacketDistributor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import top.theillusivec4.polymorph.common.crafting.RecipeSelection;
import top.theillusivec4.polymorph.common.integration.appliedenergistics2.CPacketCallUpdate;
import top.theillusivec4.polymorph.common.network.PolymorphNetwork;

@SuppressWarnings("unused")
@Mixin(PatternTermContainer.class)
public abstract class MixinPatternTermContainer extends ItemTerminalContainer {

  public MixinPatternTermContainer(int id, PlayerInventory ip, ITerminalHost monitorable) {
    super(id, ip, monitorable);
  }

  @Inject(
      at = @At("RETURN"),
      method = "putStackInSlot",
      remap = false
  )
  private void polymorph$putStack(int slotID, ItemStack stack, CallbackInfo ci) {
    PolymorphNetwork.get().send(PacketDistributor.SERVER.noArg(), new CPacketCallUpdate());
  }

  @Redirect(
      at = @At(
          value = "INVOKE",
          target = "net/minecraft/item/crafting/RecipeManager.getRecipe(Lnet/minecraft/item/crafting/IRecipeType;Lnet/minecraft/inventory/IInventory;Lnet/minecraft/world/World;)Ljava/util/Optional;"),
      method = "getAndUpdateOutput",
      remap = false)
  private <C extends IInventory, T extends IRecipe<C>> Optional<T> polymorph$getRecipe(
      RecipeManager recipeManager, IRecipeType<T> type, C inventory, World world) {
    return RecipeSelection.getPlayerRecipe(type, inventory, world,
        this.getPlayerInventory().player);
  }
}
