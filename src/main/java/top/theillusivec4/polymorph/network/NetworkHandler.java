package top.theillusivec4.polymorph.network;

import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.network.NetworkEvent.Context;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.simple.SimpleChannel;
import top.theillusivec4.polymorph.Polymorph;
import top.theillusivec4.polymorph.network.client.CPacketFetchRecipeOutputs;
import top.theillusivec4.polymorph.network.client.CPacketSetOutput;
import top.theillusivec4.polymorph.network.server.SPacketSendRecipeOutputs;

public class NetworkHandler {

  private static final String PTC_VERSION = "1";

  public static SimpleChannel INSTANCE;

  private static int id = 0;

  public static void register() {
    INSTANCE = NetworkRegistry.ChannelBuilder.named(new ResourceLocation(Polymorph.MODID, "main"))
        .networkProtocolVersion(() -> PTC_VERSION).clientAcceptedVersions(PTC_VERSION::equals)
        .serverAcceptedVersions(PTC_VERSION::equals).simpleChannel();

    register(CPacketFetchRecipeOutputs.class, CPacketFetchRecipeOutputs::encode,
        CPacketFetchRecipeOutputs::decode, CPacketFetchRecipeOutputs::handle);
    register(CPacketSetOutput.class, CPacketSetOutput::encode, CPacketSetOutput::decode,
        CPacketSetOutput::handle);
    register(SPacketSendRecipeOutputs.class, SPacketSendRecipeOutputs::encode,
        SPacketSendRecipeOutputs::decode, SPacketSendRecipeOutputs::handle);
  }

  private static <M> void register(Class<M> messageType, BiConsumer<M, PacketBuffer> encoder,
      Function<PacketBuffer, M> decoder, BiConsumer<M, Supplier<Context>> messageConsumer) {
    INSTANCE.registerMessage(id++, messageType, encoder, decoder, messageConsumer);
  }
}
