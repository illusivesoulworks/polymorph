//package top.theillusivec4.polymorph.common.capability;
//
//import net.minecraft.entity.player.PlayerEntity;
//import net.minecraft.inventory.IInventory;
//import net.minecraft.inventory.container.AbstractFurnaceContainer;
//import net.minecraft.item.ItemStack;
//import net.minecraft.item.crafting.AbstractCookingRecipe;
//import net.minecraft.item.crafting.IRecipe;
//import net.minecraft.item.crafting.IRecipeType;
//import net.minecraft.tileentity.AbstractFurnaceTileEntity;
//import net.minecraft.util.NonNullList;
//import net.minecraft.world.World;
//import net.minecraft.world.server.ServerWorld;
//import top.theillusivec4.polymorph.api.PolymorphApi;
//import top.theillusivec4.polymorph.mixin.core.AccessorAbstractFurnaceContainer;
//import top.theillusivec4.polymorph.mixin.core.AccessorAbstractFurnaceTileEntity;
//
//public class FurnaceRecipeProcessor
//    extends AbstractRecipeProcessor<AbstractFurnaceTileEntity, IInventory, AbstractCookingRecipe> {
//
//  public FurnaceRecipeProcessor(AbstractFurnaceTileEntity tileEntity) {
//    super(tileEntity);
//  }
//
//  @Override
//  public NonNullList<ItemStack> getInput() {
//    return NonNullList.from(ItemStack.EMPTY, this.getInventory().getStackInSlot(0));
//  }
//
//  @Override
//  public IInventory getInventory() {
//    return this.getTileEntity();
//  }
//
//  @Override
//  public IRecipeType<? extends AbstractCookingRecipe> getRecipeType() {
//    return ((AccessorAbstractFurnaceTileEntity) this.getTileEntity()).getRecipeType();
//  }
//
//  @Override
//  public void setSelectedRecipe(IRecipe<?> recipe, PlayerEntity selectingPlayer) {
//    super.setSelectedRecipe(recipe, selectingPlayer);
//    World world = this.getTileEntity().getWorld();
//
//    if (world instanceof ServerWorld) {
//      ((ServerWorld) world).getPlayers().forEach(player -> {
//        if (player.openContainer instanceof AbstractFurnaceContainer &&
//            ((AccessorAbstractFurnaceContainer) player.openContainer).getFurnaceInventory() ==
//                this.getTileEntity()) {
//          PolymorphApi.common().getPacketDistributor()
//              .sendHighlightRecipeS2C(player, recipe.getId());
//        }
//      });
//    }
//  }
//}
