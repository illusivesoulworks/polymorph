/*
 * Copyright (c) 2020 C4
 *
 * This file is part of Polymorph, a mod made for Minecraft.
 *
 * Polymorph is free software: you can redistribute it and/or modify it under the terms of the GNU
 * Lesser General Public License as published by the Free Software Foundation, either version 3 of
 * the License, or any later version.
 *
 * Polymorph is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without
 * even the implied warranty of MERCHANTABILITY or FITNESS FOR PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with Polymorph.
 * If not, see <https://www.gnu.org/licenses/>.
 */

package top.theillusivec4.polymorph.loader.common.integration.ironfurnaces;

//import ironfurnaces.container.BlockIronFurnaceScreenHandler;
//import ironfurnaces.container.BlockIronFurnaceScreenHandlerBase;
//import ironfurnaces.tileentity.BlockIronFurnaceTileBase;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.recipe.AbstractCookingRecipe;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeManager;
import net.minecraft.recipe.RecipeType;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import net.minecraft.world.World;
import org.apache.commons.lang3.reflect.FieldUtils;
import top.theillusivec4.polymorph.api.PolymorphApi;
import top.theillusivec4.polymorph.api.type.FurnaceProvider;
import top.theillusivec4.polymorph.api.type.PersistentSelector;
import top.theillusivec4.polymorph.core.Polymorph;
import top.theillusivec4.polymorph.loader.common.integration.CompatibilityModule;

public class IronFurnacesModule extends CompatibilityModule {

  @Override
  public void setup() {
//    PolymorphApi.getInstance().addEntityProvider(blockEntity -> {
//      if (blockEntity instanceof BlockIronFurnaceTileBase) {
//        return new IronFurnaceSelector((BlockIronFurnaceTileBase) blockEntity);
//      }
//      return null;
//    }, screenHandler -> {
//      if (screenHandler instanceof BlockIronFurnaceScreenHandlerBase) {
//        return new IronFurnaceProvider(screenHandler);
//      }
//      return null;
//    });
  }

//  private static class IronFurnaceSelector implements PersistentSelector {
//
//    private final BlockIronFurnaceTileBase parent;
//
//    private AbstractCookingRecipe selectedRecipe;
//    private ItemStack lastFailedInput = ItemStack.EMPTY;
//    private String savedRecipe = "";
//
//    public IronFurnaceSelector(BlockIronFurnaceTileBase tileEntity) {
//      this.parent = tileEntity;
//    }
//
//    @SuppressWarnings("unchecked")
//    @Override
//    public Optional<Recipe<?>> fetchRecipe(World world) {
//      ItemStack input = parent.getStack(0);
//
//      if (input == lastFailedInput) {
//        return Optional.empty();
//      }
//
//      if (!savedRecipe.isEmpty()) {
//        Optional<Recipe<?>> saved =
//            (Optional<Recipe<?>>) world.getRecipeManager().get(new Identifier(savedRecipe));
//
//        if (!saved.isPresent() || !((Recipe<Inventory>) saved.get()).matches(parent, world)) {
//          savedRecipe = "";
//        } else {
//          this.setSelectedRecipe(saved.get());
//          savedRecipe = "";
//          return saved;
//        }
//      }
//      Optional<Recipe<?>> maybeRecipe = world.getRecipeManager().values().stream()
//          .filter((val) -> val.getType() == this.getRecipeType()).flatMap((val) -> Util
//              .stream(this.getRecipeType().get((Recipe<Inventory>) val, world, parent)))
//          .min(Comparator.comparing((recipe) -> recipe.getOutput().getTranslationKey()))
//          .map((val) -> {
//            this.setSelectedRecipe(val);
//            return val;
//          });
//
//      if (!maybeRecipe.isPresent()) {
//        lastFailedInput = input;
//      }
//      return maybeRecipe;
//    }
//
//    @Override
//    public RecipeType<? extends Recipe<?>> getRecipeType() {
//      try {
//        return (RecipeType<? extends Recipe<?>>) FieldUtils
//            .readField(this.parent, "recipeType", true);
//      } catch (IllegalAccessException e) {
//        Polymorph.LOGGER.error("Error accessing recipeType of Iron Furnaces!");
//        e.printStackTrace();
//      }
//      return RecipeType.SMELTING;
//    }
//
//    @Override
//    public Optional<Recipe<?>> getSelectedRecipe() {
//      return Optional.ofNullable(selectedRecipe);
//    }
//
//    @Override
//    public void setSavedRecipe(String recipe) {
//      this.savedRecipe = recipe;
//    }
//
//    @Override
//    public void setSelectedRecipe(Recipe<?> recipe) {
//      this.selectedRecipe = (AbstractCookingRecipe) recipe;
//      World world = this.parent.getWorld();
//
//      if (world instanceof ServerWorld) {
//        ((ServerWorld) world).getPlayers().forEach(player -> {
//          if (player.currentScreenHandler instanceof BlockIronFurnaceScreenHandler &&
//              player.currentScreenHandler.slots.get(0).inventory == this.parent) {
//            Polymorph.getLoader().getPacketVendor()
//                .highlightRecipe(recipe.getId().toString(), player);
//          }
//        });
//      }
//    }
//
//    @Override
//    public BlockEntity getParent() {
//      return this.parent;
//    }
//
//    @Override
//    public void readFromNbt(NbtCompound compoundTag) {
//
//      if (compoundTag.contains("SelectedRecipe")) {
//        this.setSavedRecipe(compoundTag.getString("SelectedRecipe"));
//      }
//    }
//
//    @Override
//    public void writeToNbt(NbtCompound compoundTag) {
//
//      if (this.selectedRecipe != null) {
//        compoundTag.putString("SelectedRecipe", this.selectedRecipe.getId().toString());
//      }
//    }
//  }

//  private static class IronFurnaceProvider implements FurnaceProvider {
//
//    final ScreenHandler container;
//    final Inventory input;
//
//    public IronFurnaceProvider(ScreenHandler container) {
//      this.container = container;
//      this.input = container.slots.get(0).inventory;
//    }
//
//    @SuppressWarnings("unchecked")
//    private RecipeType<? extends AbstractCookingRecipe> getRecipeType() {
//
//      if (this.input instanceof BlockIronFurnaceTileBase) {
//        try {
//          return (RecipeType<? extends AbstractCookingRecipe>) FieldUtils
//              .readField(this.input, "recipeType", true);
//        } catch (IllegalAccessException e) {
//          Polymorph.LOGGER.error("Error accessing recipeType of Iron Furnaces!");
//          e.printStackTrace();
//        }
//      }
//      return RecipeType.SMELTING;
//    }
//
//    @Override
//    public ScreenHandler getScreenHandler() {
//      return this.container;
//    }
//
//    @Override
//    public Inventory getInventory() {
//      return this.input;
//    }
//
//    @Override
//    public List<? extends AbstractCookingRecipe> getRecipes(World world,
//                                                            RecipeManager recipeManager) {
//      return recipeManager.getAllMatches(this.getRecipeType(), this.getInventory(), world);
//    }
//
//    @Override
//    public Slot getOutputSlot() {
//      return this.container.slots.get(2);
//    }
//  }
}
