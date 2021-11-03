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

package top.theillusivec4.polymorph.common.network.server;

import java.util.function.Supplier;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.network.NetworkEvent;
import top.theillusivec4.polymorph.client.recipe.RecipesWidget;

public class SPacketHighlightRecipe {

  private final ResourceLocation recipe;

  public SPacketHighlightRecipe(ResourceLocation pResourceLocation) {
    this.recipe = pResourceLocation;
  }

  public ResourceLocation getRecipe() {
    return this.recipe;
  }

  public static void encode(SPacketHighlightRecipe pPacket, PacketBuffer pBuffer) {
    pBuffer.writeResourceLocation(pPacket.recipe);
  }

  public static SPacketHighlightRecipe decode(PacketBuffer pBuffer) {
    return new SPacketHighlightRecipe(pBuffer.readResourceLocation());
  }

  public static void handle(SPacketHighlightRecipe pPacket,
                            Supplier<NetworkEvent.Context> pContext) {
    pContext.get().enqueueWork(() -> DistExecutor.unsafeRunWhenOn(Dist.CLIENT,
        () -> () -> ClientPacketHandler.handle(pPacket)));
    pContext.get().setPacketHandled(true);
  }
}
