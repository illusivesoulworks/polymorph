package com.illusivesoulworks.polymorph.common.network;

import com.illusivesoulworks.polymorph.common.network.server.SPacketHighlightRecipe;
import com.illusivesoulworks.polymorph.common.network.server.SPacketPlayerRecipeSync;
import com.illusivesoulworks.polymorph.common.network.server.SPacketRecipesList;
import com.illusivesoulworks.polymorph.common.network.server.SPacketUpdatePreview;
import net.minecraft.network.chat.Component;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public class ClientPayloadHandler {

  private static final ClientPayloadHandler INSTANCE = new ClientPayloadHandler();

  public static ClientPayloadHandler getInstance() {
    return INSTANCE;
  }

  private static void handleData(final IPayloadContext context, Runnable handler) {
    context.enqueueWork(handler)
        .exceptionally(e -> {
          context.disconnect(Component.translatable("polymorph.networking.failed", e.getMessage()));
          return null;
        });
  }

  public void handlePacket(final SPacketUpdatePreview packet, final IPayloadContext ctx) {
    handleData(ctx, () -> SPacketUpdatePreview.handle(packet));
  }

  public void handlePacket(final SPacketHighlightRecipe packet, final IPayloadContext ctx) {
    handleData(ctx, () -> SPacketHighlightRecipe.handle(packet));
  }

  public void handlePacket(final SPacketPlayerRecipeSync packet, final IPayloadContext ctx) {
    handleData(ctx, () -> SPacketPlayerRecipeSync.handle(packet));
  }

  public void handlePacket(final SPacketRecipesList packet, final IPayloadContext ctx) {
    handleData(ctx, () -> SPacketRecipesList.handle(packet));
  }
}
