package top.theillusivec4.polymorph.common.integration.extendedcrafting;

import com.blakebr0.extendedcrafting.api.crafting.ITableRecipe;
import com.blakebr0.extendedcrafting.client.screen.BasicAutoTableScreen;
import com.blakebr0.extendedcrafting.container.BasicAutoTableContainer;
import com.blakebr0.extendedcrafting.container.BasicTableContainer;
import com.blakebr0.extendedcrafting.container.inventory.ExtendedCraftingInventory;
import com.blakebr0.extendedcrafting.tileentity.AutoTableTileEntity;
import javax.annotation.Nonnull;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.crafting.ICraftingRecipe;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.tileentity.TileEntity;
import top.theillusivec4.polymorph.api.PolymorphApi;
import top.theillusivec4.polymorph.api.client.base.IPolymorphClient;
import top.theillusivec4.polymorph.api.common.base.IPolymorphCommon;
import top.theillusivec4.polymorph.common.integration.AbstractCompatibilityModule;
import top.theillusivec4.polymorph.mixin.integration.extendedcrafting.AccessorAutoTableContainer;
import top.theillusivec4.polymorph.mixin.integration.extendedcrafting.AccessorAutoTableTileEntity;

public class ExtendedCraftingModule extends AbstractCompatibilityModule {

  private static final Container EMPTY_CONTAINER = new Container(null, -1) {
    @Override
    public boolean canInteractWith(@Nonnull PlayerEntity playerIn) {
      return false;
    }
  };

  @Override
  public void setup() {
    IPolymorphCommon commonApi = PolymorphApi.common();
    commonApi.registerTileEntity2RecipeData(tileEntity -> {
      if (tileEntity instanceof AutoTableTileEntity.Basic) {
        return new AutoTableRecipeData((AutoTableTileEntity.Basic) tileEntity);
      }
      return null;
    });
    commonApi.registerContainer2TileEntity(container -> {
      if (container instanceof BasicAutoTableContainer) {
        AccessorAutoTableContainer access = (AccessorAutoTableContainer) container;
        return access.getWorld().getTileEntity(access.getPos());
      }
      return null;
    });
  }

  @Override
  public void clientSetup() {
    IPolymorphClient clientApi = PolymorphApi.client();
    clientApi.registerWidget(containerScreen -> {
      if (containerScreen instanceof BasicAutoTableScreen &&
          containerScreen.getContainer() instanceof BasicAutoTableContainer) {
        return clientApi.findCraftingResultSlot(containerScreen).map(
            slot -> new AutoTableRecipesWidget(containerScreen, slot)).orElse(null);
      }
      return null;
    });
  }

  @Override
  public boolean openContainer(Container container, ServerPlayerEntity serverPlayerEntity) {

    if (container instanceof BasicTableContainer) {
      PolymorphApi.common().getRecipeData(serverPlayerEntity)
          .ifPresent(recipeData -> {
            ExtendedCraftingInventory inv = getMatrix(container);

            if (inv != null) {
              container.onCraftMatrixChanged(inv);
            }
          });
      return true;
    }
    return false;
  }

  @Override
  public boolean selectRecipe(Container container, IRecipe<?> recipe) {

    if (container instanceof BasicTableContainer || container instanceof BasicAutoTableContainer) {
      ExtendedCraftingInventory inv = getMatrix(container);

      if (inv != null) {
        container.onCraftMatrixChanged(inv);
      }
      return true;
    }
    return false;
  }

  @Override
  public boolean selectRecipe(TileEntity tileEntity, IRecipe<?> recipe) {

    if (tileEntity instanceof AutoTableTileEntity.Basic) {
      AutoTableTileEntity.Basic basic = (AutoTableTileEntity.Basic) tileEntity;
      AutoTableTileEntity.WrappedRecipe wrappedRecipe = null;

      if (recipe instanceof ITableRecipe) {
        wrappedRecipe = new AutoTableTileEntity.WrappedRecipe((ITableRecipe) recipe);
      } else if (recipe instanceof ICraftingRecipe) {
        ExtendedCraftingInventory craftingInventory =
            new ExtendedCraftingInventory(EMPTY_CONTAINER, basic.getRecipeInventory(), 3);
        wrappedRecipe =
            new AutoTableTileEntity.WrappedRecipe((ICraftingRecipe) recipe, craftingInventory);
      }

      if (wrappedRecipe != null) {
        ((AccessorAutoTableTileEntity) basic).setRecipe(wrappedRecipe);
        PolymorphApi.common().getPacketDistributor()
            .sendBlockEntitySyncS2C(basic.getPos(), recipe.getId());
      }
    }
    return false;
  }

  private static ExtendedCraftingInventory getMatrix(Container container) {

    for (Slot slot : container.inventorySlots) {

      if (slot.inventory instanceof ExtendedCraftingInventory) {
        return (ExtendedCraftingInventory) slot.inventory;
      }
    }
    return null;
  }
}
