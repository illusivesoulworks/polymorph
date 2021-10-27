package top.theillusivec4.polymorph.mixin.util.integration;

import javax.annotation.Nullable;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.item.crafting.ICraftingRecipe;
import slimeknights.mantle.tileentity.InventoryTileEntity;
import top.theillusivec4.polymorph.api.PolymorphApi;

public class TinkersConstructMixinHooks {

  public static void calcResult(InventoryTileEntity pTileEntity, @Nullable PlayerEntity pPlayer,
                                @Nullable ICraftingRecipe pRecipe,
                                CraftingInventory pCraftingInventory) {

    if (pPlayer instanceof ServerPlayerEntity) {
      PolymorphApi.common().getRecipeData(pTileEntity).ifPresent(recipeData -> {
        if (pRecipe != null && pRecipe.matches(pCraftingInventory, pTileEntity.getWorld())) {
          recipeData.setFailing(false);
        }
      });
    }
  }
}
