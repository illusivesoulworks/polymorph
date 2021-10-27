package top.theillusivec4.polymorph.common.crafting;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import top.theillusivec4.polymorph.api.PolymorphApi;

public class RecipeSelection {

  public static <T extends IRecipe<C>, C extends IInventory> Optional<T> getPlayerRecipe(
      IRecipeType<T> pType, C pInventory, World pWorld, PlayerEntity pPlayer) {
    return getPlayerRecipe(pType, pInventory, pWorld, pPlayer, new ArrayList<>());
  }

  public static <T extends IRecipe<C>, C extends IInventory> Optional<T> getPlayerRecipe(
      IRecipeType<T> pType, C pInventory, World pWorld, PlayerEntity pPlayer, List<T> pRecipes) {
    return PolymorphApi.common().getRecipeData(pPlayer)
        .map(dataset -> dataset.getRecipe(pType, pInventory, pWorld, pRecipes)).orElse(null);
  }

  public static <T extends IRecipe<C>, C extends IInventory> Optional<T> getTileEntityRecipe(
      IRecipeType<T> pType, C pInventory, World pWorld, TileEntity pTileEntity) {
    return PolymorphApi.common().getRecipeData(pTileEntity)
        .map(dataset -> dataset.getRecipe(pType, pInventory, pWorld, new ArrayList<>()))
        .orElse(null);
  }
}
