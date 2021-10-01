package top.theillusivec4.polymorph.common.integration.refinedstorage;

import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.crafting.ICraftingRecipe;
import net.minecraftforge.fml.network.PacketDistributor;
import top.theillusivec4.polymorph.client.recipe.controller.CraftingRecipeController;
import top.theillusivec4.polymorph.common.integration.refinedstorage.network.CPacketSelectRefined;
import top.theillusivec4.polymorph.common.integration.refinedstorage.network.RefinedStoragePolymorphNetwork;

public class RefinedStorageRecipeController extends CraftingRecipeController {

  public RefinedStorageRecipeController(ContainerScreen<?> containerScreen,
                                        CraftingInventory craftingInventory, Slot outputSlot) {
    super(containerScreen, craftingInventory, outputSlot);
  }

  @Override
  public void selectRecipe(ICraftingRecipe recipe) {
    RefinedStoragePolymorphNetwork.INSTANCE.send(PacketDistributor.SERVER.noArg(),
        new CPacketSelectRefined(recipe.getId()));
  }
}
