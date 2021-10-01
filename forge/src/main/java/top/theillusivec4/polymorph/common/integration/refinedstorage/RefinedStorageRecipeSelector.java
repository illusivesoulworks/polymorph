package top.theillusivec4.polymorph.common.integration.refinedstorage;

import com.refinedmods.refinedstorage.tile.grid.GridTile;
import java.util.Comparator;
import java.util.Optional;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.item.crafting.ICraftingRecipe;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Util;
import net.minecraft.world.World;
import org.apache.commons.lang3.reflect.FieldUtils;
import top.theillusivec4.polymorph.api.type.ITileEntityRecipeSelector;
import top.theillusivec4.polymorph.common.PolymorphMod;

public class RefinedStorageRecipeSelector implements ITileEntityRecipeSelector {

  private final GridTile parent;

  protected ICraftingRecipe selectedRecipe;
  protected String savedRecipe = "";

  public RefinedStorageRecipeSelector(GridTile tileEntity) {
    this.parent = tileEntity;
  }

  @Override
  public Optional<? extends IRecipe<?>> getRecipe(World world) {
    CraftingInventory inv = this.parent.getNode().getCraftingMatrix();

    if (inv == null) {
      return Optional.empty();
    }

    if (!savedRecipe.isEmpty()) {
      Optional<IRecipe<?>> saved =
          (Optional<IRecipe<?>>) world.getRecipeManager()
              .getRecipe(new ResourceLocation(savedRecipe));

      if (!saved.isPresent() || !((IRecipe<CraftingInventory>) saved.get()).matches(inv, world)) {
        savedRecipe = "";
      } else {
        this.setSelectedRecipe(saved.get());
        savedRecipe = "";
        return saved;
      }
    }
    return world.getRecipeManager().getRecipes().stream()
        .filter((val) -> val.getType() == this.getRecipeType()).flatMap(
            (val) -> Util.streamOptional(
                this.getRecipeType().matches((IRecipe<CraftingInventory>) val, world, inv)))
        .min(Comparator.comparing((recipe) -> recipe.getRecipeOutput().getTranslationKey()))
        .<IRecipe<?>>map((val) -> {
          this.setSelectedRecipe(val);
          return val;
        });
  }

  @Override
  public Optional<ICraftingRecipe> getSelectedRecipe() {
    return Optional.ofNullable(selectedRecipe);
  }

  @Override
  public IRecipeType<ICraftingRecipe> getRecipeType() {
    return IRecipeType.CRAFTING;
  }

  @Override
  public void setSelectedRecipe(IRecipe<?> recipe) {

    if (recipe instanceof ICraftingRecipe) {
      this.selectedRecipe = (ICraftingRecipe) recipe;

      try {
        FieldUtils.writeField(this.parent.getNode(), "currentRecipe", this.selectedRecipe, true);
      } catch (IllegalAccessException e) {
        PolymorphMod.LOGGER.error("Error accessing currentRecipe from Refined Storage!");
      } catch (IllegalArgumentException e) {
        PolymorphMod.LOGGER.debug("Cannot find Refined Storage, skipping field override!");
      }
    }
  }

  @Override
  public void setSavedRecipe(String recipe) {
    this.savedRecipe = recipe;
  }

  @Override
  public TileEntity getParent() {
    return this.parent;
  }

  @Override
  public void readNBT(CompoundNBT nbtCompound) {

    if (nbtCompound.contains("SelectedRecipe")) {
      this.setSavedRecipe(nbtCompound.getString("SelectedRecipe"));
    }
  }

  @Override
  public CompoundNBT writeNBT() {
    CompoundNBT nbt = new CompoundNBT();

    if (this.selectedRecipe != null) {
      nbt.putString("SelectedRecipe", this.selectedRecipe.getId().toString());
    }
    return nbt;
  }
}
