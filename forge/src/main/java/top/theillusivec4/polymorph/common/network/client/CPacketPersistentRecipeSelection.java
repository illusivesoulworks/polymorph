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

package top.theillusivec4.polymorph.common.network.client;

import java.util.Optional;
import java.util.function.Supplier;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.container.Container;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkEvent;
import top.theillusivec4.polymorph.api.PolymorphApi;
import top.theillusivec4.polymorph.common.integration.AbstractCompatibilityModule;
import top.theillusivec4.polymorph.common.integration.PolymorphIntegrations;

public class CPacketPersistentRecipeSelection {

  private final ResourceLocation recipe;

  public CPacketPersistentRecipeSelection(ResourceLocation pResourceLocation) {
    this.recipe = pResourceLocation;
  }

  public static void encode(CPacketPersistentRecipeSelection pPacket, PacketBuffer pBuffer) {
    pBuffer.writeResourceLocation(pPacket.recipe);
  }

  public static CPacketPersistentRecipeSelection decode(PacketBuffer pBuffer) {
    return new CPacketPersistentRecipeSelection(pBuffer.readResourceLocation());
  }

  public static void handle(CPacketPersistentRecipeSelection pPacket,
                            Supplier<NetworkEvent.Context> pContext) {
    pContext.get().enqueueWork(() -> {
      ServerPlayerEntity sender = pContext.get().getSender();

      if (sender != null) {
        World world = sender.getCommandSenderWorld();
        Optional<? extends IRecipe<?>> maybeRecipe =
            world.getRecipeManager().byKey(pPacket.recipe);
        maybeRecipe.ifPresent(recipe -> {
          Container container = sender.containerMenu;
          PolymorphApi.common().getRecipeDataFromTileEntity(container)
              .ifPresent(recipeData -> {
                recipeData.selectRecipe(recipe);

                for (AbstractCompatibilityModule integration : PolymorphIntegrations.get()) {

                  if (integration.selectRecipe(recipeData.getOwner(), recipe) ||
                      integration.selectRecipe(container, recipe)) {
                    return;
                  }
                }
              });
        });
      }
    });
    pContext.get().setPacketHandled(true);
  }
}
