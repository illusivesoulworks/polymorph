package top.theillusivec4.polymorph.common.integration.toms_storage;

import com.tom.storagemod.tile.TileEntityCraftingTerminal;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.item.crafting.ICraftingRecipe;
import net.minecraft.item.crafting.IRecipeType;
import top.theillusivec4.polymorph.common.capability.AbstractRecipeProcessor;

public class CraftingTerminalRecipeProcessor extends
    AbstractRecipeProcessor<TileEntityCraftingTerminal, CraftingInventory, ICraftingRecipe> {

  public CraftingTerminalRecipeProcessor(TileEntityCraftingTerminal tileEntity) {
    super(tileEntity);
  }

  @Override
  public CraftingInventory getInventory() {
    return this.getTileEntity().getCraftingInv();
  }

  @Override
  public IRecipeType<? extends ICraftingRecipe> getRecipeType() {
    return IRecipeType.CRAFTING;
  }
}
