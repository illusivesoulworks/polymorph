package top.theillusivec4.polymorph.mixin.core;

import java.util.List;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.recipe.SmithingRecipe;
import net.minecraft.screen.ForgingScreenHandler;
import net.minecraft.screen.ScreenHandlerContext;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.screen.SmithingScreenHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import top.theillusivec4.polymorph.common.network.PolymorphPackets;
import top.theillusivec4.polymorph.mixin.util.MixinHooks;

@Mixin(SmithingScreenHandler.class)
public abstract class MixinSmithingScreenHandler extends ForgingScreenHandler {

  @Unique
  private List<SmithingRecipe> recipes;

  @Shadow
  private SmithingRecipe currentRecipe;

  public MixinSmithingScreenHandler(
      @Nullable ScreenHandlerType<?> type, int syncId,
      PlayerInventory playerInventory,
      ScreenHandlerContext context) {
    super(type, syncId, playerInventory, context);
  }

  @ModifyVariable(at = @At(value = "INVOKE_ASSIGN", target = "net/minecraft/recipe/RecipeManager.getAllMatches (Lnet/minecraft/recipe/RecipeType;Lnet/minecraft/inventory/Inventory;Lnet/minecraft/world/World;)Ljava/util/List;"), method = "updateResult")
  private List<SmithingRecipe> polymorph$getRecipes(List<SmithingRecipe> recipes) {
    this.recipes = recipes;

    if (this.player instanceof ServerPlayerEntity) {
      ServerPlayNetworking.send((ServerPlayerEntity) this.player, PolymorphPackets.SEND_RECIPES,
          PacketByteBufs.empty());
    }
    return recipes;
  }

  @Inject(at = @At(value = "INVOKE_ASSIGN", target = "java/util/List.get(I)Ljava/lang/Object;", shift = At.Shift.BY, by = 3), method = "updateResult")
  private void polymorph$updateResult(CallbackInfo ci) {
    this.currentRecipe = MixinHooks.getSmithingRecipe(this.player, this.recipes);
  }
}
