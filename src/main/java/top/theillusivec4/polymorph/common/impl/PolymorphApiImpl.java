package top.theillusivec4.polymorph.common.impl;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import javax.annotation.Nonnull;
import net.minecraft.inventory.CraftResultInventory;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.Slot;
import top.theillusivec4.polymorph.api.PolyProvider;
import top.theillusivec4.polymorph.api.PolymorphApi;

public class PolymorphApiImpl implements PolymorphApi {

  public static final PolymorphApi INSTANCE = new PolymorphApiImpl();

  private static final Map<Class<? extends Container>, Function<? extends Container, PolyProvider>>
      providerFunctions = new HashMap<>();

  @Override
  public <T extends Container> void addProvider(Class<T> clazz,
                                                Function<T, PolyProvider> providerFunction) {
    providerFunctions.put(clazz, providerFunction);
  }

  @Override
  @SuppressWarnings("unchecked")
  public Optional<PolyProvider> getProvider(Container container) {
    Function<Container, PolyProvider> providerFunction =
        (Function<Container, PolyProvider>) providerFunctions
            .get(container.getClass());

    if (providerFunction == null) {
      Slot resultSlot = null;
      CraftingInventory craftingInventory = null;

      for (Slot slot : container.inventorySlots) {

        if (resultSlot == null && slot.inventory instanceof CraftResultInventory) {
          resultSlot = slot;
        } else if (craftingInventory == null && slot.inventory instanceof CraftingInventory) {
          craftingInventory = (CraftingInventory) slot.inventory;
        }

        if (resultSlot != null && craftingInventory != null) {
          break;
        }
      }

      return Optional.ofNullable(resultSlot != null && craftingInventory != null ?
          new SimpleProvider(container, craftingInventory, resultSlot) : null);
    }
    return Optional.of(providerFunction.apply(container));
  }

  private static class SimpleProvider implements PolyProvider {

    final Container container;
    final CraftingInventory craftingInventory;
    final Slot resultSlot;

    public SimpleProvider(Container container, CraftingInventory craftingInventory,
                          Slot resultSlot) {
      this.container = container;
      this.craftingInventory = craftingInventory;
      this.resultSlot = resultSlot;
    }

    @Override
    public Container getContainer() {
      return this.container;
    }

    @Nonnull
    @Override
    public CraftingInventory getCraftingInventory() {
      return this.craftingInventory;
    }

    @Nonnull
    @Override
    public Slot getOutputSlot() {
      return this.resultSlot;
    }
  }
}
