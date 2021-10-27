package top.theillusivec4.polymorph.common.capability;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.IRecipeType;
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
import top.theillusivec4.polymorph.api.common.base.IRecipePair;
import top.theillusivec4.polymorph.api.common.capability.IPlayerRecipeData;
import top.theillusivec4.polymorph.api.common.capability.IRecipeData;
import top.theillusivec4.polymorph.api.common.capability.ITileEntityRecipeData;

public class PolymorphCapabilities {

  @CapabilityInject(IPlayerRecipeData.class)
  public static final Capability<IPlayerRecipeData> PLAYER_RECIPE_DATA;

  @CapabilityInject(ITileEntityRecipeData.class)
  public static final Capability<ITileEntityRecipeData> TILE_ENTITY_RECIPE_DATA;

  static {
    PLAYER_RECIPE_DATA = null;
    TILE_ENTITY_RECIPE_DATA = null;
  }

  public static final ResourceLocation PLAYER_RECIPE_DATA_ID =
      new ResourceLocation(PolymorphApi.MOD_ID, "player_recipe_data");
  public static final ResourceLocation TILE_ENTITY_RECIPE_DATA_ID =
      new ResourceLocation(PolymorphApi.MOD_ID, "tile_entity_recipe_data");

  public static LazyOptional<IPlayerRecipeData> getRecipeData(PlayerEntity pPlayer) {
    return pPlayer.getCapability(PLAYER_RECIPE_DATA);
  }

  public static LazyOptional<ITileEntityRecipeData> getRecipeData(TileEntity pTileEntity) {
    return pTileEntity.getCapability(TILE_ENTITY_RECIPE_DATA);
  }

  public static void register() {
    CapabilityManager manager = CapabilityManager.INSTANCE;
    manager.register(
        IPlayerRecipeData.class, new Capability.IStorage<IPlayerRecipeData>() {
          @Nullable
          @Override
          public INBT writeNBT(Capability<IPlayerRecipeData> capability,
                               IPlayerRecipeData instance, Direction side) {
            return instance.writeNBT();
          }

          @Override
          public void readNBT(Capability<IPlayerRecipeData> capability,
                              IPlayerRecipeData instance, Direction side, INBT nbt) {
            instance.readNBT((CompoundNBT) nbt);
          }
        }, EmptyPlayerRecipeData::new);
    manager.register(
        ITileEntityRecipeData.class, new Capability.IStorage<ITileEntityRecipeData>() {
          @Nullable
          @Override
          public INBT writeNBT(Capability<ITileEntityRecipeData> capability,
                               ITileEntityRecipeData instance, Direction side) {
            return instance.writeNBT();
          }

          @Override
          public void readNBT(Capability<ITileEntityRecipeData> capability,
                              ITileEntityRecipeData instance, Direction side, INBT nbt) {
            instance.readNBT((CompoundNBT) nbt);
          }
        }, EmptyTileEntityRecipeData::new);
  }

  private static final class EmptyPlayerRecipeData extends EmptyRecipeData<PlayerEntity>
      implements IPlayerRecipeData {

  }

  private static final class EmptyTileEntityRecipeData extends EmptyRecipeData<TileEntity>
      implements ITileEntityRecipeData {

    @Override
    public void syncRecipesList(ServerPlayerEntity pPlayer) {

    }

    @Override
    public boolean isFailing() {
      return false;
    }

    @Override
    public void setFailing(boolean pFailing) {

    }
  }

  private static class EmptyRecipeData<E> implements IRecipeData<E> {

    @Override
    public <T extends IRecipe<C>, C extends IInventory> Optional<T> getRecipe(IRecipeType<T> pType,
                                                                              C pInventory,
                                                                              World pWorld,
                                                                              List<T> pRecipes) {
      return Optional.empty();
    }

    @Override
    public Optional<? extends IRecipe<?>> getSelectedRecipe() {
      return Optional.empty();
    }

    @Override
    public void setSelectedRecipe(@Nonnull IRecipe<?> pRecipe) {

    }

    @Nonnull
    @Override
    public Set<IRecipePair> getRecipesList() {
      return new HashSet<>();
    }

    @Override
    public void setRecipeDataset(@Nonnull Set<IRecipePair> pData) {

    }

    @Override
    public boolean isEmpty(IInventory pInventory) {
      return false;
    }

    @Override
    public E getOwner() {
      return null;
    }

    @Override
    public CompoundNBT writeNBT() {
      return null;
    }

    @Override
    public void readNBT(CompoundNBT pCompound) {

    }
  }
}
