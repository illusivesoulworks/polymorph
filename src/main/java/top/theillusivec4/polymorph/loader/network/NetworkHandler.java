/*
 * Copyright (c) 2020 C4
 *
 * This file is part of Polymorph, a mod made for Minecraft.
 *
 * Polymorph is free software: you can redistribute it and/or modify it under the terms of the GNU
 * Lesser General Public License as published by the Free Software Foundation, either version 3 of
 * the License, or any later version.
 *
 * Polymorph is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without
 * even the implied warranty of MERCHANTABILITY or FITNESS FOR PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with Polymorph.
 * If not, see <https://www.gnu.org/licenses/>.
 */

package top.theillusivec4.polymorph.loader.network;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.CraftingRecipe;
import net.minecraft.recipe.Recipe;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;
import top.theillusivec4.polymorph.api.PolymorphApi;
import top.theillusivec4.polymorph.api.PolymorphComponent;
import top.theillusivec4.polymorph.api.type.CraftingProvider;
import top.theillusivec4.polymorph.api.type.PersistentSelector;
import top.theillusivec4.polymorph.api.type.PolyProvider;
import top.theillusivec4.polymorph.core.Polymorph;

public class NetworkHandler {

  public static void setup() {
    ServerPlayNetworking.registerGlobalReceiver(NetworkPackets.SET_RECIPE,
        (((server, player, handler, buf, responseSender) -> {
          String id = buf.readString(32767);
          server.execute(() -> {

            if (player != null && player.getServer() != null) {
              ScreenHandler container = player.currentScreenHandler;
              Optional<PolyProvider<?, ?>> maybeProvider =
                  PolymorphApi.getInstance().getProvider(container);
              maybeProvider.ifPresent(provider -> {
                if (provider.getInventory() instanceof BlockEntity) {
                  BlockEntity te = (BlockEntity) provider.getInventory();
                  PolymorphComponent.SELECTOR.maybeGet(te).ifPresent(selector -> {
                    Optional<? extends Recipe<?>> recipe =
                        player.getServerWorld().getRecipeManager().get(new Identifier(id));
                    recipe.ifPresent(selector::setSelectedRecipe);
                  });
                }
              });
            }
          });
        })));

    ServerPlayNetworking.registerGlobalReceiver(NetworkPackets.SET_CRAFTING_RECIPE,
        (((server, player, handler, buf, responseSender) -> {
          String id = buf.readString(32767);
          server.execute(() -> {

            if (player != null && player.getServer() != null) {
              ScreenHandler container = player.currentScreenHandler;
              AtomicReference<ItemStack> output = new AtomicReference<>(ItemStack.EMPTY);
              PolymorphApi.getInstance().getProvider(container).ifPresent(provider -> {
                if (provider instanceof CraftingProvider) {
                  CraftingProvider craftingProvider = (CraftingProvider) provider;
                  Slot slot = provider.getOutputSlot();
                  Optional<? extends Recipe<?>> result = player.getServerWorld().getRecipeManager()
                      .get(new Identifier(id));
                  CraftingInventory craftingInventory = craftingProvider.getInventory();
                  result.ifPresent(res -> {

                    if (res instanceof CraftingRecipe) {
                      CraftingRecipe craftingRecipe = (CraftingRecipe) res;

                      if (craftingRecipe.matches(craftingInventory, player.world)) {
                        output.set(craftingRecipe.craft(craftingInventory));
                        slot.inventory.setStack(slot.id, output.get());
                      }
                    }
                  });
                  Polymorph.getLoader().getPacketVendor().syncOutput(output.get(), player);
                }
              });
            }
          });
        })));

    ServerPlayNetworking.registerGlobalReceiver(NetworkPackets.TRANSFER_RECIPE,
        (((server, player, handler, buf, responseSender) -> {
          String id = buf.readString(32767);
          server.execute(() -> {

            if (player != null && player.getServer() != null) {
              ScreenHandler screenHandler = player.currentScreenHandler;
              PolymorphApi.getInstance().getProvider(screenHandler).ifPresent(provider -> {
                if (provider instanceof CraftingProvider) {
                  CraftingProvider craftingProvider = (CraftingProvider) provider;
                  Optional<? extends Recipe<?>> result = player.getServerWorld().getRecipeManager()
                      .get(new Identifier(id));
                  result.ifPresent(res -> {

                    if (res instanceof CraftingRecipe) {
                      CraftingRecipe craftingRecipe = (CraftingRecipe) res;
                      craftingProvider.transfer(player, craftingRecipe);
                    }
                  });
                  Polymorph.getLoader().getPacketVendor().syncOutput(ItemStack.EMPTY, player);
                }
              });
            }
          });
        })));

    ServerPlayNetworking.registerGlobalReceiver(NetworkPackets.FETCH_RECIPES,
        (((server, player, handler, buf, responseSender) -> server.execute(() -> {

          if (player != null && player.getServer() != null) {
            ScreenHandler container = player.currentScreenHandler;
            AtomicReference<String> selectedRecipe = new AtomicReference<>("");
            Optional<PolyProvider<?, ?>> maybeProvider =
                PolymorphApi.getInstance().getProvider(container);
            maybeProvider.ifPresent(provider -> {
              if (provider.getInventory() instanceof BlockEntity) {
                BlockEntity te = (BlockEntity) provider.getInventory();
                Optional<PersistentSelector> maybeSelector =
                    PolymorphComponent.SELECTOR.maybeGet(te);
                selectedRecipe.set(maybeSelector.flatMap(selector -> selector.getSelectedRecipe()
                    .map(recipe -> recipe.getId().toString())).orElse(""));
              }
            });
            World world = player.getServerWorld();
            List<String> recipes = PolymorphApi.getInstance()
                .getProvider(container)
                .map(provider -> provider
                    .getRecipes(world, world.getRecipeManager()).stream()
                    .filter(recipe -> !recipe.getOutput().isEmpty())
                    .map(recipe -> recipe.getId().toString())
                    .collect(Collectors.toList())).orElse(new ArrayList<>());
            Polymorph.getLoader().getPacketVendor()
                .sendRecipes(recipes, selectedRecipe.get(), player);
          }
        }))));
  }

}
