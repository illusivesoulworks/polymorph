package top.theillusivec4.polymorph.core;

import net.minecraft.inventory.CraftingInventory;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import top.theillusivec4.polymorph.Polymorph;
import top.theillusivec4.polymorph.util.ClientCraftingManager;

public class PolymorphHooks {

  public static void onCraftMatrixChanged(CraftingInventory craftingInventory) {
    DistExecutor.runWhenOn(Dist.CLIENT, () -> () -> ClientCraftingManager.getInstance()
        .ifPresent(ClientCraftingManager::flagUpdate));
  }
}
