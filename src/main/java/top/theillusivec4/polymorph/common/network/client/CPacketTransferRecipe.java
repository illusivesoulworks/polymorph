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

package top.theillusivec4.polymorph.common.network.client;

import com.lothrazar.storagenetwork.gui.ContainerNetwork;
import java.util.Optional;
import java.util.function.Supplier;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.ICraftingRecipe;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.network.NetworkEvent.Context;
import net.minecraftforge.fml.network.PacketDistributor;
import top.theillusivec4.polymorph.Polymorph;
import top.theillusivec4.polymorph.api.PolymorphApi;
import top.theillusivec4.polymorph.common.integrations.fastbench.FastWorkbenchModule;
import top.theillusivec4.polymorph.common.integrations.storagenetwork.StorageNetworkModule;
import top.theillusivec4.polymorph.common.network.NetworkHandler;
import top.theillusivec4.polymorph.common.network.server.SPacketSyncOutput;

public class CPacketTransferRecipe {

  private final String recipe;

  public CPacketTransferRecipe(String recipe) {
    this.recipe = recipe;
  }

  public static void encode(CPacketTransferRecipe msg, PacketBuffer buf) {
    buf.writeString(msg.recipe);
  }

  public static CPacketTransferRecipe decode(PacketBuffer buf) {
    return new CPacketTransferRecipe(buf.readString(32767));
  }

  public static void handle(CPacketTransferRecipe msg, Supplier<Context> ctx) {
    ctx.get().enqueueWork(() -> {
      ServerPlayerEntity sender = ctx.get().getSender();

      if (sender != null) {
        Container container = sender.openContainer;

        PolymorphApi.getProvider(container).ifPresent(provider -> {
          Slot slot = provider.getOutputSlot();
          Optional<? extends IRecipe<?>> result = sender.getServerWorld().getRecipeManager()
              .getRecipe(new ResourceLocation(msg.recipe));
          CraftingInventory finalCraftingInventory = provider.getCraftingInventory();
          result.ifPresent(res -> {

            if (res instanceof ICraftingRecipe && finalCraftingInventory != null) {
              ICraftingRecipe craftingRecipe = (ICraftingRecipe) res;

              if (Polymorph.isFastBenchLoaded) {
                FastWorkbenchModule.setLastRecipe(container, craftingRecipe);
              }
              // Handler for special transfer logic
              if (Polymorph.isStorageNetworkLoaded && StorageNetworkModule
                  .isNetworkContainer(container)) {
                StorageNetworkModule
                    .transferStackInSlot((ContainerNetwork) container, sender, slot.slotNumber,
                        craftingRecipe);
              } else {
                ItemStack itemstack = container.transferStackInSlot(sender, slot.slotNumber);

                if (craftingRecipe.matches(finalCraftingInventory, sender.world)) {
                  slot.putStack(craftingRecipe.getCraftingResult(finalCraftingInventory));

                  while (!itemstack.isEmpty() && ItemStack
                      .areItemsEqual(slot.getStack(), itemstack)) {
                    itemstack = container.transferStackInSlot(sender, slot.slotNumber);

                    if (craftingRecipe.matches(finalCraftingInventory, sender.world)) {
                      slot.putStack(craftingRecipe.getCraftingResult(finalCraftingInventory));
                    }
                  }
                }
              }
            }
          });
        });
        NetworkHandler.INSTANCE.send(PacketDistributor.PLAYER.with(() -> sender),
            new SPacketSyncOutput(ItemStack.EMPTY));
      }
    });
    ctx.get().setPacketHandled(true);
  }
}
