package top.theillusivec4.polymorph.common.impl;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.block.entity.AbstractFurnaceBlockEntity;
import net.minecraft.block.entity.BlastFurnaceBlockEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.SmokerBlockEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.recipe.AbstractCookingRecipe;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeType;
import net.minecraft.screen.AbstractFurnaceScreenHandler;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import net.minecraft.world.World;
import top.theillusivec4.polymorph.api.type.BlockEntityRecipeSelector;
import top.theillusivec4.polymorph.common.network.PolymorphPackets;
import top.theillusivec4.polymorph.mixin.core.AccessorAbstractFurnaceScreenHandler;

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
      World world = this.parent.getWorld();

      if (world instanceof ServerWorld) {
        ((ServerWorld) world).getPlayers().forEach(player -> {
          if (player.currentScreenHandler instanceof AbstractFurnaceScreenHandler &&
              ((AccessorAbstractFurnaceScreenHandler) player.currentScreenHandler)
                  .getInventory() == this.parent) {
            PacketByteBuf buf = PacketByteBufs.create();
            buf.writeIdentifier(recipe.getId());
            ServerPlayNetworking.send(player, PolymorphPackets.HIGHLIGHT_RECIPE, buf);
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
