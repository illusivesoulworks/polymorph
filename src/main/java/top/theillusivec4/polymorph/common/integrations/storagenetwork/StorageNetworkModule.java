/*
 * Copyright (c) 2020 C4
 *
 * This file is part of Polymorph, a mod made for Minecraft.
 *
 * Polymorph is free software: you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published
 * by the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Polymorph is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with Polymorph.  If not, see <https://www.gnu.org/licenses/>.
 */

package top.theillusivec4.polymorph.common.integrations.storagenetwork;

import com.lothrazar.storagenetwork.capability.handler.ItemStackMatcher;
import com.lothrazar.storagenetwork.block.main.TileMain;
import com.lothrazar.storagenetwork.block.request.ContainerNetworkCraftingTable;
import com.lothrazar.storagenetwork.gui.ContainerNetwork;
import com.lothrazar.storagenetwork.gui.NetworkCraftingInventory;
import com.lothrazar.storagenetwork.item.remote.ContainerNetworkCraftingRemote;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.ICraftingRecipe;
import net.minecraft.util.NonNullList;
import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraftforge.items.wrapper.PlayerMainInvWrapper;
import top.theillusivec4.polymorph.Polymorph;
import top.theillusivec4.polymorph.api.PolymorphApi;
import top.theillusivec4.polymorph.api.PolymorphApi.IProvider;

public class StorageNetworkModule {

  public static void setup() {
    PolymorphApi.addProvider(ContainerNetworkCraftingTable.class, StorageNetworkProvider::new);
    PolymorphApi.addProvider(ContainerNetworkCraftingRemote.class, StorageNetworkProvider::new);
  }

  public static boolean isNetworkContainer(Container container) {
    return container instanceof ContainerNetwork;
  }

  public static void transferStackInSlot(ContainerNetwork containerNetwork, PlayerEntity playerIn,
      int slotIndex, ICraftingRecipe recipe) {
    Slot slot = containerNetwork.inventorySlots.get(slotIndex);

    if (slot != null && slot.getHasStack()) {
      TileMain tileMain = containerNetwork.getTileMain();
      craftShift(containerNetwork, playerIn, tileMain, recipe);
    }
  }

  @SuppressWarnings("deprecation")
  public static void craftShift(ContainerNetwork containerNetwork, PlayerEntity player,
      TileMain tile, ICraftingRecipe recipe) {

    if (!containerNetwork.isCrafting() || containerNetwork.matrix == null) {
      return;
    }
    Field f = null;
    try {
      f = ContainerNetwork.class.getDeclaredField("recipeLocked");
      f.setAccessible(true);
      f.set(containerNetwork, true);
    } catch (Exception e) {
      Polymorph.LOGGER.error("Reflection error!", e);
    }
    NetworkCraftingInventory matrix = containerNetwork.matrix;
    ItemStack res = recipe.getCraftingResult(matrix);
    int crafted = 0;
    List<ItemStack> recipeCopy = new ArrayList<>();

    for (int i = 0; i < matrix.getSizeInventory(); i++) {
      recipeCopy.add(matrix.getStackInSlot(i).copy());
    }
    int sizePerCraft = res.getCount();

    while (crafted + sizePerCraft <= res.getMaxStackSize()) {
      res = recipe.getCraftingResult(matrix);

      if (!ItemHandlerHelper
          .insertItemStacked(new PlayerMainInvWrapper(player.inventory), res, true).isEmpty()) {
        break;
      }
      if (!recipe.matches(matrix, player.world)) {
        break;
      }

      if (!player.inventory.addItemStackToInventory(res)) {
        player.dropItem(res, false);
      }
      NonNullList<ItemStack> remainder = recipe.getRemainingItems(matrix);

      for (int i = 0; i < remainder.size(); ++i) {
        ItemStack remainderCurrent = remainder.get(i);
        ItemStack slot = matrix.getStackInSlot(i);

        if (remainderCurrent.isEmpty()) {
          matrix.getStackInSlot(i).shrink(1);
          continue;
        }

        if (slot.getItem().getContainerItem() != null) {
          slot = new ItemStack(slot.getItem().getContainerItem());
          matrix.setInventorySlotContents(i, slot);
        } else if (!slot.getItem().getContainerItem(slot).isEmpty()) {
          slot = slot.getItem().getContainerItem(slot);
          matrix.setInventorySlotContents(i, slot);
        } else if (!remainderCurrent.isEmpty()) {
          if (slot.isEmpty()) {
            matrix.setInventorySlotContents(i, remainderCurrent);
          } else if (ItemStack.areItemsEqual(slot, remainderCurrent) && ItemStack
              .areItemStackTagsEqual(slot, remainderCurrent)) {
            remainderCurrent.grow(slot.getCount());
            matrix.setInventorySlotContents(i, remainderCurrent);
          } else if (ItemStack.areItemsEqualIgnoreDurability(slot, remainderCurrent)) {
            matrix.setInventorySlotContents(i, remainderCurrent);
          } else {
            if (!player.inventory.addItemStackToInventory(remainderCurrent)) {
              player.dropItem(remainderCurrent, false);
            }
          }
        } else if (!slot.isEmpty()) {
          matrix.decrStackSize(i, 1);
        }
      }
      crafted += sizePerCraft;
      ItemStack stackInSlot;
      ItemStack recipeStack;
      ItemStackMatcher itemStackMatcherCurrent;

      for (int i = 0; i < matrix.getSizeInventory(); i++) {
        stackInSlot = matrix.getStackInSlot(i);

        if (stackInSlot.isEmpty()) {
          recipeStack = recipeCopy.get(i);
          itemStackMatcherCurrent =
              !recipeStack.isEmpty() ? new ItemStackMatcher(recipeStack, false, false) : null;
          ItemStack req = tile.request(itemStackMatcherCurrent, 1, false);
          matrix.setInventorySlotContents(i, req);
        }
      }
      containerNetwork.onCraftMatrixChanged(matrix);
    }
    containerNetwork.detectAndSendChanges();

    if (f != null) {
      try {
        f.set(containerNetwork, false);
      } catch (IllegalAccessException e) {
        Polymorph.LOGGER.error("Reflection error!", e);
      }
    }
    containerNetwork.onCraftMatrixChanged(matrix);
  }

  public static class StorageNetworkProvider<T extends ContainerNetwork> implements IProvider {

    private final T containerNetwork;

    public StorageNetworkProvider(T containerNetwork) {
      this.containerNetwork = containerNetwork;
    }

    @Override
    public Container getContainer() {
      return this.containerNetwork;
    }

    @Override
    public int getXOffset() {
      return 13;
    }

    @Override
    public int getYOffset() {
      return -22;
    }
  }
}
