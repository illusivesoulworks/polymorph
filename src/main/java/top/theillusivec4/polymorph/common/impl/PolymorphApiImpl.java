package top.theillusivec4.polymorph.common.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import javax.annotation.Nonnull;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.inventory.CraftResultInventory;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.container.AbstractFurnaceContainer;
import net.minecraft.inventory.container.BlastFurnaceContainer;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.FurnaceContainer;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.crafting.AbstractCookingRecipe;
import net.minecraft.item.crafting.ICraftingRecipe;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.item.crafting.RecipeManager;
import net.minecraft.world.World;
import top.theillusivec4.polymorph.api.PolymorphApi;
import top.theillusivec4.polymorph.api.type.ICraftingProvider;
import top.theillusivec4.polymorph.api.type.IFurnaceProvider;
import top.theillusivec4.polymorph.api.type.IPolyProvider;
import top.theillusivec4.polymorph.api.type.IRecipeSelector;
import top.theillusivec4.polymorph.client.selector.CraftingRecipeSelector;
import top.theillusivec4.polymorph.client.selector.FurnaceRecipeSelector;

public class PolymorphApiImpl implements PolymorphApi {

  public static final PolymorphApi INSTANCE = new PolymorphApiImpl();

  private static final Map<Class<? extends Container>, Function<? extends Container, IPolyProvider<? extends IInventory, ? extends IRecipe<?>>>>
      providerFunctions = new HashMap<>();

  @Override
  public <T extends Container> void addProvider(Class<T> clazz,
                                                Function<T, IPolyProvider<?, ?>> providerFunction) {
    providerFunctions.put(clazz, providerFunction);
  }

  @Override
  @SuppressWarnings("unchecked")
  public Optional<IPolyProvider<?, ?>> getProvider(
      Container container) {
    Function<Container, IPolyProvider<?, ?>> providerFunction =
        (Function<Container, IPolyProvider<?, ?>>) providerFunctions.get(container.getClass());

    if (providerFunction == null) {

      if (container instanceof AbstractFurnaceContainer) {
        return Optional.of(new SimpleFurnaceProvider(container));
      } else {
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
    }
    return Optional.of(providerFunction.apply(container));
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

  private static class SimpleFurnaceProvider implements IFurnaceProvider {

    final Container container;
    final IInventory input;
    final IRecipeType<? extends AbstractCookingRecipe> recipeType;

    public SimpleFurnaceProvider(Container container) {
      this.container = container;
      this.input = container.inventorySlots.get(0).inventory;
      this.recipeType = this.getRecipeType();
    }

    private IRecipeType<? extends AbstractCookingRecipe> getRecipeType() {

      if (this.container instanceof FurnaceContainer) {
        return IRecipeType.SMELTING;
      } else if (this.container instanceof BlastFurnaceContainer) {
        return IRecipeType.BLASTING;
      } else {
        return IRecipeType.SMOKING;
      }
    }

    @Nonnull
    @Override
    public Container getContainer() {
      return this.container;
    }

    @Nonnull
    @Override
    public IInventory getInventory() {
      return this.input;
    }

    @Nonnull
    @Override
    public List<? extends AbstractCookingRecipe> getRecipes(World world,
                                                            RecipeManager recipeManager) {
      return recipeManager.getRecipes(this.recipeType, this.getInventory(), world);
    }

    @Nonnull
    @Override
    public Slot getOutputSlot() {
      return this.container.inventorySlots.get(2);
    }
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
