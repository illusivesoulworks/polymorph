package top.theillusivec4.polymorph.common.capability;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.util.LazyOptional;
import top.theillusivec4.polymorph.api.PolymorphApi;
import top.theillusivec4.polymorph.api.common.base.IRecipeData;
import top.theillusivec4.polymorph.api.common.capability.IRecipeDataset;
import top.theillusivec4.polymorph.api.common.capability.IRecipeProcessor;

public class PolymorphCapabilities {

  @CapabilityInject(IRecipeProcessor.class)
  public static final Capability<IRecipeProcessor> RECIPE_PROCESSOR;
  @CapabilityInject(IRecipeDataset.class)
  public static final Capability<IRecipeDataset> RECIPE_DATASET;

  public static final ResourceLocation RECIPE_PROCESSOR_ID =
      new ResourceLocation(PolymorphApi.MOD_ID, "recipe_processor");
  public static final ResourceLocation RECIPE_DATASET_ID =
      new ResourceLocation(PolymorphApi.MOD_ID, "recipe_dataset");

  public static LazyOptional<IRecipeProcessor> getController(TileEntity te) {
    return te.getCapability(RECIPE_PROCESSOR);
  }

  public static LazyOptional<IRecipeDataset> getRecipeDataCache(TileEntity te) {
    return te.getCapability(RECIPE_DATASET);
  }

  static {
    RECIPE_PROCESSOR = null;
    RECIPE_DATASET = null;
  }

  public static void register() {
    CapabilityManager manager = CapabilityManager.INSTANCE;
    manager.register(IRecipeProcessor.class, new Capability.IStorage<IRecipeProcessor>() {
      @Nullable
      @Override
      public INBT writeNBT(Capability<IRecipeProcessor> capability, IRecipeProcessor instance,
                           Direction side) {
        return instance.writeNBT();
      }

      @Override
      public void readNBT(Capability<IRecipeProcessor> capability, IRecipeProcessor instance,
                          Direction side, INBT nbt) {
        instance.readNBT((CompoundNBT) nbt);
      }
    }, EmptyProcessor::new);
    manager.register(IRecipeDataset.class, new Capability.IStorage<IRecipeDataset>() {
      @Nullable
      @Override
      public INBT writeNBT(Capability<IRecipeDataset> capability, IRecipeDataset instance,
                           Direction side) {
        return null;
      }

      @Override
      public void readNBT(Capability<IRecipeDataset> capability, IRecipeDataset instance,
                          Direction side, INBT nbt) {

      }
    }, EmptyDataset::new);
  }

  public static class EmptyProcessor implements IRecipeProcessor {

    @Override
    public Optional<? extends IRecipe<?>> getSelectedRecipe() {
      return Optional.empty();
    }

    @Override
    public void setSelectedRecipe(IRecipe<?> recipe, PlayerEntity selectingPlayer) {

    }

    @Override
    public Optional<? extends IRecipe<?>> getRecipe(World world) {
      return Optional.empty();
    }

    @Nonnull
    @Override
    public Set<IRecipeData> getRecipeDataset() {
      return new HashSet<>();
    }

    @Override
    public void saveRecipeDataset(Set<IRecipeData> pData) {

    }

    @Override
    public TileEntity getTileEntity() {
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

  public static class EmptyDataset implements IRecipeDataset {

    @Nonnull
    @Override
    public Set<IRecipeData> getRecipeDataset() {
      return new HashSet<>();
    }

    @Override
    public void saveRecipeDataset(Set<IRecipeData> pData) {

    }
  }
}
