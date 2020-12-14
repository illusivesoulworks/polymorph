package top.theillusivec4.polymorph.api.type;

import java.util.List;
import java.util.Optional;
import javax.annotation.Nonnull;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.crafting.AbstractCookingRecipe;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

public interface IPersistentSelector {

  ResourceLocation getRecipeKey();

  List<IRecipe<?>> getRecipes();

  void fetchRecipes();

  void setRecipes(List<? extends IRecipe<?>> recipes);

  @Nonnull
  Optional<? extends IRecipe<?>> getSelectedRecipe();

  void setSelectedRecipe(IRecipe<?> recipe);

  Optional<IRecipe<?>> getLastRecipe();

  void setLastRecipe(IRecipe<?> recipe);

  List<? extends IRecipe<?>> getLastRecipes();

  void setLastRecipes(List<? extends IRecipe<?>> recipes);

  TileEntity getParent();
}
