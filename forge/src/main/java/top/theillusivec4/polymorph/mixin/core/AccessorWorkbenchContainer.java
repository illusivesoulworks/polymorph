package top.theillusivec4.polymorph.mixin.core;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.CraftResultInventory;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.inventory.container.WorkbenchContainer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(WorkbenchContainer.class)
public interface AccessorWorkbenchContainer {

  @Accessor
  CraftingInventory getCraftMatrix();

  @Accessor
  CraftResultInventory getCraftResult();

  @Accessor
  PlayerEntity getPlayer();
}
