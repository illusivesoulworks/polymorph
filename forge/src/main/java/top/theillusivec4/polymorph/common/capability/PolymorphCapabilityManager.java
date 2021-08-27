package top.theillusivec4.polymorph.common.capability;

import java.util.Optional;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import top.theillusivec4.polymorph.api.type.ITileEntityRecipeSelector;

public class PolymorphCapabilityManager {

  public static void register() {
    CapabilityManager.INSTANCE.register(ITileEntityRecipeSelector.class,
        new Capability.IStorage<ITileEntityRecipeSelector>() {

          @Override
          public INBT writeNBT(Capability<ITileEntityRecipeSelector> capability,
                               ITileEntityRecipeSelector instance,
                               Direction side) {
            return instance.writeNBT();
          }

          @Override
          public void readNBT(Capability<ITileEntityRecipeSelector> capability,
                              ITileEntityRecipeSelector instance,
                              Direction side, INBT nbt) {
            instance.readNBT((CompoundNBT) nbt);
          }
        }, EmptySelector::new);
  }

  public static class EmptySelector implements ITileEntityRecipeSelector {

    @Override
    public Optional<? extends IRecipe<?>> getRecipe(World world) {
      return Optional.empty();
    }

    @Override
    public Optional<? extends IRecipe<?>> getSelectedRecipe() {
      return Optional.empty();
    }

    @Override
    public IRecipeType<? extends IRecipe<?>> getRecipeType() {
      return null;
    }

    @Override
    public void setSelectedRecipe(IRecipe<?> recipe) {

    }

    @Override
    public void setSavedRecipe(String recipe) {

    }

    @Override
    public TileEntity getParent() {
      return null;
    }

    @Override
    public CompoundNBT writeNBT() {
      return null;
    }

    @Override
    public void readNBT(CompoundNBT nbt) {

    }
  }
}
