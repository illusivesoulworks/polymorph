package top.theillusivec4.polymorph.api.type;

import java.util.List;
import javax.annotation.Nonnull;
import net.minecraft.item.crafting.AbstractCookingRecipe;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;

public interface IPersistentSelector {

  ResourceLocation getRecipeKey();

  List<AbstractCookingRecipe> getRecipes();

  void setRecipes(List<AbstractCookingRecipe> recipes);

  @Nonnull
  AbstractCookingRecipe getSelectedRecipe();

  TileEntity getParent();
}
