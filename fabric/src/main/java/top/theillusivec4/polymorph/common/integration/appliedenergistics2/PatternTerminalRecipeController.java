package top.theillusivec4.polymorph.common.integration.appliedenergistics2;

import appeng.container.ContainerNull;
import appeng.container.me.items.PatternTermContainer;
import appeng.container.slot.FakeCraftingMatrixSlot;
import appeng.container.slot.PatternTermSlot;
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

public class PatternTerminalRecipeController
    extends AbstractRecipeController<CraftingInventory, CraftingRecipe> {

  private final List<FakeCraftingMatrixSlot> slots = new ArrayList<>();
  private Slot outputSlot = null;
  private PatternTermContainer patternTermContainer;

  public PatternTerminalRecipeController(HandledScreen<?> handledScreen) {
    super(handledScreen);
    this.patternTermContainer = (PatternTermContainer) handledScreen.getScreenHandler();
    for (Slot slot : this.patternTermContainer.slots) {

      if (slot instanceof FakeCraftingMatrixSlot) {
        this.slots.add((FakeCraftingMatrixSlot) slot);
      } else if (this.outputSlot == null && slot instanceof PatternTermSlot) {
        this.outputSlot = slot;
      }
    }
    this.init();
  }

  @Override
  public void selectRecipe(CraftingRecipe recipe) {
    PacketByteBuf buf = PacketByteBufs.create();
    Identifier id = recipe.getId();
    buf.writeIdentifier(id);
    ClientPlayNetworking.send(PolymorphCompatibilityPackets.SELECT_AE2_PATTERN, buf);
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

  @Override
  public boolean isActive() {
    return this.patternTermContainer.craftingMode;
  }
}
