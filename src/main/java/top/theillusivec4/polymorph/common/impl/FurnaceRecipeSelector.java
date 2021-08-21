package top.theillusivec4.polymorph.common.impl;

import java.util.Comparator;
import java.util.Optional;
import net.minecraft.block.entity.AbstractFurnaceBlockEntity;
import net.minecraft.block.entity.BlastFurnaceBlockEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.SmokerBlockEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.recipe.AbstractCookingRecipe;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeType;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import net.minecraft.world.World;
import top.theillusivec4.polymorph.api.type.BlockEntityRecipeSelector;

public class FurnaceRecipeSelector implements BlockEntityRecipeSelector {

  private final AbstractFurnaceBlockEntity parent;

  private AbstractCookingRecipe selectedRecipe;
  private ItemStack lastFailedInput = ItemStack.EMPTY;
  private String savedRecipe = "";

  public FurnaceRecipeSelector(AbstractFurnaceBlockEntity tileEntity) {
    this.parent = tileEntity;
  }

  @SuppressWarnings("unchecked")
  @Override
  public Optional<Recipe<?>> getRecipe(World world) {
    ItemStack input = parent.getStack(0);

    if (input == lastFailedInput) {
      return Optional.empty();
    }

    if (!savedRecipe.isEmpty()) {
      Optional<Recipe<?>> saved =
          (Optional<Recipe<?>>) world.getRecipeManager().get(new Identifier(savedRecipe));

      if (!saved.isPresent() || !((Recipe<Inventory>) saved.get()).matches(parent, world)) {
        savedRecipe = "";
      } else {
        this.setSelectedRecipe(saved.get());
        savedRecipe = "";
        return saved;
      }
    }
    Optional<Recipe<?>> maybeRecipe = world.getRecipeManager().values().stream()
        .filter((val) -> val.getType() == this.getRecipeType()).flatMap((val) -> Util
            .stream(this.getRecipeType().get((Recipe<Inventory>) val, world, parent)))
        .min(Comparator.comparing((recipe) -> recipe.getOutput().getTranslationKey()))
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
  public RecipeType<? extends AbstractCookingRecipe> getRecipeType() {
    if (this.parent instanceof SmokerBlockEntity) {
      return RecipeType.SMOKING;
    } else if (this.parent instanceof BlastFurnaceBlockEntity) {
      return RecipeType.BLASTING;
    } else {
      return RecipeType.SMELTING;
    }
  }

  @Override
  public void setSelectedRecipe(Recipe<?> recipe) {

    if (recipe instanceof AbstractCookingRecipe) {
      this.selectedRecipe = (AbstractCookingRecipe) recipe;
    }
  }

  @Override
  public void setSavedRecipe(String recipe) {
    this.savedRecipe = recipe;
  }

  @Override
  public BlockEntity getParent() {
    return this.parent;
  }

  @Override
  public void readFromNbt(NbtCompound nbtCompound) {

    if (nbtCompound.contains("SelectedRecipe")) {
      this.setSavedRecipe(nbtCompound.getString("SelectedRecipe"));
    }
  }

  @Override
  public void writeToNbt(NbtCompound nbtCompound) {

    if (this.selectedRecipe != null) {
      nbtCompound.putString("SelectedRecipe", this.selectedRecipe.getId().toString());
    }
  }
}
