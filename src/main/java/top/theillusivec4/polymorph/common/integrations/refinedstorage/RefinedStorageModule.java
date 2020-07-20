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

package top.theillusivec4.polymorph.common.integrations.refinedstorage;

import com.refinedmods.refinedstorage.api.network.INetwork;
import com.refinedmods.refinedstorage.api.network.grid.GridType;
import com.refinedmods.refinedstorage.api.network.grid.INetworkAwareGrid;
import com.refinedmods.refinedstorage.api.util.Action;
import com.refinedmods.refinedstorage.api.util.IStackList;
import com.refinedmods.refinedstorage.apiimpl.API;
import com.refinedmods.refinedstorage.container.GridContainer;
import com.refinedmods.refinedstorage.tile.grid.portable.IPortableGrid;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.CraftResultInventory;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.ICraftingRecipe;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.fml.hooks.BasicEventHooks;
import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraftforge.items.SlotItemHandler;
import top.theillusivec4.polymorph.Polymorph;
import top.theillusivec4.polymorph.api.PolyProvider;
import top.theillusivec4.polymorph.api.PolymorphApi;
import top.theillusivec4.polymorph.common.integrations.CompatibilityModule;

public class RefinedStorageModule extends CompatibilityModule {

  public void setup() {
    PolymorphApi.addProvider(GridContainer.class, GridProvider::new);
  }

  public static class GridProvider implements PolyProvider {

    private final GridContainer gridContainer;

    public GridProvider(GridContainer gridContainer) {
      this.gridContainer = gridContainer;
    }

    @Override
    public Container getContainer() {
      return this.gridContainer;
    }

    @Override
    public boolean isActive() {
      return gridContainer.getGrid().getGridType() == GridType.CRAFTING;
    }

    @Override
    public int getXOffset() {
      return 21;
    }

    @Override
    public int getYOffset() {
      return -42;
    }

    // The rest of this file is derived directly from Refined Storage by raoulvdberge
    // Refined Storage is licensed under the MIT License
    @Override
    public void transfer(PlayerEntity playerIn, ICraftingRecipe recipe) {
      Slot slot = gridContainer.inventorySlots.get(getOutputSlot().slotNumber);

      if (gridContainer.getGrid() instanceof IPortableGrid && slot instanceof SlotItemHandler
          && ((SlotItemHandler) slot).getItemHandler()
          .equals(((IPortableGrid) gridContainer.getGrid()).getDisk())) {
        return;
      }

      if (this.gridContainer.getGrid() instanceof INetworkAwareGrid) {
        this.onCraftedShift((INetworkAwareGrid) gridContainer.getGrid(), playerIn, recipe);
      } else {
        gridContainer.getGrid().onCraftedShift(playerIn);
      }
      gridContainer.detectAndSendChanges();
    }

    public void onCraftedShift(INetworkAwareGrid grid, PlayerEntity player,
        ICraftingRecipe recipe) {
      CraftingInventory matrix = grid.getCraftingMatrix();

      if (matrix != null) {
        INetwork network = grid.getNetwork();
        List<ItemStack> craftedItemsList = new ArrayList<>();
        CraftResultInventory result = grid.getCraftingResult();
        ItemStack crafted = result != null ? result.getStackInSlot(0) : ItemStack.EMPTY;
        int maxCrafted = crafted.getMaxStackSize();
        int amountCrafted = 0;
        boolean useNetwork = network != null;
        IStackList<ItemStack> availableItems = null;

        if (useNetwork) {
          availableItems = this.createFilteredItemList(network, matrix);
        }
        IStackList<ItemStack> usedItems = API.instance().createItemStackList();
        ForgeHooks.setCraftingPlayer(player);

        do {
          API.instance().getCraftingGridBehavior()
              .onCrafted(grid, recipe, player, availableItems, usedItems);
          craftedItemsList.add(crafted.copy());
          amountCrafted += crafted.getCount();
          ItemStack stack = recipe.getCraftingResult(matrix);
          Polymorph.LOGGER.info(stack.toString());
        } while (API.instance().getComparer().isEqual(crafted, recipe.getCraftingResult(matrix))
            && amountCrafted < maxCrafted && amountCrafted + crafted.getCount() <= maxCrafted
            && recipe.matches(matrix, player.world));

        if (useNetwork) {
          usedItems.getStacks().forEach((stack) -> network
              .extractItem(stack.getStack(), (stack.getStack()).getCount(), Action.PERFORM));
        }

        for (ItemStack craftedItem : craftedItemsList) {

          if (!player.inventory.addItemStackToInventory(craftedItem.copy())) {
            ItemStack remainder = craftedItem;
            if (useNetwork) {
              remainder = network.insertItem(craftedItem, craftedItem.getCount(), Action.PERFORM);
            }

            if (!remainder.isEmpty()) {
              InventoryHelper
                  .spawnItemStack(player.getEntityWorld(), player.getPositionVec().getX(),
                      player.getPositionVec().getY(), player.getPositionVec().getZ(), remainder);
            }
          }
        }
        crafted.onCrafting(player.world, player, amountCrafted);
        BasicEventHooks.firePlayerCraftingEvent(player,
            ItemHandlerHelper.copyStackWithSize(crafted, amountCrafted), grid.getCraftingMatrix());
        ForgeHooks.setCraftingPlayer(null);
      }
    }

    private IStackList<ItemStack> createFilteredItemList(INetwork network,
        CraftingInventory matrix) {
      IStackList<ItemStack> availableItems = API.instance().createItemStackList();

      for (int i = 0; i < matrix.getSizeInventory(); ++i) {
        ItemStack stack = network.getItemStorageCache().getList().get(matrix.getStackInSlot(i));

        if (stack != null && availableItems.get(stack) == null) {
          availableItems.add(stack);
        }
      }
      return availableItems;
    }
  }
}
