package top.theillusivec4.polymorph.mixin;

import java.util.List;
import java.util.Optional;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeManager;
import net.minecraft.recipe.RecipeType;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;
import top.theillusivec4.polymorph.common.network.PolymorphPackets;
import top.theillusivec4.polymorph.common.util.CraftingPlayers;

public class MixinHooks {

  public static <T extends Recipe<C>, C extends Inventory> Optional<T> getResult(
      RecipeManager recipeManager, RecipeType<T> type, C inventory, World world,
      PlayerEntity player) {
    List<T> recipes = recipeManager.getAllMatches(type, inventory, world);

    if (recipes.isEmpty()) {
      return Optional.empty();
    }
    T result = null;
    PacketByteBuf buf = PacketByteBufs.create();

    for (T recipe : recipes) {
      Identifier id = recipe.getId();

      if (result == null && CraftingPlayers.getRecipe((ServerPlayerEntity) player)
          .map(identifier -> identifier.equals(id)).orElse(false)) {
        result = recipe;
      }
      buf.writeString(id.toString());
    }
    ServerPlayNetworking.send((ServerPlayerEntity) player, PolymorphPackets.SEND_RECIPES, buf);
    return recipes.size() > 0 ? Optional.of(recipes.get(0)) : Optional.empty();
  }
}
