package top.theillusivec4.polymorph.common.capability;

import java.util.HashSet;
import java.util.Set;
import javax.annotation.Nonnull;
import top.theillusivec4.polymorph.api.common.base.IRecipeData;
import top.theillusivec4.polymorph.api.common.capability.IRecipeDataset;

public class SimpleRecipeDataset implements IRecipeDataset {

  private final Set<IRecipeData> recipeData;

  public SimpleRecipeDataset() {
    this.recipeData = new HashSet<>();
  }

  @Nonnull
  @Override
  public Set<IRecipeData> getRecipeDataset() {
    return this.recipeData;
  }

  @Override
  public void saveRecipeDataset(Set<IRecipeData> pData) {
    this.recipeData.clear();
    this.recipeData.addAll(pData);
  }
}
