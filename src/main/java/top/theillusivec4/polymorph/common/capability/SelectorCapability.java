package top.theillusivec4.polymorph.common.capability;

import java.util.List;
import javax.annotation.Nonnull;
import net.minecraft.item.crafting.AbstractCookingRecipe;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
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
            return new CompoundNBT();
          }

          @Override
          public void readNBT(Capability<IPersistentSelector> capability,
                              IPersistentSelector instance, Direction side, INBT nbt) {

          }
        }, DefaultSelector::new);
  }

  private static class DefaultSelector implements IPersistentSelector {

    @Override
    public ResourceLocation getRecipeKey() {
      return null;
    }

    @Override
    public List<AbstractCookingRecipe> getRecipes() {
      return null;
    }

    @Override
    public void setRecipes(List<AbstractCookingRecipe> recipes) {

    }

    @Nonnull
    @Override
    public AbstractCookingRecipe getSelectedRecipe() {
      return null;
    }

    @Override
    public TileEntity getParent() {
      return null;
    }
  }
}
