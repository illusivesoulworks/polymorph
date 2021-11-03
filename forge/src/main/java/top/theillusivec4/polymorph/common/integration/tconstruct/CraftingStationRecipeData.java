package top.theillusivec4.polymorph.common.integration.tconstruct;

import net.minecraft.inventory.CraftingInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import slimeknights.tconstruct.tables.tileentity.table.CraftingStationTileEntity;
import top.theillusivec4.polymorph.common.capability.AbstractTileEntityRecipeData;
import top.theillusivec4.polymorph.mixin.integration.tconstruct.AccessorCraftingStationTileEntity;

public class CraftingStationRecipeData
    extends AbstractTileEntityRecipeData<CraftingStationTileEntity> {

  private CraftingInventory craftingInventory;

  public CraftingStationRecipeData(CraftingStationTileEntity pOwner) {
    super(pOwner);
  }

  @Override
  protected NonNullList<ItemStack> getInput() {

    if (this.craftingInventory == null) {
      this.craftingInventory =
          ((AccessorCraftingStationTileEntity) this.getOwner()).getCraftingInventory();
    }

    if (this.craftingInventory != null) {

      NonNullList<ItemStack> input =
          NonNullList.withSize(this.craftingInventory.getSizeInventory(), ItemStack.EMPTY);

      for (int i = 0; i < this.craftingInventory.getSizeInventory(); i++) {
        input.set(i, this.craftingInventory.getStackInSlot(i));
      }
      return input;
    }
    return NonNullList.create();
  }
}
