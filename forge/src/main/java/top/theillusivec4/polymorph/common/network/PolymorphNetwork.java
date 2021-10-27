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
import top.theillusivec4.polymorph.common.network.client.CPacketPersistentRecipeSelection;
import top.theillusivec4.polymorph.common.network.client.CPacketPlayerRecipeSelection;
import top.theillusivec4.polymorph.common.network.client.CPacketRecipesRequest;
import top.theillusivec4.polymorph.common.network.server.SPacketHighlightRecipe;
import top.theillusivec4.polymorph.common.network.server.SPacketRecipesList;

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
    register(CPacketPlayerRecipeSelection.class, CPacketPlayerRecipeSelection::encode,
        CPacketPlayerRecipeSelection::decode, CPacketPlayerRecipeSelection::handle);
    register(CPacketPersistentRecipeSelection.class, CPacketPersistentRecipeSelection::encode,
        CPacketPersistentRecipeSelection::decode, CPacketPersistentRecipeSelection::handle);
    register(CPacketRecipesRequest.class, CPacketRecipesRequest::encode,
        CPacketRecipesRequest::decode, CPacketRecipesRequest::handle);

    // Server-to-Client
    register(SPacketRecipesList.class, SPacketRecipesList::encode, SPacketRecipesList::decode,
        SPacketRecipesList::handle);
    register(SPacketHighlightRecipe.class, SPacketHighlightRecipe::encode,
        SPacketHighlightRecipe::decode, SPacketHighlightRecipe::handle);
  }

  private static <M> void register(Class<M> pClass, BiConsumer<M, PacketBuffer> pEncoder,
                                   Function<PacketBuffer, M> pDecoder,
                                   BiConsumer<M, Supplier<NetworkEvent.Context>> pMessage) {
    instance.registerMessage(id++, pClass, pEncoder, pDecoder, pMessage);
  }
}
