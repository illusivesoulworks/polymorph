package top.theillusivec4.polymorph.common.capability;

import com.mojang.datafixers.util.Pair;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.SortedSet;
import java.util.TreeSet;
import javax.annotation.Nullable;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import top.theillusivec4.polymorph.api.common.capability.IRecipeProcessor;
import top.theillusivec4.polymorph.common.PolymorphMod;
import top.theillusivec4.polymorph.common.impl.RecipeData;
import top.theillusivec4.polymorph.common.integration.AbstractCompatibilityModule;

public abstract class AbstractRecipeProcessor<T extends TileEntity, I extends IInventory, R extends IRecipe<I>>
    extends SimpleRecipeDataset implements IRecipeProcessor {

  private final T blockEntity;

  protected R selectedRecipe;
  protected String savedRecipe = "";

  public AbstractRecipeProcessor(T tileEntity) {
    this.blockEntity = tileEntity;
  }

  @SuppressWarnings("unchecked")
  @Override
  public Optional<R> getRecipe(World world) {

    if (!savedRecipe.isEmpty()) {
      List<R> recipe = new ArrayList<>();
      try {
        world.getRecipeManager().getRecipe(new ResourceLocation(savedRecipe)).ifPresent(result -> {
          if (((R) result).matches(this.getInventory(), world)) {
            this.setSelectedRecipe(result, null);
            recipe.add((R) result);
          }
        });
      } catch (ClassCastException e) {
        PolymorphMod.LOGGER.error("Recipe {} does not match inventory {}", savedRecipe,
            this.getInventory());
      }
      savedRecipe = "";

      if (!recipe.isEmpty()) {
        return Optional.of(recipe.get(0));
      }
    }
    SortedSet<Pair<R, ItemStack>> recipes =
        new TreeSet<>(Comparator.comparing(pair -> pair.getSecond().getTranslationKey()));
    this.getRecipeDataset().clear();

    for (IRecipe<?> recipe : world.getRecipeManager().getRecipes()) {

      try {
        if (recipe.getType() == this.getRecipeType() &&
            ((IRecipe<IInventory>) recipe).matches(this.getInventory(), world)) {
          R cast = (R) recipe;
          ItemStack output = cast.getCraftingResult(this.getInventory());
          recipes.add(new Pair<>(cast, output));
          this.getRecipeDataset().add(new RecipeData(recipe.getId(), output));
        }
      } catch (ClassCastException e) {
        PolymorphMod.LOGGER.error("Recipe {} does not match inventory {}", recipe,
            this.getInventory());
      }
    }

    if (recipes.isEmpty()) {
      return Optional.empty();
    } else {
      R recipe = recipes.first().getFirst();
      this.setSelectedRecipe(recipe, null);
      return Optional.of(recipe);
    }
  }

  public abstract I getInventory();

  public abstract IRecipeType<? extends R> getRecipeType();

  @Override
  public Optional<R> getSelectedRecipe() {
    return Optional.ofNullable(selectedRecipe);
  }

  @SuppressWarnings("unchecked")
  @Override
  public void setSelectedRecipe(IRecipe<?> recipe, @Nullable PlayerEntity selectingPlayer) {
    try {
      this.selectedRecipe = (R) recipe;
    } catch (ClassCastException e) {
      PolymorphMod.LOGGER.error("Recipe {} does not match inventory {}", recipe,
          this.getInventory());
      return;
    }
    Container container = selectingPlayer != null ? selectingPlayer.openContainer : null;

    for (AbstractCompatibilityModule integration : PolymorphMod.getIntegrations()) {

      if (integration.selectRecipe(this.blockEntity, this.selectedRecipe) ||
          (container != null && integration.selectRecipe(container, this.selectedRecipe))) {
        return;
      }
    }
  }

  @Override
  public T getTileEntity() {
    return this.blockEntity;
  }

  @Override
  public void readNBT(CompoundNBT nbtCompound) {

    if (nbtCompound.contains("SelectedRecipe")) {
      this.savedRecipe = nbtCompound.getString("SelectedRecipe");
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
