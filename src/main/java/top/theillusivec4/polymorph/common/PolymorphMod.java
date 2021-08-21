package top.theillusivec4.polymorph.common;

import net.fabricmc.api.ModInitializer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import top.theillusivec4.polymorph.common.network.PolymorphNetwork;

public class PolymorphMod implements ModInitializer {

  public static final String MOD_ID = "polymorph";
  public static final Logger LOGGER = LogManager.getLogger();

  @Override
  public void onInitialize() {
    PolymorphNetwork.setup();
  }
}
