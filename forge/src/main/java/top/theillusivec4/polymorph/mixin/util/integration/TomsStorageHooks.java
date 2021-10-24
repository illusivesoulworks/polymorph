package top.theillusivec4.polymorph.mixin.util.integration;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.inventory.container.IContainerListener;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.world.World;
import top.theillusivec4.polymorph.api.PolymorphApi;
import top.theillusivec4.polymorph.api.common.base.IRecipeData;
import top.theillusivec4.polymorph.common.impl.RecipeData;

public class TomsStorageHooks {

  public static void sendRecipes(World world, CraftingInventory inv,
                                 List<IContainerListener> listeners) {

    if (!world.isRemote()) {
      Set<IRecipeData> recipes =
          world.getRecipeManager().getRecipes(IRecipeType.CRAFTING, inv, world).stream()
              .map(recipe -> new RecipeData(recipe.getId(), recipe.getCraftingResult(inv)))
              .collect(Collectors.toSet());

      for (IContainerListener listener : listeners) {

        if (listener instanceof ServerPlayerEntity) {
          PolymorphApi.common().getPacketDistributor()
              .sendRecipesListS2C((ServerPlayerEntity) listener, recipes);
        }
      }
    }
  }
}
