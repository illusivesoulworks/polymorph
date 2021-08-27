package top.theillusivec4.polymorph.api.type;

import java.util.Optional;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public interface ITileEntityRecipeSelector {

  Optional<? extends IRecipe<?>> getRecipe(World world);

  Optional<? extends IRecipe<?>> getSelectedRecipe();

  IRecipeType<? extends IRecipe<?>> getRecipeType();

  void setSelectedRecipe(IRecipe<?> recipe);

  void setSavedRecipe(String recipe);

  TileEntity getParent();

  CompoundNBT writeNBT();

  void readNBT(CompoundNBT nbt);
}
