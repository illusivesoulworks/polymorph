//package top.theillusivec4.polymorph.common.integration.toms_storage;
//
//import com.tom.storagemod.tile.TileEntityCraftingTerminal;
//import javax.annotation.Nullable;
//import net.minecraft.entity.player.PlayerEntity;
//import net.minecraft.inventory.CraftingInventory;
//import net.minecraft.item.ItemStack;
//import net.minecraft.item.crafting.ICraftingRecipe;
//import net.minecraft.item.crafting.IRecipe;
//import net.minecraft.item.crafting.IRecipeType;
//import net.minecraft.util.NonNullList;
//import top.theillusivec4.polymorph.common.capability.AbstractRecipeProcessor;
//import top.theillusivec4.polymorph.common.util.PolymorphAccessor;
//
//public class CraftingTerminalRecipeProcessor extends
//    AbstractRecipeProcessor<TileEntityCraftingTerminal, CraftingInventory, ICraftingRecipe> {
//
//  public CraftingTerminalRecipeProcessor(TileEntityCraftingTerminal tileEntity) {
//    super(tileEntity);
//  }
//
//  @Override
//  public NonNullList<ItemStack> getInput() {
//    CraftingInventory inventory = this.getInventory();
//    NonNullList<ItemStack> input =
//        NonNullList.withSize(inventory.getSizeInventory(), ItemStack.EMPTY);
//
//    for (int i = 0; i < inventory.getSizeInventory(); i++) {
//      input.set(i, inventory.getStackInSlot(i));
//    }
//    return input;
//  }
//
//  @Override
//  public void setSelectedRecipe(IRecipe<?> recipe, @Nullable PlayerEntity selectingPlayer) {
//    super.setSelectedRecipe(recipe, selectingPlayer);
//    PolymorphAccessor.writeField(this.getTileEntity(), "currentRecipe", recipe);
//    PolymorphAccessor.invokeMethod(this.getTileEntity(), "onCraftingMatrixChanged");
//  }
//
//  @Override
//  public CraftingInventory getInventory() {
//    return this.getTileEntity().getCraftingInv();
//  }
//
//  @Override
//  public IRecipeType<? extends ICraftingRecipe> getRecipeType() {
//    return IRecipeType.CRAFTING;
//  }
//}
