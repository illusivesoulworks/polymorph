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

public class BackpackUpgradeRecipesWidget extends AbstractRecipesWidget implements
    ITickingRecipesWidget {

  private final BackpackContainer backpackContainer;
  private int lastUpgradeId = -1;
  private Slot outputSlot;
  private boolean isCrafting = false;
  private boolean isSmelting = false;

  public BackpackUpgradeRecipesWidget(BackpackScreen pBackpackScreen,
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

    if (this.lastUpgradeId != upgradeId) {
      this.lastUpgradeId = upgradeId;

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
            this.outputSlot = upgradeContainerBase.getSlots().get(2);
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

    if (this.isCrafting || this.isSmelting) {
      super.render(pMatrixStack, pMouseX, pMouseY, pRenderPartialTicks);
    }
  }

  @Override
  public boolean mouseClicked(double pMouseX, double pMouseY, int pButton) {

    if (this.isCrafting || this.isSmelting) {
      return super.mouseClicked(pMouseX, pMouseY, pButton);
    }
    return false;
  }

  @Override
  public int getXPos() {
    int xOffset = this.isCrafting ? -21 : 0;
    return getOutputSlot().x - xOffset;
  }

  @Override
  public int getYPos() {
    int yOffset = this.isCrafting ? 0 : -23;
    return getOutputSlot().y + yOffset;
  }
}
