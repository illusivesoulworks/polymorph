package top.theillusivec4.polymorph.core;

import java.util.Comparator;
import java.util.Optional;
import net.minecraft.block.entity.AbstractFurnaceBlockEntity;
import net.minecraft.block.entity.BlastFurnaceBlockEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.SmokerBlockEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.recipe.AbstractCookingRecipe;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeType;
import net.minecraft.screen.AbstractFurnaceScreenHandler;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import net.minecraft.world.World;
import top.theillusivec4.polymorph.api.type.PersistentSelector;
import top.theillusivec4.polymorph.loader.mixin.core.AbstractFurnaceScreenHandlerAccessor;

public class FurnaceSelector implements PersistentSelector {

  private final AbstractFurnaceBlockEntity parent;

  private AbstractCookingRecipe selectedRecipe;
  private ItemStack lastFailedInput = ItemStack.EMPTY;
  private String savedRecipe = "";

  public FurnaceSelector(AbstractFurnaceBlockEntity tileEntity) {
    this.parent = tileEntity;
  }

  @SuppressWarnings("unchecked")
  @Override
  public Optional<Recipe<?>> fetchRecipe(World world) {
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
  public RecipeType<? extends Recipe<?>> getRecipeType() {
    if (this.parent instanceof SmokerBlockEntity) {
      return RecipeType.SMOKING;
    } else if (this.parent instanceof BlastFurnaceBlockEntity) {
      return RecipeType.BLASTING;
    } else {
      return RecipeType.SMELTING;
    }
  }

  @Override
  public Optional<Recipe<?>> getSelectedRecipe() {
    return Optional.ofNullable(selectedRecipe);
  }

  @Override
  public void setSavedRecipe(String recipe) {
    this.savedRecipe = recipe;
  }

  @Override
  public void setSelectedRecipe(Recipe<?> recipe) {
    this.selectedRecipe = (AbstractCookingRecipe) recipe;
    World world = this.parent.getWorld();

    if (world instanceof ServerWorld) {
      ((ServerWorld) world).getPlayers().forEach(player -> {
        if (player.currentScreenHandler instanceof AbstractFurnaceScreenHandler &&
            ((AbstractFurnaceScreenHandlerAccessor) player.currentScreenHandler)
                .getInventory() == this.parent) {
          Polymorph.getLoader().getPacketVendor()
              .highlightRecipe(recipe.getId().toString(), player);
        }
      });
    }
  }

  @Override
  public BlockEntity getParent() {
    return this.parent;
  }

  @Override
  public void readFromNbt(CompoundTag compoundTag) {

    if (compoundTag.contains("SelectedRecipe")) {
      this.setSavedRecipe(compoundTag.getString("SelectedRecipe"));
    }
  }

  @Override
  public void writeToNbt(CompoundTag compoundTag) {

    if (this.selectedRecipe != null) {
      compoundTag.putString("SelectedRecipe", this.selectedRecipe.getId().toString());
    }
  }
}
