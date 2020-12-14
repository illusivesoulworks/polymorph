package top.theillusivec4.polymorph.common.integrations.ironfurnaces;

import ironfurnaces.container.BlockIronFurnaceContainerBase;
import ironfurnaces.tileentity.BlockIronFurnaceTileBase;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import javax.annotation.Nonnull;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.AbstractCookingRecipe;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.item.crafting.RecipeManager;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Util;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.fml.network.PacketDistributor;
import org.apache.commons.lang3.reflect.FieldUtils;
import top.theillusivec4.polymorph.Polymorph;
import top.theillusivec4.polymorph.api.PolymorphApi;
import top.theillusivec4.polymorph.api.type.IFurnaceProvider;
import top.theillusivec4.polymorph.api.type.IPersistentSelector;
import top.theillusivec4.polymorph.common.integrations.CompatibilityModule;
import top.theillusivec4.polymorph.common.network.NetworkManager;
import top.theillusivec4.polymorph.common.network.server.SPacketHighlightRecipe;

public class IronFurnacesModule extends CompatibilityModule {

  @Override
  public void setup() {
    PolymorphApi.getInstance().addEntityProvider(tileEntity -> {
      if (tileEntity instanceof BlockIronFurnaceTileBase) {
        return new IronFurnaceSelector((BlockIronFurnaceTileBase) tileEntity);
      }
      return null;
    }, container -> {
      if (container instanceof BlockIronFurnaceContainerBase) {
        return new IronFurnaceProvider((BlockIronFurnaceContainerBase) container);
      }
      return null;
    });
  }

  private static class IronFurnaceSelector implements IPersistentSelector {

    private final BlockIronFurnaceTileBase parent;

    private AbstractCookingRecipe selectedRecipe;
    private ItemStack lastFailedInput = ItemStack.EMPTY;
    private String savedRecipe = "";

    public IronFurnaceSelector(BlockIronFurnaceTileBase tileEntity) {
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
          return saved;
        }
      }
      Optional<IRecipe<?>> maybeRecipe = world.getRecipeManager().getRecipes().stream()
          .filter((val) -> val.getType() == this.getRecipeType()).flatMap((val) -> Util
              .streamOptional(
                  this.getRecipeType().matches((IRecipe<IInventory>) val, world, parent)))
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
      return this.parent.recipeType;
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
        try {
          FieldUtils.writeField(this.parent, "curRecipe", this.selectedRecipe, true);
        } catch (IllegalAccessException e) {
          Polymorph.LOGGER.error("Error accessing curRecipe from Iron Furnaces!");
        }
        ((ServerWorld) world).getPlayers().forEach(player -> {
          if (player.openContainer instanceof BlockIronFurnaceContainerBase &&
              player.openContainer.inventorySlots.get(0).inventory == this.parent) {
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

  private static class IronFurnaceProvider implements IFurnaceProvider {

    final IInventory input;
    private final BlockIronFurnaceContainerBase container;

    public IronFurnaceProvider(BlockIronFurnaceContainerBase containerBase) {
      this.container = containerBase;
      this.input = containerBase.inventorySlots.get(0).inventory;
    }

    public IRecipeType<? extends AbstractCookingRecipe> getRecipeType() {

      if (this.input instanceof BlockIronFurnaceTileBase) {
        BlockIronFurnaceTileBase te = (BlockIronFurnaceTileBase) this.input;
        return te.recipeType;
      }
      return IRecipeType.SMELTING;
    }

    @Nonnull
    @Override
    public Container getContainer() {
      return container;
    }

    @Nonnull
    @Override
    public IInventory getInventory() {
      return this.input;
    }

    @Nonnull
    @Override
    public Slot getOutputSlot() {
      return this.container.inventorySlots.get(2);
    }

    @Nonnull
    @Override
    public List<? extends AbstractCookingRecipe> getRecipes(World world,
                                                            RecipeManager recipeManager) {
      return recipeManager.getRecipes(this.getRecipeType(), this.input, world);
    }
  }
}
