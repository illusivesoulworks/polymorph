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
import java.util.List;
import net.fabricmc.fabric.api.network.ClientSidePacketRegistry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.CraftingRecipe;
import net.minecraft.recipe.RecipeManager;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.util.Identifier;
import top.theillusivec4.polymorph.api.PolymorphApi;
import top.theillusivec4.polymorph.core.client.RecipeSelectionManager;

public class ClientNetworkHandler {

  public static void setup() {
    ClientSidePacketRegistry.INSTANCE
        .register(NetworkPackets.SYNC_OUTPUT, (((packetContext, packetByteBuf) -> {
          ItemStack stack = packetByteBuf.readItemStack();
          packetContext.getTaskQueue().execute(() -> {
            PlayerEntity playerEntity = packetContext.getPlayer();
            ScreenHandler screenHandler = playerEntity.currentScreenHandler;
            PolymorphApi.getProvider(screenHandler).ifPresent(provider -> {
              Slot slot = provider.getOutputSlot();
              slot.inventory.setStack(slot.id, stack);
            });
            RecipeSelectionManager.getInstance().ifPresent(RecipeSelectionManager::unlockUpdates);
          });
        })));

    ClientSidePacketRegistry.INSTANCE
        .register(NetworkPackets.SEND_RECIPES, (((packetContext, packetByteBuf) -> {
          List<String> packetRecipes = new ArrayList<>();

          while (packetByteBuf.isReadable()) {
            packetRecipes.add(packetByteBuf.readString(32767));
          }
          packetContext.getTaskQueue().execute(() -> {
            PlayerEntity playerEntity = packetContext.getPlayer();

            if (playerEntity != null) {
              RecipeManager recipeManager = playerEntity.world.getRecipeManager();
              List<CraftingRecipe> recipes = new ArrayList<>();
              for (String packetRecipe : packetRecipes) {
                recipeManager.get(new Identifier(packetRecipe)).ifPresent(recipe -> {

                  if (recipe instanceof CraftingRecipe) {
                    recipes.add((CraftingRecipe) recipe);
                  }
                });
              }
              RecipeSelectionManager.getInstance().ifPresent(
                  recipeSelectionManager -> recipeSelectionManager
                      .setRecipes(recipes, playerEntity.world, true));
            }
          });
        })));
  }
}
