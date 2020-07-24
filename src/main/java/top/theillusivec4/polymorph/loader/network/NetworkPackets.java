package top.theillusivec4.polymorph.loader.network;

import net.minecraft.util.Identifier;
import top.theillusivec4.polymorph.core.Polymorph;

public class NetworkPackets {

  public static final Identifier SET_RECIPE = new Identifier(Polymorph.MODID, "set_recipe");
  public static final Identifier TRANSFER_RECIPE = new Identifier(Polymorph.MODID, "transfer_recipe");
  public static final Identifier SYNC_OUTPUT = new Identifier(Polymorph.MODID, "sync_output");
}
