//package top.theillusivec4.polymorph.common.capability;
//
//import javax.annotation.Nonnull;
//import net.minecraft.inventory.CraftingInventory;
//import net.minecraft.inventory.ItemStackHelper;
//import net.minecraft.item.ItemStack;
//import net.minecraft.item.crafting.ICraftingRecipe;
//import net.minecraft.item.crafting.IRecipeType;
//import net.minecraft.item.crafting.RecipeItemHelper;
//import net.minecraft.tileentity.TileEntity;
//import net.minecraft.util.NonNullList;
//
//public abstract class AbstractCraftingRecipeProcessor<T extends TileEntity>
//    extends AbstractRecipeProcessor<T, CraftingInventory, ICraftingRecipe> {
//
//  public AbstractCraftingRecipeProcessor(T tileEntity) {
//    super(tileEntity);
//  }
//
//  @Override
//  public abstract CraftingInventory getInventory();
//
//  @Override
//  public IRecipeType<? extends ICraftingRecipe> getRecipeType() {
//    return IRecipeType.CRAFTING;
//  }
//
//  public static class FalseCraftingInventory extends CraftingInventory {
//
//    private final NonNullList<ItemStack> stackList;
//
//    public FalseCraftingInventory(int width, int height) {
//      super(null, width, height);
//      this.stackList = NonNullList.withSize(width * height, ItemStack.EMPTY);
//    }
//
//    @Override
//    public int getSizeInventory() {
//      return this.stackList.size();
//    }
//
//    @Override
//    public boolean isEmpty() {
//
//      for (ItemStack itemstack : this.stackList) {
//
//        if (!itemstack.isEmpty()) {
//          return false;
//        }
//      }
//      return true;
//    }
//
//    @Override
//    @Nonnull
//    public ItemStack getStackInSlot(int index) {
//      return index >= this.getSizeInventory() ? ItemStack.EMPTY : this.stackList.get(index);
//    }
//
//    @Override
//    @Nonnull
//    public ItemStack removeStackFromSlot(int index) {
//      return ItemStackHelper.getAndRemove(this.stackList, index);
//    }
//
//    @Override
//    @Nonnull
//    public ItemStack decrStackSize(int index, int count) {
//      return ItemStackHelper.getAndSplit(this.stackList, index, count);
//    }
//
//    @Override
//    public void setInventorySlotContents(int index, @Nonnull ItemStack stack) {
//      this.stackList.set(index, stack);
//    }
//
//    @Override
//    public void clear() {
//      this.stackList.clear();
//    }
//
//    @Override
//    public void fillStackedContents(@Nonnull RecipeItemHelper helper) {
//
//      for (ItemStack itemstack : this.stackList) {
//        helper.accountPlainStack(itemstack);
//      }
//    }
//  }
//}
