package top.theillusivec4.polymorph.common;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.screen.SmithingScreenHandler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import top.theillusivec4.polymorph.api.PolymorphApi;
import top.theillusivec4.polymorph.client.recipe.SmithingRecipeController;
import top.theillusivec4.polymorph.common.integration.AbstractCompatibilityModule;
import top.theillusivec4.polymorph.common.integration.appliedenergistics2.AppliedEnergisticsModule;
import top.theillusivec4.polymorph.common.integration.fabricfurnaces.FabricFurnacesModule;
import top.theillusivec4.polymorph.common.integration.ironfurnaces.IronFurnacesModule;
import top.theillusivec4.polymorph.common.network.PolymorphNetwork;
import top.theillusivec4.polymorph.server.PolymorphCommands;

public class PolymorphMod implements ModInitializer {

  public static final Map<String, Supplier<AbstractCompatibilityModule>> INTEGRATIONS =
      new HashMap<>();

  public static final String MOD_ID = "polymorph";
  public static final Logger LOGGER = LogManager.getLogger();

  public static boolean isFastFurnaceLoaded = false;

  static {
    INTEGRATIONS.put("ironfurnaces", IronFurnacesModule::new);
    INTEGRATIONS.put("fabric-furnaces", FabricFurnacesModule::new);
    INTEGRATIONS.put("appliedenergistics2", AppliedEnergisticsModule::new);
  }

  @Override
  public void onInitialize() {
    PolymorphNetwork.setup();
    PolymorphCommands.setup();
    PolymorphApi.getInstance().addRecipeController(handledScreen -> {
      if (handledScreen.getScreenHandler() instanceof SmithingScreenHandler) {
        return new SmithingRecipeController(handledScreen);
      }
      return null;
    });

    FabricLoader loader = FabricLoader.getInstance();
    isFastFurnaceLoaded = loader.isModLoaded("fastfurnace");
    INTEGRATIONS.forEach((modid, supplier) -> {

      if (loader.isModLoaded(modid)) {
        supplier.get().setup();
      }
    });
  }
}
