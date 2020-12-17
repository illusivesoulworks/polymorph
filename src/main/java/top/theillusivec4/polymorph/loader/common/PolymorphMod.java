/*
 * Copyright (c) 2020 C4
 *
 * This file is part of Polymorph, a mod made for Minecraft.
 *
 * Polymorph is free software: you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published
 * by the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Polymorph is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with Polymorph.  If not, see <https://www.gnu.org/licenses/>.
 */

package top.theillusivec4.polymorph.loader.common;

import dev.onyxstudios.cca.api.v3.block.BlockComponentFactoryRegistry;
import dev.onyxstudios.cca.api.v3.block.BlockComponentInitializer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.block.entity.AbstractFurnaceBlockEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.recipe.AbstractCookingRecipe;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeManager;
import net.minecraft.recipe.RecipeType;
import net.minecraft.screen.AbstractFurnaceScreenHandler;
import net.minecraft.screen.BlastFurnaceScreenHandler;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.SmokerScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.world.World;
import top.theillusivec4.polymorph.api.PolymorphApi;
import top.theillusivec4.polymorph.api.PolymorphComponent;
import top.theillusivec4.polymorph.api.type.FurnaceProvider;
import top.theillusivec4.polymorph.api.type.PersistentSelector;
import top.theillusivec4.polymorph.core.FurnaceSelector;
import top.theillusivec4.polymorph.loader.common.integration.CompatibilityModule;
import top.theillusivec4.polymorph.loader.common.integration.fabricfurnaces.FabricFurnacesModule;
import top.theillusivec4.polymorph.loader.common.integration.ironfurnaces.IronFurnacesModule;
import top.theillusivec4.polymorph.loader.network.NetworkHandler;
import top.theillusivec4.polymorph.loader.server.PolymorphCommands;

public class PolymorphMod implements ModInitializer, BlockComponentInitializer {

  private static final Map<String, Supplier<CompatibilityModule>> INTEGRATIONS = new HashMap<>();
  private static final List<CompatibilityModule> ACTIVE_INTEGRATIONS = new ArrayList<>();

  public static boolean isFastFurnaceLoaded = false;

  static {
    INTEGRATIONS.put("ironfurnaces", IronFurnacesModule::new);
    INTEGRATIONS.put("fabric-furnaces", FabricFurnacesModule::new);
  }

  public PolymorphMod() {
    FabricLoader loader = FabricLoader.getInstance();
    isFastFurnaceLoaded = loader.isModLoaded("fastfurnace");
    INTEGRATIONS.forEach((modid, supplier) -> {

      if (loader.isModLoaded(modid)) {
        ACTIVE_INTEGRATIONS.add(supplier.get());
      }
    });
  }

  @Override
  public void onInitialize() {
    PolymorphApi.getInstance().addEntityProvider(blockEntity -> {
      if (blockEntity instanceof AbstractFurnaceBlockEntity) {
        return new FurnaceSelector((AbstractFurnaceBlockEntity) blockEntity);
      }
      return null;
    }, screenHandler -> {
      if (screenHandler instanceof AbstractFurnaceScreenHandler) {
        return new SimpleFurnaceProvider(screenHandler);
      }
      return null;
    });
    NetworkHandler.setup();
    CommandRegistrationCallback.EVENT
        .register((commandDispatcher, b) -> PolymorphCommands.register(commandDispatcher));
    ACTIVE_INTEGRATIONS.forEach(CompatibilityModule::setup);
  }

  @Override
  public void registerBlockComponentFactories(
      BlockComponentFactoryRegistry blockComponentFactoryRegistry) {
    blockComponentFactoryRegistry
        .registerFor(BlockEntity.class, PolymorphComponent.SELECTOR,
            blockEntity -> PolymorphApi.getInstance().getSelector(blockEntity)
                .orElse(new EmptySelector()));
  }

  private static class EmptySelector implements PersistentSelector {

    @Override
    public Optional<Recipe<?>> fetchRecipe(World world) {
      return Optional.empty();
    }

    @Override
    public RecipeType<? extends Recipe<?>> getRecipeType() {
      return null;
    }

    @Override
    public Optional<? extends Recipe<?>> getSelectedRecipe() {
      return Optional.empty();
    }

    @Override
    public void setSavedRecipe(String recipe) {

    }

    @Override
    public void setSelectedRecipe(Recipe<?> recipe) {

    }

    @Override
    public BlockEntity getParent() {
      return null;
    }

    @Override
    public void readFromNbt(CompoundTag tag) {

    }

    @Override
    public void writeToNbt(CompoundTag tag) {

    }
  }

  public static class SimpleFurnaceProvider implements FurnaceProvider {

    final ScreenHandler container;
    final Inventory input;
    final RecipeType<? extends AbstractCookingRecipe> recipeType;

    public SimpleFurnaceProvider(ScreenHandler container) {
      this.container = container;
      this.input = container.slots.get(0).inventory;
      this.recipeType = this.getRecipeType();
    }

    private RecipeType<? extends AbstractCookingRecipe> getRecipeType() {

      if (this.container instanceof SmokerScreenHandler) {
        return RecipeType.SMOKING;
      } else if (this.container instanceof BlastFurnaceScreenHandler) {
        return RecipeType.BLASTING;
      } else {
        return RecipeType.SMELTING;
      }
    }

    @Override
    public ScreenHandler getScreenHandler() {
      return this.container;
    }

    @Override
    public Inventory getInventory() {
      return this.input;
    }

    @Override
    public List<? extends AbstractCookingRecipe> getRecipes(World world,
                                                            RecipeManager recipeManager) {
      return recipeManager.getAllMatches(this.recipeType, this.getInventory(), world);
    }

    @Override
    public Slot getOutputSlot() {
      return this.container.slots.get(2);
    }
  }
}
