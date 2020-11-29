package top.theillusivec4.polymorph.common.integrations.craftingcraft;

import javax.annotation.Nonnull;
import net.blay09.mods.craftingcraft.container.InventoryCraftingContainer;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.Slot;
import top.theillusivec4.polymorph.api.PolymorphApi;
import top.theillusivec4.polymorph.api.type.ICraftingProvider;
import top.theillusivec4.polymorph.common.integrations.CompatibilityModule;

public class CraftingCraftModule extends CompatibilityModule {

  @Override
  public void setup() {
    PolymorphApi.getInstance()
        .addProvider(InventoryCraftingContainer.class, InventoryCraftingProvider::new);
  }

  public static class InventoryCraftingProvider implements ICraftingProvider {

    InventoryCraftingContainer craftingContainer;

    public InventoryCraftingProvider(InventoryCraftingContainer craftingContainer) {
      this.craftingContainer = craftingContainer;
    }

    @Override
    public Container getContainer() {
      return this.craftingContainer;
    }

    @Nonnull
    @Override
    public CraftingInventory getInventory() {
      return craftingContainer.getCraftMatrix();
    }

    @Nonnull
    @Override
    public Slot getOutputSlot() {
      return this.craftingContainer.getSlot(0);
    }
  }
}
