package top.theillusivec4.polymorph.mixin.integration.tconstruct;

import net.minecraft.inventory.CraftingInventory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import slimeknights.tconstruct.tables.tileentity.table.CraftingStationTileEntity;
import slimeknights.tconstruct.tables.tileentity.table.crafting.CraftingInventoryWrapper;

@Mixin(CraftingStationTileEntity.class)
public interface AccessorCraftingStationTileEntity {

  @Accessor(remap = false)
  CraftingInventoryWrapper getCraftingInventory();
}
