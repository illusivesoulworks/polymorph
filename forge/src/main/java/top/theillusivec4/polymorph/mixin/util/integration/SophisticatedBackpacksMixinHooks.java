package top.theillusivec4.polymorph.mixin.util.integration;

import java.lang.ref.WeakReference;
import java.util.Map;
import java.util.Optional;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipe;
import net.minecraft.item.crafting.ICraftingRecipe;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.p3pp3rf1y.sophisticatedbackpacks.common.gui.UpgradeContainerBase;
import net.p3pp3rf1y.sophisticatedbackpacks.upgrades.crafting.CraftingUpgradeContainer;
import net.p3pp3rf1y.sophisticatedbackpacks.upgrades.smelting.SmeltingUpgradeContainer;
import top.theillusivec4.polymorph.api.PolymorphApi;
import top.theillusivec4.polymorph.api.common.base.IPolymorphCommon;
import top.theillusivec4.polymorph.api.common.base.IPolymorphPacketDistributor;
import top.theillusivec4.polymorph.common.crafting.RecipeSelection;
import top.theillusivec4.polymorph.common.util.PolymorphUtils;
import top.theillusivec4.polymorph.mixin.integration.sophisticatedbackpacks.AccessorRecipeHelper;

public class SophisticatedBackpacksMixinHooks {

  public static void updateCraftingResult(World pWorld, ICraftingRecipe pLastRecipe,
                                          CraftingInventory pCraftingInventory,
                                          PlayerEntity pPlayer, ItemStack pUpgradeStack) {

    if (!pWorld.isRemote() && pPlayer instanceof ServerPlayerEntity) {
      IPolymorphCommon commonApi = PolymorphApi.common();
      commonApi.getRecipeData(pUpgradeStack).ifPresent(recipeData -> {
        if (pLastRecipe != null && pLastRecipe.matches(pCraftingInventory, pWorld)) {
          recipeData.setFailing(false);
          commonApi.getPacketDistributor()
              .sendRecipesListS2C((ServerPlayerEntity) pPlayer, recipeData.getRecipesList());
        }
      });
    }
  }

  public static Optional<FurnaceRecipe> getSmeltingRecipe(ItemStack pInput, ItemStack pUpgrade) {
    WeakReference<World> weakReference = AccessorRecipeHelper.getWorld();

    if (weakReference != null) {
      World world = weakReference.get();

      if (world != null) {
        return RecipeSelection.getStackRecipe(IRecipeType.SMELTING,
            PolymorphUtils.wrapItems(pInput), world, pUpgrade);
      }
    }
    return Optional.empty();
  }

  public static void onOpenTab(int pId, PlayerEntity pPlayer,
                               Map<Integer, UpgradeContainerBase<?, ?>> pUpgradeContainers) {

    if (!pPlayer.world.isRemote() && pPlayer instanceof ServerPlayerEntity) {
      ServerPlayerEntity serverPlayerEntity = (ServerPlayerEntity) pPlayer;
      UpgradeContainerBase<?, ?> upgrade = pUpgradeContainers.get(pId);
      IPolymorphCommon commonApi = PolymorphApi.common();
      IPolymorphPacketDistributor packetDistributor = commonApi.getPacketDistributor();
      IInventory inventory = null;
      boolean sendSelected = false;

      if (upgrade != null) {

        if (upgrade instanceof CraftingUpgradeContainer) {
          inventory = ((CraftingUpgradeContainer) upgrade).getCraftMatrix();
        } else if (upgrade instanceof SmeltingUpgradeContainer) {
          sendSelected = true;
          inventory = PolymorphUtils.wrapItems(upgrade.getSlots().get(0).getStack());
        }
        IInventory finalInventory = inventory;
        boolean finalSendSelected = sendSelected;
        commonApi.getRecipeData(upgrade.getUpgradeStack()).ifPresent(
            recipeData -> {
              if (!recipeData.isEmpty(finalInventory) && !recipeData.isFailing()) {
                ResourceLocation selected = finalSendSelected ?
                    recipeData.getSelectedRecipe().map(IRecipe::getId).orElse(null) : null;
                packetDistributor.sendRecipesListS2C(serverPlayerEntity,
                    recipeData.getRecipesList(),
                    selected);
              } else {
                packetDistributor.sendRecipesListS2C(serverPlayerEntity);
              }
            });
      }
    }
  }
}
