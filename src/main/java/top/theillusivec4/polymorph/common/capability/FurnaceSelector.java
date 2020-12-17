package top.theillusivec4.polymorph.common.capability;

import java.util.Comparator;
import java.util.Optional;
import javax.annotation.Nonnull;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.container.AbstractFurnaceContainer;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.AbstractCookingRecipe;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.tileentity.AbstractFurnaceTileEntity;
import net.minecraft.tileentity.BlastFurnaceTileEntity;
import net.minecraft.tileentity.SmokerTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Util;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.fml.network.PacketDistributor;
import org.apache.commons.lang3.reflect.FieldUtils;
import top.theillusivec4.polymorph.Polymorph;
import top.theillusivec4.polymorph.api.type.IPersistentSelector;
import top.theillusivec4.polymorph.common.network.NetworkManager;
import top.theillusivec4.polymorph.common.network.server.SPacketHighlightRecipe;
import top.theillusivec4.polymorph.mixin.core.AbstractFurnaceContainerMixin;

public class FurnaceSelector implements IPersistentSelector {

  private final AbstractFurnaceTileEntity parent;

  private AbstractCookingRecipe selectedRecipe;
  private ItemStack lastFailedInput = ItemStack.EMPTY;
  private String savedRecipe = "";

  public FurnaceSelector(AbstractFurnaceTileEntity tileEntity) {
    this.parent = tileEntity;
  }

  @SuppressWarnings("unchecked")
  @Override
  public Optional<IRecipe<?>> fetchRecipe(World world) {
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
  public IRecipeType<? extends IRecipe<?>> getRecipeType() {
    if (this.parent instanceof SmokerTileEntity) {
      return IRecipeType.SMOKING;
    } else if (this.parent instanceof BlastFurnaceTileEntity) {
      return IRecipeType.BLASTING;
    } else {
      return IRecipeType.SMELTING;
    }
  }

  @Nonnull
  @Override
  public Optional<IRecipe<?>> getSelectedRecipe() {
    return Optional.ofNullable(selectedRecipe);
  }

  @Override
  public void setSavedRecipe(String recipe) {
    this.savedRecipe = recipe;
  }

  @Override
  public void setSelectedRecipe(IRecipe<?> recipe) {
    this.selectedRecipe = (AbstractCookingRecipe) recipe;
    World world = this.parent.getWorld();

    if (world instanceof ServerWorld) {

      if (Polymorph.isFastFurnaceLoaded) {
        try {
          FieldUtils.writeField(this.parent, "curRecipe", this.selectedRecipe, true);
        } catch (IllegalAccessException e) {
          Polymorph.LOGGER.error("Error accessing curRecipe from FastFurnace!");
        }
      }
      ((ServerWorld) world).getPlayers().forEach(player -> {
        if (player.openContainer instanceof AbstractFurnaceContainer &&
            ((AbstractFurnaceContainerMixin) player.openContainer).getFurnaceInventory() ==
                this.parent) {
          NetworkManager.INSTANCE.send(PacketDistributor.PLAYER.with(() -> player),
              new SPacketHighlightRecipe(recipe.getId().toString()));
        }
      });
    }
  }

  @Override
  public TileEntity getParent() {
    return this.parent;
  }
}
