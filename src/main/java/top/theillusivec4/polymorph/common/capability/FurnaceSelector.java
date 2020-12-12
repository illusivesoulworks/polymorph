package top.theillusivec4.polymorph.common.capability;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import javax.annotation.Nonnull;
import net.minecraft.item.crafting.AbstractCookingRecipe;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.tileentity.AbstractFurnaceTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import top.theillusivec4.polymorph.api.type.IPersistentSelector;

public class FurnaceSelector implements IPersistentSelector {

  private final AbstractFurnaceTileEntity parent;
  private final List<AbstractCookingRecipe> recipes = new ArrayList<>();

  private ResourceLocation recipeKey;
  private AbstractCookingRecipe selectedRecipe;
  private AbstractCookingRecipe lastRecipe;

  public FurnaceSelector(AbstractFurnaceTileEntity tileEntity) {
    this.parent = tileEntity;
  }

  @Override
  public ResourceLocation getRecipeKey() {
    return null;
  }

  @Override
  public List<IRecipe<?>> getRecipes() {
    return null;
  }

  @Override
  public void setRecipes(List<? extends IRecipe<?>> recipes) {

  }

  @Nonnull
  @Override
  public Optional<IRecipe<?>> getSelectedRecipe() {

    if (selectedRecipe != null && this.parent.getWorld() != null &&
        !selectedRecipe.matches(this.parent, this.parent.getWorld())) {
      this.selectedRecipe = null;
    }
    return Optional.ofNullable(selectedRecipe);
  }

  @Override
  public void setSelectedRecipe(IRecipe<?> recipe) {
    this.selectedRecipe = (AbstractCookingRecipe) recipe;
  }

  @Override
  public Optional<IRecipe<?>> getLastRecipe() {
    return Optional.ofNullable(lastRecipe);
  }

  @Override
  public void setLastRecipe(IRecipe<?> recipe) {
    this.lastRecipe = (AbstractCookingRecipe) recipe;
  }

  @Override
  public TileEntity getParent() {
    return this.parent;
  }
}
