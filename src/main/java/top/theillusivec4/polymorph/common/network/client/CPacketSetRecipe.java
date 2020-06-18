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

import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;
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
import top.theillusivec4.polymorph.api.PolymorphApi;
import top.theillusivec4.polymorph.common.network.NetworkHandler;
import top.theillusivec4.polymorph.common.network.server.SPacketSyncOutput;

public class CPacketSetRecipe {

  private final String recipe;

  public CPacketSetRecipe(String recipe) {
    this.recipe = recipe;
  }

  public static void encode(CPacketSetRecipe msg, PacketBuffer buf) {
    buf.writeString(msg.recipe);
  }

  public static CPacketSetRecipe decode(PacketBuffer buf) {
    return new CPacketSetRecipe(buf.readString(32767));
  }

  public static void handle(CPacketSetRecipe msg, Supplier<Context> ctx) {
    ctx.get().enqueueWork(() -> {
      ServerPlayerEntity sender = ctx.get().getSender();

      if (sender != null) {
        Container container = sender.openContainer;
        AtomicReference<ItemStack> output = new AtomicReference<>(ItemStack.EMPTY);
        PolymorphApi.getProvider(container).ifPresent(provider -> {
          Slot slot = provider.getOutputSlot();
          Optional<? extends IRecipe<?>> result = sender.getServerWorld().getRecipeManager()
              .getRecipe(new ResourceLocation(msg.recipe));
          CraftingInventory finalCraftingInventory = provider.getCraftingMatrix();
          result.ifPresent(res -> {

            if (res instanceof ICraftingRecipe && finalCraftingInventory != null) {
              ICraftingRecipe craftingRecipe = (ICraftingRecipe) res;

              if (craftingRecipe.matches(finalCraftingInventory, sender.world)) {
                output.set(craftingRecipe.getCraftingResult(finalCraftingInventory));
                slot.putStack(output.get());
              }
            }
          });
        });
        NetworkHandler.INSTANCE
            .send(PacketDistributor.PLAYER.with(() -> sender), new SPacketSyncOutput(output.get()));
      }
    });
    ctx.get().setPacketHandled(true);
  }
}
