/*
 * Copyright (C) 2020-2021 C4
 *
 * This file is part of Polymorph.
 *
 * Polymorph is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Polymorph is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * and the GNU Lesser General Public License along with Polymorph.
 * If not, see <https://www.gnu.org/licenses/>.
 *
 */

package top.theillusivec4.polymorph.common.integration.sophisticatedbackpacks;

import java.lang.ref.WeakReference;
import java.util.Map;
import java.util.Optional;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SmeltingRecipe;
import net.minecraft.world.level.Level;
import net.p3pp3rf1y.sophisticatedbackpacks.client.gui.BackpackScreen;
import net.p3pp3rf1y.sophisticatedbackpacks.common.gui.BackpackContainer;
import net.p3pp3rf1y.sophisticatedbackpacks.common.gui.UpgradeContainerBase;
import net.p3pp3rf1y.sophisticatedbackpacks.upgrades.crafting.CraftingUpgradeContainer;
import net.p3pp3rf1y.sophisticatedbackpacks.upgrades.crafting.CraftingUpgradeItem;
import net.p3pp3rf1y.sophisticatedbackpacks.upgrades.smelting.AutoSmeltingUpgradeContainer;
import net.p3pp3rf1y.sophisticatedbackpacks.upgrades.smelting.AutoSmeltingUpgradeItem;
import net.p3pp3rf1y.sophisticatedbackpacks.upgrades.smelting.SmeltingLogic;
import net.p3pp3rf1y.sophisticatedbackpacks.upgrades.smelting.SmeltingLogicContainer;
import net.p3pp3rf1y.sophisticatedbackpacks.upgrades.smelting.SmeltingUpgradeContainer;
import net.p3pp3rf1y.sophisticatedbackpacks.upgrades.smelting.SmeltingUpgradeItem;
import net.p3pp3rf1y.sophisticatedbackpacks.util.RecipeHelper;
import top.theillusivec4.polymorph.api.PolymorphApi;
import top.theillusivec4.polymorph.api.common.base.IPolymorphCommon;
import top.theillusivec4.polymorph.api.common.base.IPolymorphPacketDistributor;
import top.theillusivec4.polymorph.common.capability.StackRecipeData;
import top.theillusivec4.polymorph.common.crafting.RecipeSelection;
import top.theillusivec4.polymorph.common.integration.AbstractCompatibilityModule;
import top.theillusivec4.polymorph.common.util.PolymorphUtils;
import top.theillusivec4.polymorph.mixin.integration.sophisticatedbackpacks.AccessorCraftingUpgradeContainer;
import top.theillusivec4.polymorph.mixin.integration.sophisticatedbackpacks.AccessorRecipeHelper;
import top.theillusivec4.polymorph.mixin.integration.sophisticatedbackpacks.AccessorSmeltingLogic;
import top.theillusivec4.polymorph.mixin.integration.sophisticatedbackpacks.AccessorSmeltingLogicContainer;

public class SophisticatedBackpacksModule extends AbstractCompatibilityModule {

  @Override
  public void setup() {
    PolymorphApi.common().registerItemStack2RecipeData(pStack -> {
      Item item = pStack.getItem();
      if (item instanceof CraftingUpgradeItem) {
        return new StackRecipeData(pStack);
      } else if (item instanceof SmeltingUpgradeItem || item instanceof AutoSmeltingUpgradeItem) {
        return new SmeltingUpgradeStackRecipeData(pStack);
      }
      return null;
    });
    PolymorphApi.common().registerContainer2ItemStack(pContainer -> {
      if (pContainer instanceof BackpackContainer) {
        return ((BackpackContainer) pContainer).getOpenContainer()
            .map(UpgradeContainerBase::getUpgradeStack).orElse(ItemStack.EMPTY);
      }
      return ItemStack.EMPTY;
    });
  }

  @Override
  public void clientSetup() {
    PolymorphApi.client().registerWidget(pContainerScreen -> {
      if (pContainerScreen instanceof BackpackScreen &&
          pContainerScreen.getMenu() instanceof BackpackContainer) {
        return new BackpackUpgradeRecipesWidget((BackpackScreen) pContainerScreen,
            (BackpackContainer) pContainerScreen.getMenu());
      }
      return null;
    });
  }

  @Override
  public boolean selectRecipe(AbstractContainerMenu container, Recipe<?> recipe) {

    if (container instanceof BackpackContainer) {
      return ((BackpackContainer) container).getOpenContainer().map(upgradeContainerBase -> {
        if (upgradeContainerBase instanceof CraftingUpgradeContainer) {

          if (recipe instanceof CraftingRecipe) {
            AccessorCraftingUpgradeContainer craftingUpgradeContainer =
                (AccessorCraftingUpgradeContainer) upgradeContainerBase;
            craftingUpgradeContainer.setLastRecipe((CraftingRecipe) recipe);
            craftingUpgradeContainer.callOnCraftMatrixChanged(null);
            ((BackpackContainer) container).sendSlotUpdates();
            return true;
          }
        } else if (upgradeContainerBase instanceof SmeltingUpgradeContainer) {
          SmeltingLogicContainer smeltingLogicContainer =
              ((SmeltingUpgradeContainer) upgradeContainerBase).getSmeltingLogicContainer();
          SmeltingLogic logic =
              ((AccessorSmeltingLogicContainer) smeltingLogicContainer).getSupplySmeltingLogic()
                  .get();
          ((AccessorSmeltingLogic) logic).setSmeltingRecipeInitialized(false);
          return true;
        } else if (upgradeContainerBase instanceof AutoSmeltingUpgradeContainer) {
          SmeltingLogicContainer smeltingLogicContainer =
              ((AutoSmeltingUpgradeContainer) upgradeContainerBase).getSmeltingLogicContainer();
          SmeltingLogic logic =
              ((AccessorSmeltingLogicContainer) smeltingLogicContainer).getSupplySmeltingLogic()
                  .get();
          ((AccessorSmeltingLogic) logic).setSmeltingRecipeInitialized(false);
        }
        return false;
      }).orElse(false);
    }
    return false;
  }

  @Override
  public boolean openContainer(AbstractContainerMenu container, ServerPlayer serverPlayerEntity) {

    if (container instanceof BackpackContainer) {
      IPolymorphCommon commonApi = PolymorphApi.common();
      commonApi.getRecipeDataFromItemStack(container).ifPresent(
          recipeData -> ((BackpackContainer) container).getOpenContainer()
              .ifPresent(upgradeContainerBase -> {
                if (upgradeContainerBase instanceof CraftingUpgradeContainer) {
                  Container inv =
                      ((CraftingUpgradeContainer) upgradeContainerBase).getCraftMatrix();

                  if (inv.isEmpty()) {
                    commonApi.getPacketDistributor().sendRecipesListS2C(serverPlayerEntity);
                  } else {
                    commonApi.getPacketDistributor()
                        .sendRecipesListS2C(serverPlayerEntity, recipeData.getRecipesList());
                  }
                } else if (upgradeContainerBase instanceof SmeltingUpgradeContainer) {
                  boolean hasInput =
                      ((SmeltingUpgradeContainer) upgradeContainerBase).getSmeltingLogicContainer()
                          .getSmeltingSlots().get(0).hasItem();

                  if (hasInput) {
                    ResourceLocation rl =
                        recipeData.getSelectedRecipe().map(Recipe::getId).orElse(null);
                    commonApi.getPacketDistributor()
                        .sendRecipesListS2C(serverPlayerEntity, recipeData.getRecipesList(), rl);
                  } else {
                    commonApi.getPacketDistributor().sendRecipesListS2C(serverPlayerEntity);
                  }
                } else if (upgradeContainerBase instanceof AutoSmeltingUpgradeContainer) {
                  boolean hasInput =
                      ((AutoSmeltingUpgradeContainer) upgradeContainerBase).getSmeltingLogicContainer()
                          .getSmeltingSlots().get(0).hasItem();

                  if (hasInput) {
                    ResourceLocation rl =
                        recipeData.getSelectedRecipe().map(Recipe::getId).orElse(null);
                    commonApi.getPacketDistributor()
                        .sendRecipesListS2C(serverPlayerEntity, recipeData.getRecipesList(), rl);
                  } else {
                    commonApi.getPacketDistributor().sendRecipesListS2C(serverPlayerEntity);
                  }
                }
              }));
      return true;
    }
    return false;
  }

  public static void updateCraftingResult(Level pWorld, CraftingRecipe pLastRecipe,
                                          CraftingContainer pCraftingInventory,
                                          Player pPlayer, ItemStack pUpgradeStack) {

    if (!pWorld.isClientSide() && pPlayer instanceof ServerPlayer) {
      IPolymorphCommon commonApi = PolymorphApi.common();
      commonApi.getRecipeData(pUpgradeStack).ifPresent(recipeData -> {
        if (pLastRecipe != null && pLastRecipe.matches(pCraftingInventory, pWorld)) {
          recipeData.setFailing(false);
          commonApi.getPacketDistributor()
              .sendRecipesListS2C((ServerPlayer) pPlayer, recipeData.getRecipesList());
        }
      });
    }
  }

  public static Optional<SmeltingRecipe> getSmeltingRecipe(ItemStack pInput, ItemStack pUpgrade) {
    WeakReference<Level> weakReference = AccessorRecipeHelper.getWorld();

    if (weakReference != null) {
      Level world = weakReference.get();

      if (world != null) {
        return RecipeSelection.getStackRecipe(RecipeType.SMELTING,
            PolymorphUtils.wrapItems(pInput), world, pUpgrade);
      }
    }
    return RecipeHelper.getSmeltingRecipe(pInput);
  }

  public static void onOpenTab(int pId, Player pPlayer,
                               Map<Integer, UpgradeContainerBase<?, ?>> pUpgradeContainers) {

    if (!pPlayer.level.isClientSide() && pPlayer instanceof ServerPlayer serverPlayerEntity) {
      UpgradeContainerBase<?, ?> upgrade = pUpgradeContainers.get(pId);
      IPolymorphCommon commonApi = PolymorphApi.common();
      IPolymorphPacketDistributor packetDistributor = commonApi.getPacketDistributor();
      Container inventory = null;
      boolean sendSelected = false;

      if (upgrade != null) {

        if (upgrade instanceof CraftingUpgradeContainer) {
          inventory = ((CraftingUpgradeContainer) upgrade).getCraftMatrix();
        } else if (upgrade instanceof SmeltingUpgradeContainer) {
          sendSelected = true;
          inventory = PolymorphUtils.wrapItems(upgrade.getSlots().get(0).getItem());
        } else if (upgrade instanceof AutoSmeltingUpgradeContainer) {
          sendSelected = true;
          inventory = PolymorphUtils.wrapItems(upgrade.getSlots().get(14).getItem());
        }
        Container finalInventory = inventory;
        boolean finalSendSelected = sendSelected;
        commonApi.getRecipeData(upgrade.getUpgradeStack()).ifPresent(
            recipeData -> {
              if (!recipeData.isEmpty(finalInventory) && !recipeData.isFailing()) {
                ResourceLocation selected = finalSendSelected ?
                    recipeData.getSelectedRecipe().map(Recipe::getId).orElse(null) : null;
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
