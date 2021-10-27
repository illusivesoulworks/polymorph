//package top.theillusivec4.polymorph.common.integration.cyclic;
//
//import com.lothrazar.cyclic.block.crafter.TileCrafter;
//import net.minecraft.inventory.CraftingInventory;
//import net.minecraft.item.ItemStack;
//import net.minecraft.util.NonNullList;
//import net.minecraftforge.common.util.LazyOptional;
//import net.minecraftforge.items.IItemHandler;
//import top.theillusivec4.polymorph.common.capability.AbstractCraftingRecipeProcessor;
//import top.theillusivec4.polymorph.common.util.PolymorphAccessor;
//
//public class TileCrafterRecipeProcessor extends AbstractCraftingRecipeProcessor<TileCrafter> {
//
//  public TileCrafterRecipeProcessor(TileCrafter tileEntity) {
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
//  @SuppressWarnings("unchecked")
//  @Override
//  public CraftingInventory getInventory() {
//    CraftingInventory craftingInventory = new FalseCraftingInventory(3, 3);
//    LazyOptional<IItemHandler> opt =
//        (LazyOptional<IItemHandler>) PolymorphAccessor.readField(this.getTileEntity(), "gridCap");
//
//    if (opt != null) {
//      opt.ifPresent(gridHandler -> {
//        for (int i = 0; i < 9; ++i) {
//          craftingInventory.setInventorySlotContents(i, gridHandler.getStackInSlot(i));
//        }
//      });
//    }
//    return craftingInventory;
//  }
//}
