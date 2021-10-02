package top.theillusivec4.polymorph.common.integration.refinedstorage;

import com.refinedmods.refinedstorage.api.network.grid.IGrid;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.crafting.ICraftingRecipe;
import net.minecraftforge.fml.network.PacketDistributor;
import top.theillusivec4.polymorph.client.recipe.controller.CraftingRecipeController;
import top.theillusivec4.polymorph.common.integration.refinedstorage.network.CPacketSelectRefined;
import top.theillusivec4.polymorph.common.integration.refinedstorage.network.RefinedStoragePolymorphNetwork;
import top.theillusivec4.polymorph.common.network.PolymorphNetwork;
import top.theillusivec4.polymorph.common.network.client.CPacketGetRecipes;

public class RefinedStorageRecipeController extends CraftingRecipeController {

  public RefinedStorageRecipeController(ContainerScreen<?> containerScreen,
                                        CraftingInventory craftingInventory, Slot outputSlot) {
    super(containerScreen, craftingInventory, outputSlot);
    PolymorphNetwork.INSTANCE.send(PacketDistributor.SERVER.noArg(), new CPacketGetRecipes());
  }

  @Override
  public void selectRecipe(ICraftingRecipe recipe) {
    RefinedStoragePolymorphNetwork.INSTANCE.send(PacketDistributor.SERVER.noArg(),
        new CPacketSelectRefined(recipe.getId()));
  }
}
