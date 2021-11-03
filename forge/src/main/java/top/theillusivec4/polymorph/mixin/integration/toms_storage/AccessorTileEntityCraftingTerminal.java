package top.theillusivec4.polymorph.mixin.integration.toms_storage;

import com.tom.storagemod.tile.TileEntityCraftingTerminal;
import net.minecraft.item.crafting.ICraftingRecipe;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(TileEntityCraftingTerminal.class)
public interface AccessorTileEntityCraftingTerminal {

  @Accessor(remap = false)
  void setCurrentRecipe(ICraftingRecipe pRecipe);

  @Invoker(remap = false)
  void callOnCraftingMatrixChanged();
}
