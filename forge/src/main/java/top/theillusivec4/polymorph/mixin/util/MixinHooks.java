package top.theillusivec4.polymorph.mixin.util;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.item.crafting.RecipeManager;
import net.minecraft.item.crafting.SmithingRecipe;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.PacketDistributor;
import top.theillusivec4.polymorph.api.PolymorphCapabilities;
import top.theillusivec4.polymorph.common.network.PolymorphNetwork;
import top.theillusivec4.polymorph.common.network.server.SPacketSendRecipes;
import top.theillusivec4.polymorph.common.util.CraftingPlayers;

public class MixinHooks {

  public static <T extends IRecipe<C>, C extends IInventory> Optional<T> getRecipe(
      RecipeManager recipeManager, IRecipeType<T> type, C inventory, World world,
      PlayerEntity player) {
    List<T> recipes = recipeManager.getRecipes(type, inventory, world);

    if (recipes.isEmpty()) {

      if (player instanceof ServerPlayerEntity) {
        PolymorphNetwork.INSTANCE.send(
            PacketDistributor.PLAYER.with(() -> (ServerPlayerEntity) player),
            new SPacketSendRecipes(new HashSet<>(), null));
      }
      return Optional.empty();
    }
    T result = null;
    Set<ResourceLocation> recipeIds = new HashSet<>();

    for (T recipe : recipes) {
      ResourceLocation id = recipe.getId();

      if (result == null &&
          CraftingPlayers.getRecipe(player).map(identifier -> identifier.equals(id))
              .orElse(false)) {
        result = recipe;
      }
      recipeIds.add(recipe.getId());
    }

    if (result == null) {
      CraftingPlayers.remove(player);
    }

    if (player instanceof ServerPlayerEntity) {
      PolymorphNetwork.INSTANCE.send(
          PacketDistributor.PLAYER.with(() -> (ServerPlayerEntity) player),
          new SPacketSendRecipes(recipeIds, recipes.get(0).getId()));
    }
    return Optional.of(result != null ? result : recipes.get(0));
  }

  @SuppressWarnings("unchecked")
  public static <T extends IRecipe<C>, C extends IInventory> Optional<T> getSelectedRecipe(
      IRecipeType<T> recipeTypeIn, C inventoryIn, World worldIn) {

    if (inventoryIn instanceof TileEntity) {
      TileEntity te = (TileEntity) inventoryIn;
      List<T> recipe = new ArrayList<>();
      PolymorphCapabilities.getRecipeSelector(te).ifPresent(recipeSelector -> {
        ItemStack input = inventoryIn.getStackInSlot(0);

        if (!input.isEmpty()) {
          Optional<T> maybeSelected = (Optional<T>) recipeSelector.getSelectedRecipe();
          maybeSelected.ifPresent(res -> {
            if (res.matches(inventoryIn, worldIn)) {
              recipe.add(res);
            } else {
              recipeSelector.getRecipe(worldIn).ifPresent(res1 -> recipe.add((T) res1));
            }
          });

          if (!maybeSelected.isPresent()) {
            recipeSelector.getRecipe(worldIn).ifPresent(res1 -> recipe.add((T) res1));
          }
        }
      });
      return recipe.isEmpty() ? Optional.empty() : Optional.of(recipe.get(0));
    }
    return Optional.empty();
  }

  public static SmithingRecipe getSmithingRecipe(PlayerEntity player,
                                                 List<SmithingRecipe> recipes) {
    SmithingRecipe defaultRecipe = recipes.get(0);

    if (player instanceof ServerPlayerEntity) {
      SmithingRecipe result = null;
      Set<ResourceLocation> recipeIds = new HashSet<>();

      for (SmithingRecipe recipe : recipes) {
        ResourceLocation id = recipe.getId();

        if (result == null &&
            CraftingPlayers.getRecipe(player).map(identifier -> identifier.equals(id))
                .orElse(false)) {
          result = recipe;
        }
        recipeIds.add(recipe.getId());
      }

      if (result == null) {
        CraftingPlayers.remove(player);
      }
      PolymorphNetwork.INSTANCE.send(
          PacketDistributor.PLAYER.with(() -> (ServerPlayerEntity) player),
          new SPacketSendRecipes(recipeIds, defaultRecipe.getId()));
      return result != null ? result : defaultRecipe;
    }
    return defaultRecipe;
  }
}
