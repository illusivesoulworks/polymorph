package top.theillusivec4.polymorph.mixin.core;

import java.util.HashSet;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.container.AbstractRepairContainer;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.inventory.container.SmithingTableContainer;
import net.minecraft.item.crafting.SmithingRecipe;
import net.minecraft.util.IWorldPosCallable;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.network.PacketDistributor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import top.theillusivec4.polymorph.common.network.PolymorphNetwork;
import top.theillusivec4.polymorph.common.network.server.SPacketSendRecipes;
import top.theillusivec4.polymorph.mixin.util.MixinHooks;

@Mixin(SmithingTableContainer.class)
public abstract class MixinSmithingTableContainer extends AbstractRepairContainer {

  @Unique
  private List<SmithingRecipe> recipes;

  @Shadow
  private SmithingRecipe field_234652_h_;

  public MixinSmithingTableContainer(@Nullable ContainerType<?> p_i231587_1_, int p_i231587_2_,
                                     PlayerInventory p_i231587_3_, IWorldPosCallable p_i231587_4_) {
    super(p_i231587_1_, p_i231587_2_, p_i231587_3_, p_i231587_4_);
  }

  @ModifyVariable(at = @At(value = "INVOKE_ASSIGN", target = "net/minecraft/item/crafting/RecipeManager.getRecipes (Lnet/minecraft/item/crafting/IRecipeType;Lnet/minecraft/inventory/IInventory;Lnet/minecraft/world/World;)Ljava/util/List;"), method = "updateRepairOutput")
  private List<SmithingRecipe> polymorph$getRecipes(List<SmithingRecipe> recipes) {
    this.recipes = recipes;

    if (this.field_234645_f_ instanceof ServerPlayerEntity) {
      PolymorphNetwork.INSTANCE.send(
          PacketDistributor.PLAYER.with(() -> (ServerPlayerEntity) this.field_234645_f_),
          new SPacketSendRecipes(new HashSet<>(), new ResourceLocation("")));
    }
    return recipes;
  }

  @Inject(at = @At(value = "INVOKE_ASSIGN", target = "java/util/List.get(I)Ljava/lang/Object;", shift = At.Shift.BY, by = 3), method = "updateRepairOutput")
  private void polymorph$updateRepairOutput(CallbackInfo ci) {
    this.field_234652_h_ = MixinHooks.getSmithingRecipe(this.field_234645_f_, this.recipes);
  }
}
