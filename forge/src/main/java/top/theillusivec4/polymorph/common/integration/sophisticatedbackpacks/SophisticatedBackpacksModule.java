package top.theillusivec4.polymorph.common.integration.sophisticatedbackpacks;

import java.lang.ref.WeakReference;
import java.util.Map;
import java.util.Optional;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipe;
import net.minecraft.item.crafting.ICraftingRecipe;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.p3pp3rf1y.sophisticatedbackpacks.client.gui.BackpackScreen;
import net.p3pp3rf1y.sophisticatedbackpacks.common.gui.BackpackContainer;
import net.p3pp3rf1y.sophisticatedbackpacks.common.gui.UpgradeContainerBase;
import net.p3pp3rf1y.sophisticatedbackpacks.upgrades.crafting.CraftingUpgradeContainer;
import net.p3pp3rf1y.sophisticatedbackpacks.upgrades.crafting.CraftingUpgradeItem;
import net.p3pp3rf1y.sophisticatedbackpacks.upgrades.smelting.SmeltingLogic;
import net.p3pp3rf1y.sophisticatedbackpacks.upgrades.smelting.SmeltingLogicContainer;
import net.p3pp3rf1y.sophisticatedbackpacks.upgrades.smelting.SmeltingUpgradeContainer;
import net.p3pp3rf1y.sophisticatedbackpacks.upgrades.smelting.SmeltingUpgradeItem;
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
      if (pStack.getItem() instanceof CraftingUpgradeItem) {
        return new StackRecipeData(pStack);
      } else if (pStack.getItem() instanceof SmeltingUpgradeItem) {
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
          pContainerScreen.getContainer() instanceof BackpackContainer) {
        return new BackpackUpgradeRecipesWidget((BackpackScreen) pContainerScreen,
            (BackpackContainer) pContainerScreen.getContainer());
      }
      return null;
    });
  }

  @Override
  public boolean selectRecipe(Container container, IRecipe<?> recipe) {

    if (container instanceof BackpackContainer) {
      return ((BackpackContainer) container).getOpenContainer().map(upgradeContainerBase -> {
        if (upgradeContainerBase instanceof CraftingUpgradeContainer) {

          if (recipe instanceof ICraftingRecipe) {
            AccessorCraftingUpgradeContainer craftingUpgradeContainer =
                (AccessorCraftingUpgradeContainer) upgradeContainerBase;
            craftingUpgradeContainer.setLastRecipe((ICraftingRecipe) recipe);
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
        }
        return false;
      }).orElse(false);
    }
    return false;
  }

  @Override
  public boolean openContainer(Container container, ServerPlayerEntity serverPlayerEntity) {

    if (container instanceof BackpackContainer) {
      IPolymorphCommon commonApi = PolymorphApi.common();
      commonApi.getRecipeDataFromItemStack(container).ifPresent(
          recipeData -> ((BackpackContainer) container).getOpenContainer()
              .ifPresent(upgradeContainerBase -> {
                if (upgradeContainerBase instanceof CraftingUpgradeContainer) {
                  IInventory inv =
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
                          .getSmeltingSlots().get(0).getHasStack();

                  if (hasInput) {
                    ResourceLocation rl =
                        recipeData.getSelectedRecipe().map(IRecipe::getId).orElse(null);
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
