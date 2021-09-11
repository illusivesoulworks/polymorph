package top.theillusivec4.polymorph.common.capability;

import java.util.Comparator;
import java.util.Optional;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.container.AbstractFurnaceContainer;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.AbstractCookingRecipe;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.AbstractFurnaceTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Util;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.fml.network.PacketDistributor;
import org.apache.commons.lang3.reflect.FieldUtils;
import top.theillusivec4.polymorph.api.type.ITileEntityRecipeSelector;
import top.theillusivec4.polymorph.common.PolymorphMod;
import top.theillusivec4.polymorph.common.network.PolymorphNetwork;
import top.theillusivec4.polymorph.common.network.server.SPacketHighlightRecipe;
import top.theillusivec4.polymorph.mixin.core.AccessorAbstractFurnaceContainer;
import top.theillusivec4.polymorph.mixin.core.AccessorAbstractFurnaceTileEntity;

public abstract class AbstractFurnaceRecipeSelector<T extends TileEntity & IInventory> implements
    ITileEntityRecipeSelector {

  private final T parent;

  protected AbstractCookingRecipe selectedRecipe;
  protected ItemStack lastFailedInput = ItemStack.EMPTY;
  protected String savedRecipe = "";

  public AbstractFurnaceRecipeSelector(T tileEntity) {
    this.parent = tileEntity;
  }

  @SuppressWarnings("unchecked")
  @Override
  public Optional<IRecipe<?>> getRecipe(World world) {
    ItemStack input = parent.getStackInSlot(0);

    if (input == lastFailedInput) {
      return Optional.empty();
    }

    if (!savedRecipe.isEmpty()) {
      Optional<IRecipe<?>> saved =
          (Optional<IRecipe<?>>) world.getRecipeManager()
              .getRecipe(new ResourceLocation(savedRecipe));

      if (!saved.isPresent() || !((IRecipe<IInventory>) saved.get()).matches(parent, world)) {
        savedRecipe = "";
      } else {
        this.setSelectedRecipe(saved.get());
        savedRecipe = "";
        return saved;
      }
    }
    Optional<IRecipe<?>> maybeRecipe = world.getRecipeManager().getRecipes().stream()
        .filter((val) -> val.getType() == this.getRecipeType()).flatMap((val) -> Util
            .streamOptional(this.getRecipeType().matches((IRecipe<IInventory>) val, world, parent)))
        .min(Comparator.comparing((recipe) -> recipe.getRecipeOutput().getTranslationKey()))
        .map((val) -> {
          this.setSelectedRecipe(val);
          return val;
        });

    if (!maybeRecipe.isPresent()) {
      lastFailedInput = input;
    }
    return maybeRecipe;
  }

  @Override
  public Optional<? extends AbstractCookingRecipe> getSelectedRecipe() {
    return Optional.ofNullable(selectedRecipe);
  }

  @Override
  public IRecipeType<? extends AbstractCookingRecipe> getRecipeType() {

    if (this.parent instanceof AbstractFurnaceTileEntity) {
      return ((AccessorAbstractFurnaceTileEntity) this.parent).getRecipeType();
    }
    return IRecipeType.SMELTING;
  }

  @Override
  public void setSelectedRecipe(IRecipe<?> recipe) {

    if (recipe instanceof AbstractCookingRecipe) {
      this.selectedRecipe = (AbstractCookingRecipe) recipe;
      World world = this.parent.getWorld();

      if (this.parent instanceof AbstractFurnaceTileEntity && PolymorphMod.isFastFurnaceLoaded) {
        try {
          FieldUtils.writeField(this.parent, "curRecipe", this.selectedRecipe, true);
        } catch (IllegalAccessException e) {
          PolymorphMod.LOGGER.error("Error accessing cachedRecipe from FastFurnace!");
        } catch (IllegalArgumentException e) {
          PolymorphMod.LOGGER.debug("Cannot find FastFurnace, skipping field override!");
        }
      }

      if (world instanceof ServerWorld) {
        ((ServerWorld) world).getPlayers().forEach(player -> {
          if (player.openContainer instanceof AbstractFurnaceContainer &&
              ((AccessorAbstractFurnaceContainer) player.openContainer).getFurnaceInventory() ==
                  this.parent) {
            PolymorphNetwork.INSTANCE.send(PacketDistributor.PLAYER.with(() -> player),
                new SPacketHighlightRecipe(recipe.getId()));
          }
        });
      }
    }
  }

  @Override
  public void setSavedRecipe(String recipe) {
    this.savedRecipe = recipe;
  }

  @Override
  public TileEntity getParent() {
    return this.parent;
  }

  @Override
  public void readNBT(CompoundNBT nbtCompound) {

    if (nbtCompound.contains("SelectedRecipe")) {
      this.setSavedRecipe(nbtCompound.getString("SelectedRecipe"));
    }
  }

  @Override
  public CompoundNBT writeNBT() {
    CompoundNBT nbt = new CompoundNBT();

    if (this.selectedRecipe != null) {
      nbt.putString("SelectedRecipe", this.selectedRecipe.getId().toString());
    }
    return nbt;
  }
}
