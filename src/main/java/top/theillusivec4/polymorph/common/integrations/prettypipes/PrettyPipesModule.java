package top.theillusivec4.polymorph.common.integrations.prettypipes;

import de.ellpeck.prettypipes.terminal.containers.CraftingTerminalContainer;
import javax.annotation.Nonnull;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.Slot;
import top.theillusivec4.polymorph.api.PolymorphApi;
import top.theillusivec4.polymorph.api.type.ICraftingProvider;
import top.theillusivec4.polymorph.common.integrations.CompatibilityModule;

public class PrettyPipesModule extends CompatibilityModule {

  @Override
  public void setup() {
    PolymorphApi.getInstance()
        .addProvider(CraftingTerminalContainer.class, CraftingTerminalProvider::new);
  }

  public static class CraftingTerminalProvider implements ICraftingProvider {

    CraftingTerminalContainer craftingContainer;

    public CraftingTerminalProvider(CraftingTerminalContainer craftingContainer) {
      this.craftingContainer = craftingContainer;
    }

    @Nonnull
    @Override
    public Container getContainer() {
      return this.craftingContainer;
    }

    @Nonnull
    @Override
    public CraftingInventory getInventory() {
      return craftingContainer.craftInventory;
    }

    @Override
    public int getXPos() {
      return this.getOutputSlot().xPos + 22;
    }

    @Override
    public int getYPos() {
      return this.getOutputSlot().yPos;
    }

    @Nonnull
    @Override
    public Slot getOutputSlot() {
      return this.craftingContainer.getSlot(0);
    }
  }
}
