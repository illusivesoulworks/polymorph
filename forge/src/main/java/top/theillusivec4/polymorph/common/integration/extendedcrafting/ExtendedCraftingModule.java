package top.theillusivec4.polymorph.common.integration.extendedcrafting;

import com.blakebr0.extendedcrafting.api.crafting.ITableRecipe;
import com.blakebr0.extendedcrafting.client.screen.BasicAutoTableScreen;
import com.blakebr0.extendedcrafting.container.BasicAutoTableContainer;
import com.blakebr0.extendedcrafting.container.BasicTableContainer;
import com.blakebr0.extendedcrafting.container.inventory.ExtendedCraftingInventory;
import com.blakebr0.extendedcrafting.tileentity.AutoTableTileEntity;
import javax.annotation.Nonnull;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.level.block.entity.BlockEntity;
import top.theillusivec4.polymorph.api.PolymorphApi;
import top.theillusivec4.polymorph.api.client.base.IPolymorphClient;
import top.theillusivec4.polymorph.api.common.base.IPolymorphCommon;
import top.theillusivec4.polymorph.common.integration.AbstractCompatibilityModule;
import top.theillusivec4.polymorph.mixin.integration.extendedcrafting.AccessorAutoTableContainer;
import top.theillusivec4.polymorph.mixin.integration.extendedcrafting.AccessorAutoTableTileEntity;

public class ExtendedCraftingModule extends AbstractCompatibilityModule {

  private static final AbstractContainerMenu EMPTY_CONTAINER = new AbstractContainerMenu(null, -1) {
    public boolean stillValid(@Nonnull Player player) {
      return false;
    }
  };

  @Override
  public void setup() {
    IPolymorphCommon commonApi = PolymorphApi.common();
    commonApi.registerTileEntity2RecipeData(tileEntity -> {
      if (tileEntity instanceof AutoTableTileEntity.Basic tableTile) {
        return new AutoTableRecipeData(tableTile);
      }
      return null;
    });
    commonApi.registerContainer2TileEntity(container -> {
      if (container instanceof BasicAutoTableContainer table) {
        AccessorAutoTableContainer access = (AccessorAutoTableContainer) table;
        return access.getWorld().getBlockEntity(access.getPos());
      }
      return null;
    });
  }

  @Override
  public void clientSetup() {
    IPolymorphClient clientApi = PolymorphApi.client();
    clientApi.registerWidget(containerScreen -> {
      if (containerScreen instanceof BasicAutoTableScreen &&
          containerScreen.getMenu() instanceof BasicAutoTableContainer) {
        return clientApi.findCraftingResultSlot(containerScreen).map(
            slot -> new AutoTableRecipesWidget(containerScreen, slot)).orElse(null);
      }
      return null;
    });
  }

  @Override
  public boolean openContainer(AbstractContainerMenu container, ServerPlayer serverPlayerEntity) {

    if (container instanceof BasicTableContainer table) {
      PolymorphApi.common().getRecipeData(serverPlayerEntity)
          .ifPresent(recipeData -> {
            ExtendedCraftingInventory inv = getMatrix(table);

            if (inv != null) {
              container.slotsChanged(inv);
            }
          });
      return true;
    }
    return false;
  }

  @Override
  public boolean selectRecipe(AbstractContainerMenu container, Recipe<?> recipe) {

    if (container instanceof BasicTableContainer || container instanceof BasicAutoTableContainer) {
      ExtendedCraftingInventory inv = getMatrix(container);

      if (inv != null) {
        container.slotsChanged(inv);
      }
      return true;
    }
    return false;
  }

  @Override
  public boolean selectRecipe(BlockEntity tileEntity, Recipe<?> recipe) {

    if (tileEntity instanceof AutoTableTileEntity.Basic basic) {
      AutoTableTileEntity.WrappedRecipe wrappedRecipe = null;

      if (recipe instanceof ITableRecipe tableRecipe) {
        wrappedRecipe = new AutoTableTileEntity.WrappedRecipe(tableRecipe);
      } else if (recipe instanceof CraftingRecipe craftingRecipe) {
        ExtendedCraftingInventory craftingInventory =
            new ExtendedCraftingInventory(EMPTY_CONTAINER, basic.getRecipeInventory(), 3);
        wrappedRecipe = new AutoTableTileEntity.WrappedRecipe(craftingRecipe, craftingInventory);
      }

      if (wrappedRecipe != null) {
        ((AccessorAutoTableTileEntity) basic).setRecipe(wrappedRecipe);
        PolymorphApi.common().getPacketDistributor()
            .sendBlockEntitySyncS2C(basic.getBlockPos(), recipe.getId());
      }
    }
    return false;
  }

  private static ExtendedCraftingInventory getMatrix(AbstractContainerMenu container) {

    for (Slot slot : container.slots) {

      if (slot.container instanceof ExtendedCraftingInventory inv) {
        return inv;
      }
    }
    return null;
  }
}
