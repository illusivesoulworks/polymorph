package top.theillusivec4.polymorph.mixin.integration.fastbench;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.CraftResultInventory;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import shadows.fastbench.gui.ContainerFastBench;
import shadows.fastbench.gui.CraftingInventoryExt;
import top.theillusivec4.polymorph.common.crafting.RecipeSelection;

@Mixin(ContainerFastBench.class)
public class MixinContainerFastBench {

  @Redirect(
      at = @At(
          value = "INVOKE",
          target = "shadows/fastbench/gui/ContainerFastBench.findRecipe(Lnet/minecraft/inventory/CraftingInventory;Lnet/minecraft/world/World;)Lnet/minecraft/item/crafting/IRecipe;"),
      method = "slotChangedCraftingGrid",
      remap = false)
  private static IRecipe<CraftingInventory> polymorph$findRecipe(CraftingInventory inv, World world,
                                                                 World unused1, PlayerEntity player,
                                                                 CraftingInventoryExt unused2,
                                                                 CraftResultInventory result) {
    return RecipeSelection.getRecipe(IRecipeType.CRAFTING, inv, world, player).orElse(null);
  }
}
