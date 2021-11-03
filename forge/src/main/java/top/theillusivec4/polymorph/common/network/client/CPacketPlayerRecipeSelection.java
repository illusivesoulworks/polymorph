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

import java.util.function.Supplier;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.container.AbstractRepairContainer;
import net.minecraft.inventory.container.Container;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.network.NetworkEvent;
import top.theillusivec4.polymorph.api.PolymorphApi;
import top.theillusivec4.polymorph.common.integration.AbstractCompatibilityModule;
import top.theillusivec4.polymorph.common.integration.PolymorphIntegrations;

public class CPacketPlayerRecipeSelection {

  private final ResourceLocation recipe;

  public CPacketPlayerRecipeSelection(ResourceLocation pResourceLocation) {
    this.recipe = pResourceLocation;
  }

  public static void encode(CPacketPlayerRecipeSelection pPacket, PacketBuffer pBuffer) {
    pBuffer.writeResourceLocation(pPacket.recipe);
  }

  public static CPacketPlayerRecipeSelection decode(PacketBuffer pBuffer) {
    return new CPacketPlayerRecipeSelection(pBuffer.readResourceLocation());
  }

  public static void handle(CPacketPlayerRecipeSelection pPacket,
                            Supplier<NetworkEvent.Context> pContext) {
    pContext.get().enqueueWork(() -> {
      ServerPlayerEntity sender = pContext.get().getSender();

      if (sender != null) {
        Container container = sender.containerMenu;
        sender.level.getRecipeManager().byKey(pPacket.recipe).ifPresent(recipe -> {
          PolymorphApi.common().getRecipeData(sender)
              .ifPresent(recipeData -> recipeData.selectRecipe(recipe));

          for (AbstractCompatibilityModule integration : PolymorphIntegrations.get()) {

            if (integration.selectRecipe(container, recipe)) {
              return;
            }
          }
          container.slotsChanged(sender.inventory);

          if (container instanceof AbstractRepairContainer) {
            ((AbstractRepairContainer) container).createResult();
          }
        });
      }
    });
    pContext.get().setPacketHandled(true);
  }
}
