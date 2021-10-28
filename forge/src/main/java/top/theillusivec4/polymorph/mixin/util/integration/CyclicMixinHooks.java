package top.theillusivec4.polymorph.mixin.util.integration;

import java.util.ArrayList;
import java.util.Optional;
import javax.annotation.Nonnull;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import top.theillusivec4.polymorph.common.crafting.RecipeSelection;

public class CyclicMixinHooks {

  @SuppressWarnings("unchecked")
  public static <T extends IRecipe<C>, C extends IInventory> Optional<T> getRecipe(
      ArrayList<ItemStack> stacks, World worldIn, TileEntity te) {

    if (worldIn != null && worldIn.getServer() != null) {
      CraftingInventory craftingInventory = new CraftingInventory(new FakeContainer(ContainerType.CRAFTING, 18291239), 3, 3);

      for (int i = 0; i < 3; i++) {

        for (int j = 0; j < 3; j++) {
          int indexInArray = i + j * 3;
          ItemStack stack = stacks.get(indexInArray);
          craftingInventory.setInventorySlotContents(indexInArray, stack.copy());
        }
      }
      return (Optional<T>) RecipeSelection.getTileEntityRecipe(IRecipeType.CRAFTING,
          craftingInventory, worldIn, te);
    } else {
      return Optional.empty();
    }
  }

  private static class FakeContainer extends Container {

    protected FakeContainer(ContainerType<?> type, int id) {
      super(type, id);
    }

    public boolean canInteractWith(@Nonnull PlayerEntity playerIn) {
      return true;
    }
  }
}
