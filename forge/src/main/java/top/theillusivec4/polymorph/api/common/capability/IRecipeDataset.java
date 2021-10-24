package top.theillusivec4.polymorph.api.common.capability;

import java.util.Set;
import javax.annotation.Nonnull;
import top.theillusivec4.polymorph.api.common.base.IRecipeData;

public interface IRecipeDataset {

  @Nonnull
  Set<IRecipeData> getRecipeDataset();

  void saveRecipeDataset(Set<IRecipeData> pData);
}
