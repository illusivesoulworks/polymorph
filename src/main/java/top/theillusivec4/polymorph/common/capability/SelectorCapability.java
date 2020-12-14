package top.theillusivec4.polymorph.common.capability;

import java.util.List;
import java.util.Optional;
import javax.annotation.Nonnull;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.container.AbstractFurnaceContainer;
import net.minecraft.inventory.container.BlastFurnaceContainer;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.Slot;
import net.minecraft.inventory.container.SmokerContainer;
import net.minecraft.item.crafting.AbstractCookingRecipe;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.item.crafting.RecipeManager;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.tileentity.AbstractFurnaceTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import top.theillusivec4.polymorph.api.PolymorphApi;
import top.theillusivec4.polymorph.api.type.IFurnaceProvider;
import top.theillusivec4.polymorph.api.type.IPersistentSelector;

public class SelectorCapability {

  public static void register() {
    PolymorphApi.getInstance().addEntityProvider(tileEntity -> {
      if (tileEntity instanceof AbstractFurnaceTileEntity) {
        return new FurnaceSelector((AbstractFurnaceTileEntity) tileEntity);
      }
      return null;
    }, container -> {
      if (container instanceof AbstractFurnaceContainer) {
        return new SimpleFurnaceProvider(container);
      }
      return null;
    });
    CapabilityManager.INSTANCE.register(IPersistentSelector.class,
        new Capability.IStorage<IPersistentSelector>() {
          @Nonnull
          @Override
          public INBT writeNBT(Capability<IPersistentSelector> capability,
                               IPersistentSelector instance, Direction side) {
            CompoundNBT nbt = new CompoundNBT();
            instance.getSelectedRecipe()
                .ifPresent(recipe -> nbt.putString("SelectedRecipe", recipe.getId().toString()));
            return nbt;
          }

          @Override
          public void readNBT(Capability<IPersistentSelector> capability,
                              IPersistentSelector instance, Direction side, INBT nbt) {
            if (nbt instanceof CompoundNBT) {
              CompoundNBT tag = (CompoundNBT) nbt;

              if (tag.contains("SelectedRecipe")) {
                instance.setSavedRecipe(tag.getString("SelectedRecipe"));
              }
            }
          }
        }, DefaultSelector::new);
  }

  private static class SimpleFurnaceProvider implements IFurnaceProvider {

    final Container container;
    final IInventory input;
    final IRecipeType<? extends AbstractCookingRecipe> recipeType;

    public SimpleFurnaceProvider(Container container) {
      this.container = container;
      this.input = container.inventorySlots.get(0).inventory;
      this.recipeType = this.getRecipeType();
    }

    private IRecipeType<? extends AbstractCookingRecipe> getRecipeType() {

      if (this.container instanceof SmokerContainer) {
        return IRecipeType.SMOKING;
      } else if (this.container instanceof BlastFurnaceContainer) {
        return IRecipeType.BLASTING;
      } else {
        return IRecipeType.SMELTING;
      }
    }

    @Nonnull
    @Override
    public Container getContainer() {
      return this.container;
    }

    @Nonnull
    @Override
    public IInventory getInventory() {
      return this.input;
    }

    @Nonnull
    @Override
    public List<? extends AbstractCookingRecipe> getRecipes(World world,
                                                            RecipeManager recipeManager) {
      return recipeManager.getRecipes(this.recipeType, this.getInventory(), world);
    }

    @Nonnull
    @Override
    public Slot getOutputSlot() {
      return this.container.inventorySlots.get(2);
    }
  }

  private static class DefaultSelector implements IPersistentSelector {

    @Override
    public Optional<IRecipe<?>> fetchRecipe(World world) {
      return Optional.empty();
    }

    @Override
    public IRecipeType<? extends IRecipe<?>> getRecipeType() {
      return null;
    }

    @Nonnull
    @Override
    public Optional<IRecipe<?>> getSelectedRecipe() {
      return Optional.empty();
    }

    @Override
    public void setSavedRecipe(String recipe) {

    }

    @Override
    public void setSelectedRecipe(IRecipe<?> recipe) {

    }

    @Override
    public TileEntity getParent() {
      return null;
    }
  }
}
