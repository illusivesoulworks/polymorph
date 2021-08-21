package top.theillusivec4.polymorph.common;

import dev.onyxstudios.cca.api.v3.block.BlockComponentFactoryRegistry;
import dev.onyxstudios.cca.api.v3.block.BlockComponentInitializer;
import java.util.Optional;
import net.minecraft.block.entity.AbstractFurnaceBlockEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.gui.screen.ingame.AbstractFurnaceScreen;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeType;
import net.minecraft.screen.AbstractFurnaceScreenHandler;
import net.minecraft.world.World;
import top.theillusivec4.polymorph.api.PolymorphApi;
import top.theillusivec4.polymorph.api.PolymorphComponents;
import top.theillusivec4.polymorph.api.type.BlockEntityRecipeSelector;
import top.theillusivec4.polymorph.client.recipe.FurnaceRecipeController;
import top.theillusivec4.polymorph.common.impl.FurnaceRecipeSelector;

public class PolymorphComponentInitializer implements BlockComponentInitializer {

  @Override
  public void registerBlockComponentFactories(
      BlockComponentFactoryRegistry blockComponentFactoryRegistry) {
    PolymorphApi.getInstance().addBlockEntity(blockEntity -> {
      if (blockEntity instanceof AbstractFurnaceBlockEntity) {
        return new FurnaceRecipeSelector((AbstractFurnaceBlockEntity) blockEntity);
      }
      return null;
    }, handledScreen -> {
      if (handledScreen instanceof AbstractFurnaceScreen &&
          handledScreen.getScreenHandler() instanceof AbstractFurnaceScreenHandler) {
        return new FurnaceRecipeController(handledScreen);
      }
      return null;
    });
    blockComponentFactoryRegistry
        .registerFor(BlockEntity.class, PolymorphComponents.BLOCK_ENTITY_RECIPE_SELECTOR,
            blockEntity -> PolymorphApi.getInstance().getBlockEntityRecipeSelector(blockEntity)
                .orElse(new EmptySelector()));
  }

  static class EmptySelector implements BlockEntityRecipeSelector {

    @Override
    public Optional<? extends Recipe<?>> getRecipe(World world) {
      return Optional.empty();
    }

    @Override
    public Optional<? extends Recipe<?>> getSelectedRecipe() {
      return Optional.empty();
    }

    @Override
    public RecipeType<? extends Recipe<?>> getRecipeType() {
      return null;
    }

    @Override
    public void setSelectedRecipe(Recipe<?> recipe) {

    }

    @Override
    public void setSavedRecipe(String recipe) {

    }

    @Override
    public BlockEntity getParent() {
      return null;
    }

    @Override
    public void readFromNbt(NbtCompound nbtCompound) {

    }

    @Override
    public void writeToNbt(NbtCompound nbtCompound) {

    }
  }
}
