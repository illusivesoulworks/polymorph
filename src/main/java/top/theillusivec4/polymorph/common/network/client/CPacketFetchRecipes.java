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

package top.theillusivec4.polymorph.common.network.client;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.container.Container;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkEvent.Context;
import net.minecraftforge.fml.network.PacketDistributor;
import top.theillusivec4.polymorph.api.PolymorphApi;
import top.theillusivec4.polymorph.api.PolymorphCapability;
import top.theillusivec4.polymorph.api.type.IPersistentSelector;
import top.theillusivec4.polymorph.api.type.IPolyProvider;
import top.theillusivec4.polymorph.common.network.NetworkManager;
import top.theillusivec4.polymorph.common.network.server.SPacketSendRecipes;

public class CPacketFetchRecipes {

  public static void encode(CPacketFetchRecipes msg, PacketBuffer buf) {
  }

  public static CPacketFetchRecipes decode(PacketBuffer buf) {
    return new CPacketFetchRecipes();
  }

  public static void handle(CPacketFetchRecipes msg, Supplier<Context> ctx) {
    ctx.get().enqueueWork(() -> {
      ServerPlayerEntity sender = ctx.get().getSender();

      if (sender != null) {
        Container container = sender.openContainer;
        AtomicReference<String> selectedRecipe = new AtomicReference<>("");
        Optional<IPolyProvider<?, ?>> maybeProvider =
            PolymorphApi.getInstance().getProvider(container);
        maybeProvider.ifPresent(provider -> {
          if (provider.getInventory() instanceof TileEntity) {
            TileEntity te = (TileEntity) provider.getInventory();
            Optional<IPersistentSelector> maybeSelector =
                te.getCapability(PolymorphCapability.PERSISTENT_SELECTOR).resolve();
            selectedRecipe.set(maybeSelector.flatMap(
                selector -> selector.getSelectedRecipe().map(recipe -> recipe.getId().toString()))
                .orElse(""));
          }
        });

        World world = sender.getServerWorld();
        List<String> recipes = PolymorphApi.getInstance()
            .getProvider(container)
            .map(provider -> provider
                .getRecipes(world, world.getRecipeManager()).stream()
                .map(recipe -> recipe.getId().toString())
                .collect(Collectors.toList())).orElse(new ArrayList<>());
        NetworkManager.INSTANCE
            .send(PacketDistributor.PLAYER.with(() -> sender),
                new SPacketSendRecipes(recipes, selectedRecipe.get()));
      }
    });
    ctx.get().setPacketHandled(true);
  }
}
