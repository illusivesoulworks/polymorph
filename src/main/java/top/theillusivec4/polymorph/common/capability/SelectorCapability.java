package top.theillusivec4.polymorph.common.capability;

import java.util.List;
import java.util.Optional;
import javax.annotation.Nonnull;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import top.theillusivec4.polymorph.api.type.IPersistentSelector;

public class SelectorCapability {

  public static void register() {
    CapabilityManager.INSTANCE.register(IPersistentSelector.class,
        new Capability.IStorage<IPersistentSelector>() {
          @Nonnull
          @Override
          public INBT writeNBT(Capability<IPersistentSelector> capability,
                               IPersistentSelector instance, Direction side) {
            CompoundNBT nbt = new CompoundNBT();
            instance.getSelectedRecipe()
                .ifPresent(recipe -> nbt.putString("SelectedRecipe", recipe.getId().toString()));
            return nbt;
          }

          @Override
          public void readNBT(Capability<IPersistentSelector> capability,
                              IPersistentSelector instance, Direction side, INBT nbt) {
            if (nbt instanceof CompoundNBT) {
              CompoundNBT tag = (CompoundNBT) nbt;

              if (tag.contains("SelectedRecipe")) {
                instance.setSavedRecipe(tag.getString("SelectedRecipe"));
              }
            }
          }
        }, DefaultSelector::new);
  }

  private static class DefaultSelector implements IPersistentSelector {

    @Override
    public Optional<IRecipe<?>> fetchRecipe(World world) {
      return Optional.empty();
    }

    @Override
    public IRecipeType<? extends IRecipe<?>> getRecipeType() {
      return null;
    }

    @Nonnull
    @Override
    public Optional<IRecipe<?>> getSelectedRecipe() {
      return Optional.empty();
    }

    @Override
    public void setSavedRecipe(String recipe) {

    }

    @Override
    public void setSelectedRecipe(IRecipe<?> recipe) {

    }

    @Override
    public TileEntity getParent() {
      return null;
    }
  }
}
