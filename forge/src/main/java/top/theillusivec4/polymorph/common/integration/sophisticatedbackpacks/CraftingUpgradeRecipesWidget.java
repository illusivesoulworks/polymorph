package top.theillusivec4.polymorph.common.integration.sophisticatedbackpacks;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.inventory.container.Slot;
import net.minecraft.util.ResourceLocation;
import net.p3pp3rf1y.sophisticatedbackpacks.client.gui.BackpackScreen;
import net.p3pp3rf1y.sophisticatedbackpacks.common.gui.BackpackContainer;
import net.p3pp3rf1y.sophisticatedbackpacks.common.gui.UpgradeContainerBase;
import net.p3pp3rf1y.sophisticatedbackpacks.upgrades.crafting.CraftingUpgradeContainer;
import net.p3pp3rf1y.sophisticatedbackpacks.upgrades.smelting.SmeltingUpgradeContainer;
import top.theillusivec4.polymorph.api.PolymorphApi;
import top.theillusivec4.polymorph.api.client.base.ITickingRecipesWidget;
import top.theillusivec4.polymorph.api.client.widget.AbstractRecipesWidget;

public class CraftingUpgradeRecipesWidget extends AbstractRecipesWidget implements
    ITickingRecipesWidget {

  private final BackpackContainer backpackContainer;
  private int lastUpgradeId = -1;
  private Slot outputSlot;
  private boolean isCrafting = false;
  private boolean isSmelting = false;

  public CraftingUpgradeRecipesWidget(BackpackScreen pBackpackScreen,
                                      BackpackContainer pBackpackContainer) {
    super(pBackpackScreen);
    this.backpackContainer = pBackpackContainer;
    this.outputSlot = this.backpackContainer.getSlot(0);
    int upgradeId = this.backpackContainer.getOpenContainer().map(
        UpgradeContainerBase::getUpgradeContainerId).orElse(-1);
    UpgradeContainerBase<?, ?> upgradeContainerBase =
        this.backpackContainer.getUpgradeContainers().get(upgradeId);

    if (upgradeContainerBase != null) {
      this.isCrafting = upgradeContainerBase instanceof CraftingUpgradeContainer;
      this.isSmelting = upgradeContainerBase instanceof SmeltingUpgradeContainer;
    }
  }

  @Override
  public void tick() {
    int upgradeId = this.backpackContainer.getOpenContainer().map(
        UpgradeContainerBase::getUpgradeContainerId).orElse(-1);

    if (lastUpgradeId != upgradeId) {
      lastUpgradeId = upgradeId;

      if (upgradeId != -1) {
        UpgradeContainerBase<?, ?> upgradeContainerBase =
            this.backpackContainer.getUpgradeContainers().get(upgradeId);

        if (upgradeContainerBase != null) {

          if (upgradeContainerBase instanceof CraftingUpgradeContainer) {
            this.isCrafting = true;
            this.isSmelting = false;
            this.outputSlot = upgradeContainerBase.getSlots().get(9);
            this.resetWidgetOffsets();
          } else if (upgradeContainerBase instanceof SmeltingUpgradeContainer) {
            this.isSmelting = true;
            this.isCrafting = false;
            this.resetWidgetOffsets();
          } else {
            this.isSmelting = false;
            this.isCrafting = false;
          }
        } else {
          this.isCrafting = false;
          this.isSmelting = false;
        }
      } else {
        this.isCrafting = false;
        this.isSmelting = false;
      }
    }
  }

  @Override
  public void selectRecipe(ResourceLocation pResourceLocation) {
    PolymorphApi.common().getPacketDistributor().sendStackRecipeSelectionC2S(pResourceLocation);
  }

  @Override
  public Slot getOutputSlot() {
    return this.outputSlot;
  }

  @Override
  public void render(MatrixStack pMatrixStack, int pMouseX, int pMouseY,
                     float pRenderPartialTicks) {

    if (isCrafting || isSmelting) {
      super.render(pMatrixStack, pMouseX, pMouseY, pRenderPartialTicks);
    }
  }

  @Override
  public boolean mouseClicked(double pMouseX, double pMouseY, int pButton) {

    if (isCrafting || isSmelting) {
      return super.mouseClicked(pMouseX, pMouseY, pButton);
    }
    return false;
  }

  @Override
  public int getXPos() {
    return getOutputSlot().xPos - 21;
  }

  @Override
  public int getYPos() {
    return getOutputSlot().yPos;
  }
}
