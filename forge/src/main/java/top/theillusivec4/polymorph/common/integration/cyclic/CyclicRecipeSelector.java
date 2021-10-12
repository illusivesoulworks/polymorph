package top.theillusivec4.polymorph.common.integration.cyclic;

import com.lothrazar.cyclic.block.crafter.TileCrafter;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.item.crafting.ICraftingRecipe;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Util;
import net.minecraft.world.World;
import top.theillusivec4.polymorph.api.type.ITileEntityRecipeSelector;

public class CyclicRecipeSelector implements ITileEntityRecipeSelector {

  private final TileCrafter parent;

  protected ICraftingRecipe selectedRecipe;
  protected String savedRecipe = "";
  protected CraftingInventory craftingInventory = null;
  protected Set<ResourceLocation> lastRecipes = new HashSet<>();

  public CyclicRecipeSelector(TileCrafter tileEntity) {
    this.parent = tileEntity;
  }

  public Set<ResourceLocation> getLastRecipes() {
    return this.lastRecipes;
  }

  public void setCraftingInventory(CraftingInventory inv) {
    this.craftingInventory = inv;
  }

  @Override
  public Optional<? extends IRecipe<?>> getRecipe(World world) {

    if (this.craftingInventory == null) {
      return Optional.empty();
    }

    if (!savedRecipe.isEmpty()) {
      Optional<IRecipe<?>> saved =
          (Optional<IRecipe<?>>) world.getRecipeManager()
              .getRecipe(new ResourceLocation(savedRecipe));

      if (!saved.isPresent() ||
          !((IRecipe<CraftingInventory>) saved.get()).matches(this.craftingInventory, world)) {
        savedRecipe = "";
      } else {
        this.setSelectedRecipe(saved.get());
        savedRecipe = "";
        return saved;
      }
    }
    List<IRecipe<?>> recipes = world.getRecipeManager().getRecipes().stream()
        .filter((val) -> val.getType() == this.getRecipeType()).flatMap(
            (val) -> Util.streamOptional(
                this.getRecipeType()
                    .matches((IRecipe<CraftingInventory>) val, world, this.craftingInventory)))
        .collect(Collectors.toList());
    this.lastRecipes = recipes.stream().map(IRecipe::getId).collect(Collectors.toSet());
    return recipes.stream()
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
