package top.theillusivec4.polymorph.common.integration.cyclic;

import com.lothrazar.cyclic.block.crafter.ContainerCrafter;
import com.lothrazar.cyclic.block.crafter.ScreenCrafter;
import com.lothrazar.cyclic.block.crafter.TileCrafter;
import com.lothrazar.cyclic.item.crafting.CraftingBagContainer;
import java.util.ArrayList;
import java.util.Optional;
import javax.annotation.Nonnull;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;
import top.theillusivec4.polymorph.api.PolymorphApi;
import top.theillusivec4.polymorph.api.common.base.IPolymorphCommon;
import top.theillusivec4.polymorph.common.crafting.RecipeSelection;
import top.theillusivec4.polymorph.common.integration.AbstractCompatibilityModule;
import top.theillusivec4.polymorph.mixin.integration.cyclic.AccessorContainerCrafter;
import top.theillusivec4.polymorph.mixin.integration.cyclic.AccessorTileCrafter;

public class CyclicModule extends AbstractCompatibilityModule {

  @Override
  public void setup() {
    IPolymorphCommon commonApi = PolymorphApi.common();
    commonApi.registerTileEntity2RecipeData(tileEntity -> {
      if (tileEntity instanceof TileCrafter) {
        return new TileCrafterRecipeData((TileCrafter) tileEntity);
      }
      return null;
    });
    commonApi.registerContainer2TileEntity(container -> {
      if (container instanceof ContainerCrafter) {
        return ((AccessorContainerCrafter) container).getTile();
      }
      return null;
    });
  }

  @Override
  public void clientSetup() {
    PolymorphApi.client().registerWidget(containerScreen -> {
      if (containerScreen instanceof ScreenCrafter &&
          containerScreen.getContainer() instanceof ContainerCrafter) {
        ContainerCrafter containerCrafter = (ContainerCrafter) containerScreen.getContainer();
        return new CrafterRecipesWidget(containerScreen, containerCrafter);
      }
      return null;
    });
  }

  @Override
  public boolean openContainer(Container container, ServerPlayerEntity serverPlayerEntity) {

    if (container instanceof CraftingBagContainer) {
      PolymorphApi.common().getRecipeData(serverPlayerEntity)
          .ifPresent(recipeData -> container.onCraftMatrixChanged(null));
      return true;
    }
    return false;
  }

  @SuppressWarnings("ConstantConditions")
  @Override
  public boolean selectRecipe(TileEntity tileEntity, IRecipe<?> recipe) {

    if (tileEntity instanceof TileCrafter) {
      AccessorTileCrafter tileCrafter = (AccessorTileCrafter) tileEntity;
      tileCrafter.setLastValidRecipe(recipe);
      tileCrafter.setRecipeOutput(recipe.getRecipeOutput());
      LazyOptional<IItemHandler> preview = tileCrafter.getPreview();

      if (preview != null) {
        tileCrafter.callSetPreviewSlot(preview.orElse(null), recipe.getRecipeOutput());
      }
      return true;
    }
    return false;
  }

  @SuppressWarnings("unchecked")
  public static <T extends IRecipe<C>, C extends IInventory> Optional<T> getRecipe(
      ArrayList<ItemStack> stacks, World worldIn, TileEntity te) {

    if (worldIn != null && worldIn.getServer() != null) {
      CraftingInventory craftingInventory =
          new CraftingInventory(new FakeContainer(ContainerType.CRAFTING, 18291239), 3, 3);

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
