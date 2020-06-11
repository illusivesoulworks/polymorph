package top.theillusivec4.polymorph.event;

import java.lang.reflect.Field;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.CraftResultInventory;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.inventory.container.CraftingResultSlot;
import net.minecraft.inventory.container.Slot;
import net.minecraft.inventory.container.WorkbenchContainer;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.event.TickEvent.ClientTickEvent;
import net.minecraftforge.event.TickEvent.Phase;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import top.theillusivec4.polymorph.Polymorph;
import top.theillusivec4.polymorph.util.ClientCraftingManager;
import top.theillusivec4.polymorph.util.ClientHelper;

public class ClientEventHandler {

  private static final Field CRAFT_MATRIX = ObfuscationReflectionHelper
      .findField(WorkbenchContainer.class, "field_75162_e");
  private static boolean update = false;

  @SubscribeEvent
  public void mouseClicked(GuiScreenEvent.MouseClickedEvent.Pre evt) {

    if (evt.getGui() instanceof ContainerScreen) {
      ContainerScreen<?> containerScreen = (ContainerScreen<?>) evt.getGui();

      if (containerScreen.getContainer() instanceof WorkbenchContainer) {
        Slot slot = containerScreen.getSlotUnderMouse();

        if (slot != null && slot.inventory instanceof CraftResultInventory) {
          updateCraftingMatrix((WorkbenchContainer) containerScreen.getContainer());
        }
      }
    }
  }

  @SubscribeEvent
  public void mouseReleased(GuiScreenEvent.MouseReleasedEvent.Pre evt) {

    if (evt.getGui() instanceof ContainerScreen) {
      ContainerScreen<?> containerScreen = (ContainerScreen<?>) evt.getGui();

      if (containerScreen.getContainer() instanceof WorkbenchContainer) {
        Slot slot = containerScreen.getSlotUnderMouse();
        boolean isShiftKeyDown = ClientHelper.isShiftKeyDown();

        if (slot != null && (slot.inventory instanceof CraftingInventory || (
            slot.inventory instanceof PlayerInventory && isShiftKeyDown))) {
          updateCraftingMatrix((WorkbenchContainer) containerScreen.getContainer());
        }
      }
    }
  }

  private void updateCraftingMatrix(WorkbenchContainer workbenchContainer) {
    CraftingInventory craftingMatrix = null;

    try {
      craftingMatrix = (CraftingInventory) CRAFT_MATRIX.get(workbenchContainer);
    } catch (IllegalAccessException e) {
      Polymorph.LOGGER.error("beep beep");
    }
    ClientWorld world = Minecraft.getInstance().world;

    if (world != null && craftingMatrix != null) {
      update = true;
      CraftingInventory finalCraftingMatrix = craftingMatrix;
      ClientCraftingManager.getInstance().ifPresent(
          craftingManager -> craftingManager.setCurrentCraftingMatrix(finalCraftingMatrix));
    }
  }

  @SubscribeEvent
  public void tick(ClientTickEvent evt) {

    if (evt.phase == Phase.END && update) {
      update = false;
      ClientWorld world = Minecraft.getInstance().world;

      if (world != null) {
        ClientCraftingManager.getInstance().ifPresent(ClientCraftingManager::update);
      }
    }
  }
}
