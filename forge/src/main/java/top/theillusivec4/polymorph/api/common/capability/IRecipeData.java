package top.theillusivec4.polymorph.api.common.capability;

import java.util.List;
import java.util.Optional;
import java.util.SortedSet;
import javax.annotation.Nonnull;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.world.World;
import top.theillusivec4.polymorph.api.common.base.IRecipePair;

public interface IRecipeData<E> {

  <T extends IRecipe<C>, C extends IInventory> Optional<T> getRecipe(IRecipeType<T> pType,
                                                                     C pInventory,
                                                                     World pWorld,
                                                                     List<T> pRecipes);

  Optional<? extends IRecipe<?>> getSelectedRecipe();

  void setSelectedRecipe(@Nonnull IRecipe<?> pRecipe);

  @Nonnull
  SortedSet<IRecipePair> getRecipesList();

  void setRecipeDataset(@Nonnull SortedSet<IRecipePair> pData);

  boolean isEmpty(IInventory pInventory);

  E getOwner();

  CompoundNBT writeNBT();

  void readNBT(CompoundNBT pCompound);
}
