package top.theillusivec4.polymorph.mixin.integration.refinedstorage;

import com.refinedmods.refinedstorage.apiimpl.network.node.GridNetworkNode;
import com.refinedmods.refinedstorageaddons.item.WirelessCraftingGrid;
import net.minecraft.item.crafting.ICraftingRecipe;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin({GridNetworkNode.class, WirelessCraftingGrid.class})
public interface AccessorGrid {

  @Accessor(remap = false)
  void setCurrentRecipe(ICraftingRecipe pRecipe);
}
