package top.theillusivec4.polymorph.mixin.util.integration;

import java.util.Map;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.ICraftingRecipe;
import net.minecraft.world.World;
import net.p3pp3rf1y.sophisticatedbackpacks.common.gui.UpgradeContainerBase;
import net.p3pp3rf1y.sophisticatedbackpacks.upgrades.crafting.CraftingUpgradeContainer;
import top.theillusivec4.polymorph.api.PolymorphApi;
import top.theillusivec4.polymorph.api.common.base.IPolymorphCommon;
import top.theillusivec4.polymorph.api.common.base.IPolymorphPacketDistributor;

public class SophisticatedBackpacksMixinHooks {

  public static void updateCraftingResult(World pWorld, ICraftingRecipe pLastRecipe,
                                          CraftingInventory pCraftingInventory,
                                          PlayerEntity pPlayer, ItemStack pUpgradeStack) {

    if (!pWorld.isRemote() && pPlayer instanceof ServerPlayerEntity) {
      IPolymorphCommon commonApi = PolymorphApi.common();
      commonApi.getRecipeData(pUpgradeStack).ifPresent(recipeData -> {
        if (pLastRecipe != null && pLastRecipe.matches(pCraftingInventory, pWorld)) {
          commonApi.getPacketDistributor()
              .sendRecipesListS2C((ServerPlayerEntity) pPlayer, recipeData.getRecipesList());
        }
      });
    }
  }

  public static void onOpenTab(int pId, PlayerEntity pPlayer,
                               Map<Integer, UpgradeContainerBase<?, ?>> pUpgradeContainers) {

    if (!pPlayer.world.isRemote() && pPlayer instanceof ServerPlayerEntity) {
      ServerPlayerEntity serverPlayerEntity = (ServerPlayerEntity) pPlayer;
      UpgradeContainerBase<?, ?> upgrade = pUpgradeContainers.get(pId);

      if (upgrade instanceof CraftingUpgradeContainer) {
        CraftingUpgradeContainer craftingUpgradeContainer = (CraftingUpgradeContainer) upgrade;
        IPolymorphCommon commonApi = PolymorphApi.common();
        IPolymorphPacketDistributor packetDistributor = commonApi.getPacketDistributor();
        commonApi.getRecipeData(upgrade.getUpgradeStack()).ifPresent(
            recipeData -> {
              if (!recipeData.isEmpty(craftingUpgradeContainer.getCraftMatrix())) {
                packetDistributor.sendRecipesListS2C(serverPlayerEntity,
                    recipeData.getRecipesList());
              } else {
                packetDistributor.sendRecipesListS2C(serverPlayerEntity);
              }
            });
      }
    }
  }
}
