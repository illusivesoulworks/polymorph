package top.theillusivec4.polymorph.common.integration;

import com.google.common.collect.ImmutableSet;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.function.Supplier;
import net.fabricmc.loader.api.FabricLoader;
import org.apache.commons.io.FileUtils;
import top.theillusivec4.polymorph.api.PolymorphApi;
import top.theillusivec4.polymorph.common.PolymorphMod;
import top.theillusivec4.polymorph.common.integration.appliedenergistics2.AppliedEnergisticsModule;
import top.theillusivec4.polymorph.common.integration.fastfurnace.FastFurnaceModule;
import top.theillusivec4.polymorph.common.integration.ironfurnaces.IronFurnacesModule;
import top.theillusivec4.polymorph.common.integration.recipecache.RecipeCacheModule;
import top.theillusivec4.polymorph.common.integration.toms_storage.TomsStorageModule;

public class PolymorphIntegrations {

  private static final Set<String> CONFIG_ACTIVATED = new HashSet<>();
  private static final Map<String, Supplier<Supplier<AbstractCompatibilityModule>>> INTEGRATIONS =
      new HashMap<>();
  private static final Set<AbstractCompatibilityModule> ACTIVE_INTEGRATIONS = new HashSet<>();

  static {
    INTEGRATIONS.put(Mod.FASTFURNACE.getId(), () -> FastFurnaceModule::new);
    INTEGRATIONS.put(Mod.APPLIED_ENERGISTICS_2.getId(), () -> AppliedEnergisticsModule::new);
    INTEGRATIONS.put(Mod.IRON_FURNACES.getId(), () -> IronFurnacesModule::new);
    INTEGRATIONS.put(Mod.TOMS_STORAGE.getId(), () -> TomsStorageModule::new);
    INTEGRATIONS.put(Mod.RECIPECACHE.getId(), () -> RecipeCacheModule::new);
  }

  public static void loadConfig() {
    Map<String, IntegrationConfig> defaultModConfigs = new TreeMap<>();

    for (Mod mod : Mod.values()) {
      defaultModConfigs.put(mod.getId(), new IntegrationConfig(mod.getId(), true));
    }
    IntegrationConfig[] defaults = defaultModConfigs.values().toArray(new IntegrationConfig[0]);
    IntegrationConfig[] configs = fromJson(TypeToken.get(IntegrationConfig[].class),
        new File(FabricLoader.getInstance().getConfigDir().toString(),
            PolymorphApi.MOD_ID + "-integrations.json"), defaults);
    Map<String, IntegrationConfig> currentModConfigs = new TreeMap<>(defaultModConfigs);

    for (IntegrationConfig config : configs) {
      currentModConfigs.replace(config.id, config);
    }

    for (IntegrationConfig config : currentModConfigs.values()) {

      if (config.enabled) {
        CONFIG_ACTIVATED.add(config.id);
      }
    }
    toJson(TypeToken.get(IntegrationConfig[].class),
        new File(FabricLoader.getInstance().getConfigDir().toString(),
            PolymorphApi.MOD_ID + "-integrations.json"),
        currentModConfigs.values().toArray(new IntegrationConfig[0]));
  }

  public static void init() {
    FabricLoader fabricLoader = FabricLoader.getInstance();
    INTEGRATIONS.forEach((modid, supplier) -> {

      if (CONFIG_ACTIVATED.contains(modid) && fabricLoader.isModLoaded(modid)) {
        ACTIVE_INTEGRATIONS.add(supplier.get().get());
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

  public static Set<String> getConfigActivated() {
    return ImmutableSet.copyOf(CONFIG_ACTIVATED);
  }

  public enum Mod {
    REI("roughlyenoughitems-runtime"),
    TOMS_STORAGE("toms_storage"),
    APPLIED_ENERGISTICS_2("ae2"),
    IRON_FURNACES("ironfurnaces"),
    FASTFURNACE("fastfurnace"),
    RECIPECACHE("recipecache");

    private final String id;

    Mod(String pId) {
      this.id = pId;
    }

    public String getId() {
      return this.id;
    }
  }

  private static final Gson GSON =
      new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();

  private static <T> T[] fromJson(TypeToken<T[]> token, File file, T[] defaults) {

    if (!file.exists()) {
      toJson(token, file, defaults);
      return defaults;
    } else {

      try (FileReader reader = new FileReader(file)) {
        return GSON.fromJson(reader, token.getType());
      } catch (IOException e) {
        PolymorphMod.LOGGER.error("Error reading config file!");
        return defaults;
      }
    }
  }

  private static <T> void toJson(TypeToken<T[]> token, File file, T[] defaults) {

    if (!file.exists()) {
      try {
        FileUtils.forceMkdirParent(file);
        file.createNewFile();
      } catch (IOException e) {
        PolymorphMod.LOGGER.error("Error creating config file!");
        return;
      }
    }

    try (FileWriter writer = new FileWriter(file)) {
      writer.write(getJson(defaults, token));
    } catch (IOException e) {
      PolymorphMod.LOGGER.error("Error creating config file!");
    }
  }

  private static <T> String getJson(T[] elements, TypeToken<T[]> token) {
    return GSON.toJson(elements, token.getType());
  }

  private static class IntegrationConfig {

    private final String id;
    private final boolean enabled;

    private IntegrationConfig(String id, boolean enabled) {
      this.id = id;
      this.enabled = enabled;
    }
  }
}
