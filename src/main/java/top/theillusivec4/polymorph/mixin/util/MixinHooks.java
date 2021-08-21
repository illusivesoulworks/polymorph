package top.theillusivec4.polymorph.mixin.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeManager;
import net.minecraft.recipe.RecipeType;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;
import top.theillusivec4.polymorph.api.PolymorphComponents;
import top.theillusivec4.polymorph.api.type.BlockEntityRecipeSelector;
import top.theillusivec4.polymorph.common.network.PolymorphPackets;
import top.theillusivec4.polymorph.common.util.CraftingPlayers;

public class MixinHooks {

  public static <T extends Recipe<C>, C extends Inventory> Optional<T> getResult(
      RecipeManager recipeManager, RecipeType<T> type, C inventory, World world,
      PlayerEntity player) {
    List<T> recipes = recipeManager.getAllMatches(type, inventory, world);

    if (recipes.isEmpty()) {
      ServerPlayNetworking.send((ServerPlayerEntity) player, PolymorphPackets.SEND_RECIPES,
          PacketByteBufs.empty());
      return Optional.empty();
    }
    T result = null;
    PacketByteBuf buf = PacketByteBufs.create();
    buf.writeIdentifier(new Identifier(""));

    for (T recipe : recipes) {
      Identifier id = recipe.getId();

      if (result == null && CraftingPlayers.getRecipe((ServerPlayerEntity) player)
          .map(identifier -> identifier.equals(id)).orElse(false)) {
        result = recipe;
      }
      buf.writeString(id.toString());
    }

    if (result == null) {
      CraftingPlayers.remove((ServerPlayerEntity) player);
    }
    ServerPlayNetworking.send((ServerPlayerEntity) player, PolymorphPackets.SEND_RECIPES, buf);
    return Optional.of(result != null ? result : recipes.get(0));
  }

  @SuppressWarnings("unchecked")
  public static <T extends Recipe<C>, C extends Inventory> Optional<T> getSelectedRecipe(
      RecipeType<T> recipeTypeIn, C inventoryIn, World worldIn) {
    if (inventoryIn instanceof BlockEntity) {
      BlockEntity te = (BlockEntity) inventoryIn;
      Optional<BlockEntityRecipeSelector> component =
          PolymorphComponents.BLOCK_ENTITY_RECIPE_SELECTOR.maybeGet(te);
      List<T> recipe = new ArrayList<>();
      component.ifPresent(selector -> {
        ItemStack input = inventoryIn.getStack(0);

        if (!input.isEmpty()) {
          Optional<T> maybeSelected = (Optional<T>) selector.getSelectedRecipe();
          maybeSelected.ifPresent(res -> {
            if (res.matches(inventoryIn, worldIn)) {
              recipe.add(res);
            } else {
              selector.getRecipe(worldIn).ifPresent(res1 -> recipe.add((T) res1));
            }
          });

          if (!maybeSelected.isPresent()) {
            selector.getRecipe(worldIn).ifPresent(res1 -> recipe.add((T) res1));
          }
        }
      });
      return recipe.isEmpty() ? Optional.empty() : Optional.of(recipe.get(0));
    }
    return Optional.empty();
  }
}
