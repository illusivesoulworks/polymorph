package top.theillusivec4.polymorph.common.capability;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nonnull;
import net.minecraft.item.crafting.AbstractCookingRecipe;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import top.theillusivec4.polymorph.api.type.IPersistentSelector;

public class PersistentSelector implements IPersistentSelector {

  private final TileEntity parent;
  private final List<AbstractCookingRecipe> recipes = new ArrayList<>();

  private ResourceLocation recipeKey;
  private AbstractCookingRecipe selectedRecipe;

  public PersistentSelector(TileEntity tileEntity) {
    this.parent = tileEntity;
  }

  @Override
  public ResourceLocation getRecipeKey() {
    return null;
  }

  @Override
  public List<AbstractCookingRecipe> getRecipes() {
    return null;
  }

  @Override
  public void setRecipes(List<AbstractCookingRecipe> recipes) {

  }

  @Nonnull
  @Override
  public AbstractCookingRecipe getSelectedRecipe() {
    return null;
  }

  @Override
  public TileEntity getParent() {
    return null;
  }
}
