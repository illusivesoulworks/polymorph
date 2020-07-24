package top.theillusivec4.polymorph.loader.util;

import java.util.function.Supplier;
import net.fabricmc.api.EnvType;
import net.fabricmc.loader.api.FabricLoader;

public class EnvironmentExecutor {

  public static void runOnClient(Supplier<Runnable> runnableSupplier) {

    if (FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT) {
      runnableSupplier.get().run();
    }
  }
}
