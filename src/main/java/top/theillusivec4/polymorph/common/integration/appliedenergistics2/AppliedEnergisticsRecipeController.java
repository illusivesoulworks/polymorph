package top.theillusivec4.polymorph.common.integration.appliedenergistics2;

import appeng.container.ContainerNull;
import appeng.container.slot.CraftingMatrixSlot;
import appeng.container.slot.CraftingTermSlot;
import java.util.ArrayList;
import java.util.List;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.recipe.CraftingRecipe;
import net.minecraft.screen.slot.Slot;
import net.minecraft.util.Identifier;
import top.theillusivec4.polymorph.client.recipe.AbstractRecipeController;
import top.theillusivec4.polymorph.common.integration.PolymorphCompatibilityPackets;

public class AppliedEnergisticsRecipeController
    extends AbstractRecipeController<CraftingInventory, CraftingRecipe> {

  private final List<CraftingMatrixSlot> slots = new ArrayList<>();
  private Slot outputSlot = null;

  public AppliedEnergisticsRecipeController(HandledScreen<?> handledScreen) {
    super(handledScreen);
    for (Slot slot : handledScreen.getScreenHandler().slots) {

      if (slot instanceof CraftingMatrixSlot) {
        slots.add((CraftingMatrixSlot) slot);
      } else if (outputSlot == null && slot instanceof CraftingTermSlot) {
        outputSlot = slot;
      }
    }
    this.init();
  }

  @Override
  public void selectRecipe(CraftingRecipe recipe) {
    PacketByteBuf buf = PacketByteBufs.create();
    Identifier id = recipe.getId();
    buf.writeIdentifier(id);
    ClientPlayNetworking.send(PolymorphCompatibilityPackets.SELECT_AE2_CRAFT, buf);
  }

  @Override
  public CraftingInventory getInventory() {
    ContainerNull cn = new ContainerNull();
    CraftingInventory ic = new CraftingInventory(cn, 3, 3);

    for (int x = 0; x < 9; x++) {
      ic.setStack(x, slots.get(x).getStack());
    }
    return ic;
  }

  @Override
  public Slot getOutputSlot() {
    return this.outputSlot;
  }
}
