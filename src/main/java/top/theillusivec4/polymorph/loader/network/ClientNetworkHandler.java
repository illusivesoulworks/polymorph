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

package top.theillusivec4.polymorph.loader.network;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.slot.Slot;
import top.theillusivec4.polymorph.core.client.selector.CraftingRecipeSelector;
import top.theillusivec4.polymorph.core.client.selector.RecipeSelectorManager;

public class ClientNetworkHandler {

  public static void setup() {
    ClientPlayNetworking.registerGlobalReceiver(NetworkPackets.SYNC_OUTPUT,
        ((((client, handler, buf, responseSender) -> {
          ItemStack stack = buf.readItemStack();
          client.execute(() -> {
            PlayerEntity playerEntity = client.player;

            if (playerEntity != null) {
              RecipeSelectorManager.getSelector().ifPresent(selector -> {
                if (selector instanceof CraftingRecipeSelector) {
                  Slot slot = selector.getProvider().getOutputSlot();
                  slot.inventory.setStack(slot.id, stack);
                  CraftingRecipeSelector craftingRecipeSelector = (CraftingRecipeSelector) selector;
                  craftingRecipeSelector.setUpdatable(true);

                  if (stack.isEmpty()) {
                    craftingRecipeSelector.clearRecipes(playerEntity.world);
                  }
                }
              });
            }
          });
        }))));

    ClientPlayNetworking.registerGlobalReceiver(NetworkPackets.HIGHLIGHT_RECIPE,
        ((((client, handler, buf, responseSender) -> {
          String id = buf.readString(32767);
          client.execute(() -> {
            PlayerEntity playerEntity = client.player;

            if (playerEntity != null) {
              RecipeSelectorManager.getSelector()
                  .ifPresent(selector -> selector.highlightRecipe(id));
            }
          });
        }))));

    ClientPlayNetworking.registerGlobalReceiver(NetworkPackets.SEND_RECIPES,
        ((((client, handler, buf, responseSender) -> {
          List<String> recipes = new ArrayList<>();
          int length = buf.readInt();

          for (int i = 0; i < length; i++) {
            recipes.add(buf.readString(32767));
          }
          String selected = buf.readString(32767);
          client.execute(() -> {
            PlayerEntity playerEntity = client.player;

            if (playerEntity != null) {
              RecipeSelectorManager.getSelector().ifPresent(selector -> selector
                  .setRecipes(new HashSet<>(recipes), playerEntity.world, true, selected));
            }
          });
        }))));
  }
}
