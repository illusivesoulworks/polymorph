package top.theillusivec4.polymorph.mixin.util.integration;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.item.crafting.RecipeManager;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.PacketDistributor;
import slimeknights.tconstruct.tables.tileentity.table.CraftingStationTileEntity;
import top.theillusivec4.polymorph.common.network.PolymorphNetwork;
import top.theillusivec4.polymorph.common.network.server.SPacketSendRecipes;
import top.theillusivec4.polymorph.common.util.CraftingPlayers;
import top.theillusivec4.polymorph.common.util.RecipeCache;

public class TinkersConstructHooks {

  public static <T extends IRecipe<C>, C extends IInventory> Optional<T> getRecipe(
      RecipeManager recipeManager, IRecipeType<T> type, C inventory, World world,
      PlayerEntity player, RecipeCache cache) {
    List<T> recipes = recipeManager.getRecipes(type, inventory, world);

    if (recipes.isEmpty()) {

      if (player instanceof ServerPlayerEntity) {
        PolymorphNetwork.INSTANCE.send(
            PacketDistributor.PLAYER.with(() -> (ServerPlayerEntity) player),
            new SPacketSendRecipes(new HashSet<>(), null));
      }
      return Optional.empty();
    }
    cache.setLastRecipes((List<IRecipe<?>>) recipes);
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
          new SPacketSendRecipes(recipeIds, new ResourceLocation("")));
    }
    return Optional.of(result != null ? result : recipes.get(0));
  }

  public static void sendEmptyRecipes(CraftingStationTileEntity te, @Nullable PlayerEntity player) {

    if (te.isEmpty() && player instanceof ServerPlayerEntity) {
      PolymorphNetwork.INSTANCE.send(
          PacketDistributor.PLAYER.with(() -> (ServerPlayerEntity) player),
          new SPacketSendRecipes(new HashSet<>(), new ResourceLocation("")));
    }
  }

  public static void sendRecipes(PlayerEntity player, List<IRecipe<?>> lastRecipes) {

    if (!lastRecipes.isEmpty() && player instanceof ServerPlayerEntity) {
      Set<ResourceLocation> recipes =
          lastRecipes.stream().map(IRecipe::getId).collect(Collectors.toSet());
      PolymorphNetwork.INSTANCE.send(
          PacketDistributor.PLAYER.with(() -> (ServerPlayerEntity) player),
          new SPacketSendRecipes(recipes, new ResourceLocation("")));
    }
  }
}
