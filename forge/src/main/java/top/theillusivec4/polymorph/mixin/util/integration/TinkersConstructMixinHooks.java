package top.theillusivec4.polymorph.mixin.util.integration;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import javax.annotation.Nullable;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.util.LazyOptional;
import slimeknights.mantle.tileentity.InventoryTileEntity;
import top.theillusivec4.polymorph.api.PolymorphApi;
import top.theillusivec4.polymorph.api.common.base.IPolymorphCommon;
import top.theillusivec4.polymorph.api.common.base.IRecipeData;
import top.theillusivec4.polymorph.api.common.capability.IRecipeDataset;
import top.theillusivec4.polymorph.common.crafting.RecipeSelection;

public class TinkersConstructMixinHooks {

  public static void calcResult(InventoryTileEntity pTileEntity, @Nullable PlayerEntity pPlayer) {

    if (pPlayer instanceof ServerPlayerEntity) {
      IPolymorphCommon commonApi = PolymorphApi.common();

      if (pTileEntity.isEmpty()) {
        commonApi.getPacketDistributor()
            .sendRecipesListS2C((ServerPlayerEntity) pPlayer);
      } else {
        Set<IRecipeData> dataset =
            commonApi.getDatasetCapability(pTileEntity).map(IRecipeDataset::getRecipeDataset)
                .orElse(new HashSet<>());
        commonApi.getPacketDistributor().sendRecipesListS2C((ServerPlayerEntity) pPlayer, dataset);
      }
    }
  }

  public static <T extends IRecipe<C>, C extends IInventory> Optional<T> getRecipe(
      IRecipeType<T> pType, C pInventory, World pWorld, PlayerEntity pPlayer,
      TileEntity tileEntity) {
    LazyOptional<IRecipeDataset> cap = PolymorphApi.common().getDatasetCapability(tileEntity);

    if (cap.isPresent()) {
      IRecipeDataset data = cap.resolve().get();
      return RecipeSelection.getRecipe(pType, pInventory, pWorld, pPlayer,
          data::saveRecipeDataset);
    }
    return getRecipe(pType, pInventory, pWorld, pPlayer, null);
  }
}
