package top.theillusivec4.polymorph.common.integration;

import com.google.common.collect.ImmutableSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;
import net.minecraftforge.fml.ModList;
import top.theillusivec4.polymorph.common.integration.appliedenergistics2.AppliedEnergisticsModule;
import top.theillusivec4.polymorph.common.integration.craftingstation.CraftingStationModule;
import top.theillusivec4.polymorph.common.integration.cyclic.CyclicModule;
import top.theillusivec4.polymorph.common.integration.fastbench.FastBenchModule;
import top.theillusivec4.polymorph.common.integration.fastfurnace.FastFurnaceModule;
import top.theillusivec4.polymorph.common.integration.ironfurnaces.IronFurnacesModule;
import top.theillusivec4.polymorph.common.integration.prettypipes.PrettyPipesModule;
import top.theillusivec4.polymorph.common.integration.refinedstorage.RefinedStorageModule;
import top.theillusivec4.polymorph.common.integration.refinedstorageaddons.RefinedStorageAddonsModule;
import top.theillusivec4.polymorph.common.integration.sophisticatedbackpacks.SophisticatedBackpacksModule;
import top.theillusivec4.polymorph.common.integration.tconstruct.TinkersConstructModule;
import top.theillusivec4.polymorph.common.integration.toms_storage.TomsStorageModule;

public class PolymorphIntegrations {

  private static final Map<String, Supplier<AbstractCompatibilityModule>> INTEGRATIONS =
      new HashMap<>();
  private static final Set<AbstractCompatibilityModule> ACTIVE_INTEGRATIONS = new HashSet<>();

  static {
    INTEGRATIONS.put(Ids.PRETTY_PIPES, PrettyPipesModule::new);
    INTEGRATIONS.put(Ids.REFINED_STORAGE, RefinedStorageModule::new);
    INTEGRATIONS.put(Ids.REFINED_STORAGE_ADDONS, RefinedStorageAddonsModule::new);
    INTEGRATIONS.put(Ids.TINKERS_CONSTRUCT, TinkersConstructModule::new);
    INTEGRATIONS.put(Ids.FASTWORKBENCH, FastBenchModule::new);
    INTEGRATIONS.put(Ids.CRAFTING_STATION, CraftingStationModule::new);
    INTEGRATIONS.put(Ids.FASTFURNACE, FastFurnaceModule::new);
    INTEGRATIONS.put(Ids.CYCLIC, CyclicModule::new);
    INTEGRATIONS.put(Ids.TOMS_STORAGE, TomsStorageModule::new);
    INTEGRATIONS.put(Ids.SOPHISTICATED_BACKPACKS, SophisticatedBackpacksModule::new);
    INTEGRATIONS.put(Ids.APPLIED_ENERGISTICS_2, AppliedEnergisticsModule::new);
    INTEGRATIONS.put(Ids.IRON_FURNACES, IronFurnacesModule::new);
  }

  public static void init() {
    ModList modList = ModList.get();
    INTEGRATIONS.forEach((modid, supplier) -> {

      if (modList.isLoaded(modid)) {
        ACTIVE_INTEGRATIONS.add(supplier.get());
      }
    });
  }

  public static void setup() {

    for (AbstractCompatibilityModule integration : get()) {
      integration.setup();
    }
  }

  public static void clientSetup() {

    for (AbstractCompatibilityModule integration : get()) {
      integration.clientSetup();
    }
  }

  public static Set<AbstractCompatibilityModule> get() {
    return ImmutableSet.copyOf(ACTIVE_INTEGRATIONS);
  }

  public static class Ids {

    public static final String JEI = "jei";
    public static final String CRAFTINGCRAFT = "craftingcraft";
    public static final String CRAFTING_STATION = "craftingstation";
    public static final String PRETTY_PIPES = "prettypipes";
    public static final String TOMS_STORAGE = "toms_storage";
    public static final String FASTWORKBENCH = "fastbench";
    public static final String SIMPLE_STORAGE_NETWORK = "storagenetwork";
    public static final String REFINED_STORAGE = "refinedstorage";
    public static final String REFINED_STORAGE_ADDONS = "refinedstorageaddons";
    public static final String TINKERS_CONSTRUCT = "tconstruct";
    public static final String CYCLIC = "cyclic";
    public static final String SOPHISTICATED_BACKPACKS = "sophisticatedbackpacks";
    public static final String APPLIED_ENERGISTICS_2 = "appliedenergistics2";
    public static final String IRON_FURNACES = "ironfurnaces";
    public static final String FASTFURNACE = "fastfurnace";
  }
}
