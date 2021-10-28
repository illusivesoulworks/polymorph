package top.theillusivec4.polymorph.common.integration.cyclic;

import com.lothrazar.cyclic.block.crafter.ContainerCrafter;
import com.lothrazar.cyclic.block.crafter.ScreenCrafter;
import com.lothrazar.cyclic.block.crafter.TileCrafter;
import com.lothrazar.cyclic.item.crafting.CraftingBagContainer;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.container.Container;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;
import top.theillusivec4.polymorph.api.PolymorphApi;
import top.theillusivec4.polymorph.api.common.base.IPolymorphCommon;
import top.theillusivec4.polymorph.common.integration.AbstractCompatibilityModule;
import top.theillusivec4.polymorph.common.util.PolymorphAccessor;

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
        return (TileCrafter) PolymorphAccessor.readField(container, "tile");
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

  @SuppressWarnings({"unchecked", "ConstantConditions"})
  @Override
  public boolean selectRecipe(TileEntity tileEntity, IRecipe<?> recipe) {

    if (tileEntity instanceof TileCrafter) {
      PolymorphAccessor.writeField(tileEntity, "lastValidRecipe", recipe);
      PolymorphAccessor.writeField(tileEntity, "recipeOutput", recipe.getRecipeOutput());
      LazyOptional<IItemHandler> preview =
          (LazyOptional<IItemHandler>) PolymorphAccessor.readField(tileEntity, "preview");

      if (preview != null) {
        PolymorphAccessor.invokeMethod(tileEntity, "setPreviewSlot", preview.orElse(null),
            recipe.getRecipeOutput());
      }
      return true;
    }
    return false;
  }
}
