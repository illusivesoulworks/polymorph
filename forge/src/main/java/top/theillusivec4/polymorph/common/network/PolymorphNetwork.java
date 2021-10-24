package top.theillusivec4.polymorph.common.network;

import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.network.NetworkEvent;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.simple.SimpleChannel;
import top.theillusivec4.polymorph.api.PolymorphApi;
import top.theillusivec4.polymorph.common.network.client.CPacketCraftingSelection;
import top.theillusivec4.polymorph.common.network.client.CPacketRecipeSelection;
import top.theillusivec4.polymorph.common.network.client.CPacketRecipesRequest;
import top.theillusivec4.polymorph.common.network.server.SPacketCraftingAction;
import top.theillusivec4.polymorph.common.network.server.SPacketHighlightRecipe;
import top.theillusivec4.polymorph.common.network.server.SPacketRecipes;

public class PolymorphNetwork {

  private static final String PTC_VERSION = "1";

  private static SimpleChannel instance;
  private static int id = 0;

  public static SimpleChannel get() {
    return instance;
  }

  public static void setup() {
    instance =
        NetworkRegistry.ChannelBuilder.named(new ResourceLocation(PolymorphApi.MOD_ID, "main"))
            .networkProtocolVersion(() -> PTC_VERSION).clientAcceptedVersions(PTC_VERSION::equals)
            .serverAcceptedVersions(PTC_VERSION::equals).simpleChannel();

    // Client-to-Server
    register(CPacketCraftingSelection.class, CPacketCraftingSelection::encode,
        CPacketCraftingSelection::decode, CPacketCraftingSelection::handle);
    register(CPacketRecipeSelection.class, CPacketRecipeSelection::encode,
        CPacketRecipeSelection::decode, CPacketRecipeSelection::handle);
    register(CPacketRecipesRequest.class, CPacketRecipesRequest::encode,
        CPacketRecipesRequest::decode, CPacketRecipesRequest::handle);

    // Server-to-Client
    register(SPacketCraftingAction.class, SPacketCraftingAction::encode,
        SPacketCraftingAction::decode, SPacketCraftingAction::handle);
    register(SPacketRecipes.class, SPacketRecipes::encode, SPacketRecipes::decode,
        SPacketRecipes::handle);
    register(SPacketHighlightRecipe.class, SPacketHighlightRecipe::encode,
        SPacketHighlightRecipe::decode, SPacketHighlightRecipe::handle);
  }

  private static <M> void register(Class<M> messageType, BiConsumer<M, PacketBuffer> encoder,
                                   Function<PacketBuffer, M> decoder,
                                   BiConsumer<M, Supplier<NetworkEvent.Context>> messageConsumer) {
    instance.registerMessage(id++, messageType, encoder, decoder, messageConsumer);
  }
}
