package top.theillusivec4.polymorph.api.type;

import java.util.List;
import java.util.Optional;
import javax.annotation.Nonnull;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.AbstractCookingRecipe;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

public interface IPersistentSelector {

  Optional<IRecipe<?>> fetchRecipe(World world);

  IRecipeType<? extends IRecipe<?>> getRecipeType();

  @Nonnull
  Optional<? extends IRecipe<?>> getSelectedRecipe();

  void setSavedRecipe(String recipe);

  void setSelectedRecipe(IRecipe<?> recipe);

  TileEntity getParent();
}
