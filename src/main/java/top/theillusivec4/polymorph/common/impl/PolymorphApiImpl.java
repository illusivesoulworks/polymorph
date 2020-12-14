package top.theillusivec4.polymorph.common.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import javax.annotation.Nonnull;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.inventory.CraftResultInventory;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.crafting.AbstractCookingRecipe;
import net.minecraft.item.crafting.ICraftingRecipe;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.tileentity.TileEntity;
import top.theillusivec4.polymorph.api.PolymorphApi;
import top.theillusivec4.polymorph.api.type.ICraftingProvider;
import top.theillusivec4.polymorph.api.type.IFurnaceProvider;
import top.theillusivec4.polymorph.api.type.IPersistentSelector;
import top.theillusivec4.polymorph.api.type.IPolyProvider;
import top.theillusivec4.polymorph.api.type.IRecipeSelector;
import top.theillusivec4.polymorph.client.selector.CraftingRecipeSelector;
import top.theillusivec4.polymorph.client.selector.FurnaceRecipeSelector;

public class PolymorphApiImpl implements PolymorphApi {

  public static final PolymorphApi INSTANCE = new PolymorphApiImpl();

  private static final List<Function<Container, IPolyProvider<? extends IInventory, ? extends IRecipe<?>>>>
      providerFunctions = new ArrayList<>();

  private static final List<Function<TileEntity, IPersistentSelector>> entityFunctions =
      new ArrayList<>();

  @Override
  public void addProvider(Function<Container, IPolyProvider<?, ?>> providerFunction) {
    providerFunctions.add(providerFunction);
  }

  @Override
  public void addEntityProvider(Function<TileEntity, IPersistentSelector> entityFunction,
                                Function<Container, IPolyProvider<?, ?>> providerFunction) {
    this.addProvider(providerFunction);
    entityFunctions.add(entityFunction);
  }

  @Override
  public Optional<IPolyProvider<?, ?>> getProvider(Container container) {

    for (Function<Container, IPolyProvider<? extends IInventory, ? extends IRecipe<?>>> function : providerFunctions) {
      IPolyProvider<?, ?> polyProvider = function.apply(container);

      if (polyProvider != null) {
        return Optional.of(polyProvider);
      }
    }
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
        new SimpleCraftingProvider(container, craftingInventory, resultSlot) : null);
  }

  @Override
  public Optional<IPersistentSelector> getSelector(TileEntity te) {

    for (Function<TileEntity, IPersistentSelector> entityFunction : entityFunctions) {
      IPersistentSelector selector = entityFunction.apply(te);

      if (selector != null) {
        return Optional.of(selector);
      }
    }
    return Optional.empty();
  }

  @Override
  public IRecipeSelector<CraftingInventory, ICraftingRecipe> createCraftingSelector(
      ContainerScreen<?> screen, ICraftingProvider provider) {
    return new CraftingRecipeSelector(screen, provider);
  }

  @Override
  public IRecipeSelector<IInventory, AbstractCookingRecipe> createFurnaceSelector(
      ContainerScreen<?> screen, IFurnaceProvider provider) {
    return new FurnaceRecipeSelector(screen, provider);
  }

  private static class SimpleCraftingProvider implements ICraftingProvider {

    final Container container;
    final CraftingInventory craftingInventory;
    final Slot resultSlot;

    public SimpleCraftingProvider(Container container, CraftingInventory craftingInventory,
                                  Slot resultSlot) {
      this.container = container;
      this.craftingInventory = craftingInventory;
      this.resultSlot = resultSlot;
    }

    @Nonnull
    @Override
    public Container getContainer() {
      return this.container;
    }

    @Nonnull
    @Override
    public CraftingInventory getInventory() {
      return this.craftingInventory;
    }

    @Nonnull
    @Override
    public Slot getOutputSlot() {
      return this.resultSlot;
    }
  }
}
