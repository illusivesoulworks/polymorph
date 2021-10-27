package top.theillusivec4.polymorph.mixin.util.integration;

import com.refinedmods.refinedstorage.api.network.grid.ICraftingGridListener;
import com.refinedmods.refinedstorage.container.GridContainer;
import com.refinedmods.refinedstorage.tile.grid.GridTile;
import java.util.Optional;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.ICraftingRecipe;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.item.crafting.RecipeManager;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import top.theillusivec4.polymorph.common.crafting.RecipeSelection;

public class RefinedStorageHooks {

  public static boolean loaded = false;

  public static void appendPattern(boolean exactPattern, ItemStack stack, BlockPos pos,
                                   World world) {

    if (exactPattern) {
      CompoundNBT tag = stack.getTag();

      if (tag == null) {
        stack.setTag(new CompoundNBT());
      }

      if (!world.isRemote() && loaded) {
        TileEntity te = world.getTileEntity(pos);

        if (te instanceof GridTile) {
          Optional<ICraftingRecipe> recipe = RecipeSelection.getTileEntityRecipe(IRecipeType.CRAFTING,
              ((GridTile) te).getNode().getCraftingMatrix(), world, te);
          recipe.ifPresent(
              rec -> stack.getTag().putString("PolymorphRecipe", rec.getId().toString()));
        }
      }
    }
  }

  @SuppressWarnings("unchecked")
  public static <T extends IRecipe<C>, C extends IInventory> Optional<T> getPatternRecipe(
      ItemStack stack, IRecipeType<T> type, C inventory, World world) {
    CompoundNBT tag = stack.getTag();

    if (tag != null) {
      String id = tag.getString("PolymorphRecipe");
      Optional<T> opt = (Optional<T>) world.getRecipeManager().getRecipe(new ResourceLocation(id));

      if (opt.isPresent()) {
        return opt;
      }
    }
    return world.getRecipeManager().getRecipe(type, inventory, world);
  }

  public static <T extends IRecipe<C>, C extends IInventory> Optional<T> getWirelessRecipe(
      RecipeManager unused, IRecipeType<T> type, C inventory, World world,
      ICraftingGridListener listener) {

    if (listener instanceof GridContainer) {
      GridContainer container = (GridContainer) listener;
      return RecipeSelection.getPlayerRecipe(type, inventory, world, container.getPlayer());
    }
    return world.getRecipeManager().getRecipe(type, inventory, world);
  }
}
