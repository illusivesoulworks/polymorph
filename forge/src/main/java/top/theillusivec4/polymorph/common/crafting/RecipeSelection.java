package top.theillusivec4.polymorph.common.crafting;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.item.crafting.SmithingRecipe;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import top.theillusivec4.polymorph.api.PolymorphApi;
import top.theillusivec4.polymorph.api.common.base.IRecipeData;
import top.theillusivec4.polymorph.common.PolymorphMod;
import top.theillusivec4.polymorph.common.impl.RecipeData;
import top.theillusivec4.polymorph.common.crafting.CraftingPlayers;

public class RecipeSelection {

  public static <T extends IRecipe<C>, C extends IInventory> Optional<T> getRecipe(
      IRecipeType<T> pType, C pInventory, World pWorld, PlayerEntity pPlayer) {
    return getRecipe(pType, pInventory, pWorld, pPlayer, null);
  }

  public static <T extends IRecipe<C>, C extends IInventory> Optional<T> getRecipe(
      IRecipeType<T> pType, C pInventory, World pWorld, PlayerEntity pPlayer,
      Consumer<Set<IRecipeData>> pSaver) {
    List<T> recipes = pWorld.getRecipeManager().getRecipes(pType, pInventory, pWorld);

    if (recipes.isEmpty()) {

      if (pPlayer instanceof ServerPlayerEntity) {
        PolymorphApi.common().getPacketDistributor()
            .sendRecipesListS2C((ServerPlayerEntity) pPlayer);
      }
      return Optional.empty();
    }
    T result = null;
    Set<IRecipeData> recipeDataset = new HashSet<>();

    for (T recipe : recipes) {
      ResourceLocation id = recipe.getId();

      if (result == null &&
          CraftingPlayers.getRecipe(pPlayer).map(identifier -> identifier.equals(id))
              .orElse(false)) {
        result = recipe;
      }
      recipeDataset.add(new RecipeData(recipe.getId(), recipe.getCraftingResult(pInventory)));
    }

    if (pSaver != null) {
      pSaver.accept(recipeDataset);
    }

    if (result == null) {
      CraftingPlayers.remove(pPlayer);
    }

    if (pPlayer instanceof ServerPlayerEntity) {
      PolymorphApi.common().getPacketDistributor()
          .sendRecipesListS2C((ServerPlayerEntity) pPlayer, recipeDataset, null);
    }
    return Optional.of(result != null ? result : recipes.get(0));
  }

  @SuppressWarnings("unchecked")
  public static <T extends IRecipe<C>, C extends IInventory> Optional<T> getRecipe(
      IRecipeType<T> pType, C pInventory, World pWorld, TileEntity pBlockEntity) {
    List<T> recipe = new ArrayList<>();
    PolymorphApi.common().getProcessorCapability(pBlockEntity).ifPresent(controller -> {
      controller.getSelectedRecipe().ifPresent(result -> {
        try {
          if (((T) result).matches(pInventory, pWorld)) {
            recipe.add((T) result);
          }
        } catch (ClassCastException e) {
          PolymorphMod.LOGGER.error("{} cannot be cast to {}", result, pType);
        }
      });

      if (recipe.isEmpty()) {
        controller.getRecipe(pWorld).ifPresent(result -> {
          try {
            recipe.add((T) result);
          } catch (ClassCastException e) {
            PolymorphMod.LOGGER.error("{} cannot be cast to {}", result, pType);
          }
        });
      }
    });
    return recipe.isEmpty() ? Optional.empty() : Optional.of(recipe.get(0));
  }

  public static SmithingRecipe getSmithingRecipe(PlayerEntity pPlayer, IInventory pInventory,
                                                 List<SmithingRecipe> pRecipes) {
    return getSmithingRecipe(pPlayer, pInventory, pRecipes, null);
  }

  public static SmithingRecipe getSmithingRecipe(PlayerEntity pPlayer, IInventory pInventory,
                                                 List<SmithingRecipe> pRecipes,
                                                 Consumer<Set<IRecipeData>> pSaver) {
    SmithingRecipe defaultRecipe = pRecipes.get(0);

    if (pPlayer instanceof ServerPlayerEntity) {
      SmithingRecipe result = null;
      Set<IRecipeData> recipeDataset = new HashSet<>();

      for (SmithingRecipe recipe : pRecipes) {
        ResourceLocation id = recipe.getId();

        if (result == null &&
            CraftingPlayers.getRecipe(pPlayer).map(identifier -> identifier.equals(id))
                .orElse(false)) {
          result = recipe;
        }
        recipeDataset.add(new RecipeData(recipe.getId(), recipe.getCraftingResult(pInventory)));
      }

      if (pSaver != null) {
        pSaver.accept(recipeDataset);
      }

      if (result == null) {
        CraftingPlayers.remove(pPlayer);
      }
      PolymorphApi.common().getPacketDistributor()
          .sendRecipesListS2C((ServerPlayerEntity) pPlayer, recipeDataset, null);
      return result != null ? result : defaultRecipe;
    }
    return defaultRecipe;
  }
}
