package top.theillusivec4.polymorph.api;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import top.theillusivec4.polymorph.api.type.IPersistentSelector;

public class PolymorphCapability {

  @CapabilityInject(IPersistentSelector.class)
  public static final Capability<IPersistentSelector> PERSISTENT_SELECTOR;

  public static final ResourceLocation PERSISTENT_SELECTOR_ID =
      new ResourceLocation("polymorph:persistent_selector");

  static {
    PERSISTENT_SELECTOR = null;
  }
}
