package top.theillusivec4.polymorph.api.common.capability;

import java.util.Optional;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public interface IRecipeProcessor extends IRecipeDataset {

  Optional<? extends IRecipe<?>> getSelectedRecipe();

  void setSelectedRecipe(IRecipe<?> recipe, PlayerEntity selectingPlayer);

  Optional<? extends IRecipe<?>> getRecipe(World world);

  TileEntity getTileEntity();

  CompoundNBT writeNBT();

  void readNBT(CompoundNBT nbt);
}
