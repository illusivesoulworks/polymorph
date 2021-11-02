package top.theillusivec4.polymorph.mixin.integration.cyclic;

import com.lothrazar.cyclic.base.TileEntityBase;
import com.lothrazar.cyclic.block.crafter.TileCrafter;
import java.util.ArrayList;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.tileentity.TileEntityType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import top.theillusivec4.polymorph.common.integration.cyclic.CyclicModule;

@SuppressWarnings("unused")
@Mixin(TileCrafter.class)
public abstract class MixinTileCrafter extends TileEntityBase {

  public MixinTileCrafter(TileEntityType<?> tileEntityTypeIn) {
    super(tileEntityTypeIn);
  }

  @SuppressWarnings("ConstantConditions")
  @Inject(
      at = @At("HEAD"),
      method = "tryRecipes",
      remap = false,
      cancellable = true)
  private void polymorph$tryRecipes(ArrayList<ItemStack> stacks,
                                    CallbackInfoReturnable<IRecipe<?>> cir) {
    CyclicModule.getRecipe(stacks, this.world, (TileCrafter) (Object) this)
        .ifPresent(cir::setReturnValue);
  }
}
