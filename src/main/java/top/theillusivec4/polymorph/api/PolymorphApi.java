package top.theillusivec4.polymorph.api;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.Slot;

public class PolymorphApi {

  private static final Map<Class<? extends Container>, IProvider<? extends Container>> providers = new HashMap<>();

  public static void addProvider(Class<? extends Container> clazz,
      IProvider<? extends Container> provider) {
    providers.put(clazz, provider);
  }

  @SuppressWarnings("unchecked")
  public static <T extends Container> Optional<IProvider<T>> getProvider(T container) {
    IProvider<?> provider = providers.get(container.getClass());
    return provider != null ? Optional.of((IProvider<T>) provider) : Optional.empty();
  }

  public interface IProvider<T extends Container> {

    CraftingInventory getCraftingMatrix(T container);

    Slot getOutputSlot(T container);

    int getXOffset();

    int getYOffset();
  }
}
