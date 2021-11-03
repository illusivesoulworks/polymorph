package top.theillusivec4.polymorph.mixin.integration.cyclic;

import com.lothrazar.cyclic.block.crafter.TileCrafter;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(TileCrafter.class)
public interface AccessorTileCrafter {

  @Accessor(remap = false)
  LazyOptional<IItemHandler> getGridCap();

  @Accessor(remap = false)
  LazyOptional<IItemHandler> getPreview();

  @Accessor(remap = false)
  void setLastValidRecipe(IRecipe<?> pLastValidRecipe);

  @Accessor(remap = false)
  void setRecipeOutput(ItemStack pRecipeOutput);

  @Invoker(remap = false)
  void callSetPreviewSlot(IItemHandler pPreviewHandler, ItemStack pStack);
}
