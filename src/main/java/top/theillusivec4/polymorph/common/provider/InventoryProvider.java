package top.theillusivec4.polymorph.common.provider;

import java.lang.reflect.Field;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.inventory.container.PlayerContainer;
import net.minecraft.inventory.container.Slot;
import net.minecraft.inventory.container.WorkbenchContainer;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import top.theillusivec4.polymorph.Polymorph;
import top.theillusivec4.polymorph.api.PolymorphApi.IProvider;

public class InventoryProvider implements IProvider<PlayerContainer> {

  private static final Field CRAFT_MATRIX = ObfuscationReflectionHelper
      .findField(PlayerContainer.class, "field_75181_e");

  @Override
  public CraftingInventory getCraftingMatrix(PlayerContainer container) {
    CraftingInventory craftingInventory = null;

    try {
      craftingInventory = (CraftingInventory) CRAFT_MATRIX.get(container);
    } catch (IllegalAccessException e) {
      Polymorph.LOGGER
          .error("Whoops, something went wrong while trying to retrieve the crafting matrix!");
    }
    return craftingInventory;
  }

  @Override
  public Slot getOutputSlot(PlayerContainer container) {
    return container.getSlot(container.getOutputSlot());
  }

  @Override
  public int getXOffset() {
    return 66;
  }

  @Override
  public int getYOffset() {
    return -74;
  }
}
