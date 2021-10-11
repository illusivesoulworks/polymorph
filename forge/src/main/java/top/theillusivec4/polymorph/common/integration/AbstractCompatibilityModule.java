package top.theillusivec4.polymorph.common.integration;

import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.container.Container;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.tileentity.TileEntity;

public abstract class AbstractCompatibilityModule {

  public void setup() {
    // NO-OP
  }

  public void clientSetup() {
    // NO-OP
  }

  public boolean setRecipe(Container container, IRecipe<?> recipe) {
    return false;
  }

  public boolean setRecipe(TileEntity tileEntity, IRecipe<?> recipe) {
    return false;
  }

  public boolean openContainer(Container container, ServerPlayerEntity serverPlayerEntity) {
    return false;
  }
}
