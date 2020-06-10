package top.theillusivec4.polymorph;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.inventory.CraftingScreen;
import net.minecraft.client.gui.widget.button.ImageButton;
import net.minecraft.client.util.InputMappings;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.Slot;
import net.minecraft.inventory.container.WorkbenchContainer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.network.PacketDistributor;
import top.theillusivec4.polymorph.network.NetworkHandler;
import top.theillusivec4.polymorph.network.client.CPacketTransferOutput;

public class ScreenEventHandler {

  private static final ResourceLocation SWITCH = new ResourceLocation(Polymorph.MODID,
      "textures/gui/switch.png");

  public static ImageButton switchButton;
  public static RecipeSelectionGui recipeSelectionGui;

  @SubscribeEvent
  public void guiInit(GuiScreenEvent.InitGuiEvent.Post evt) {
    Screen screen = evt.getGui();

    if (screen instanceof CraftingScreen) {
      recipeSelectionGui = new RecipeSelectionGui((CraftingScreen) screen);
      int x = screen.width / 2;
      int y = screen.height / 2;
      ImageButton button = new ImageButton(x + 36, y - 72, 16, 16, 0, 0, 17, SWITCH,
          clickWidget -> recipeSelectionGui.setVisible(!recipeSelectionGui.isVisible()));
      evt.addWidget(button);
      button.visible = ClientCraftingManager.getResultsList().size() > 1;
      switchButton = button;
    }
  }

  @SubscribeEvent
  public void guiRender(GuiScreenEvent.DrawScreenEvent.Post evt) {
    Screen screen = evt.getGui();

    if (screen instanceof CraftingScreen) {

      if (recipeSelectionGui != null) {
        recipeSelectionGui.render(evt.getMouseX(), evt.getMouseY(), evt.getRenderPartialTicks());
      }
    }
  }

  @SubscribeEvent
  public void guiMouseClick(GuiScreenEvent.MouseClickedEvent.Pre evt) {
    Screen screen = evt.getGui();

    if (screen instanceof CraftingScreen) {

      if (recipeSelectionGui != null) {

        if (recipeSelectionGui.mouseClicked(evt.getMouseX(), evt.getMouseY(), evt.getButton())) {
          recipeSelectionGui.setVisible(false);
          evt.setCanceled(true);
        }
      }
      CraftingScreen craftingScreen = (CraftingScreen) screen;
      WorkbenchContainer workbenchContainer = craftingScreen.getContainer();
      int outputSlot = workbenchContainer.getOutputSlot();
      Slot slot = workbenchContainer.getSlot(outputSlot);

      if (slot == craftingScreen.getSlotUnderMouse()
          && ClientEventHandler.lastSelectedRecipe != null) {
        boolean isShiftKeyDown =
            InputMappings.isKeyDown(Minecraft.getInstance().getMainWindow().getHandle(), 340)
                || InputMappings
                .isKeyDown(Minecraft.getInstance().getMainWindow().getHandle(), 344);

        if (isShiftKeyDown) {
          ClientPlayerEntity player = Minecraft.getInstance().player;

          for (ItemStack itemstack7 = workbenchContainer.transferStackInSlot(player, outputSlot);
              !itemstack7.isEmpty() && ItemStack.areItemsEqual(slot.getStack(), itemstack7);
              itemstack7 = workbenchContainer.transferStackInSlot(player, outputSlot)) {
            slot.putStack(ClientEventHandler.lastSelectedRecipe
                .getCraftingResult(ClientEventHandler.craftingMatrix));
          }
          NetworkHandler.INSTANCE.send(PacketDistributor.SERVER.noArg(),
              new CPacketTransferOutput(ClientEventHandler.lastSelectedRecipe.getId().toString()));
          evt.setCanceled(true);
        }
      }
    }
  }

  public static boolean mergeItemStack(Container container, ItemStack stack, int startIndex,
      int endIndex, boolean reverseDirection) {
    boolean flag = false;
    int i = startIndex;

    if (reverseDirection) {
      i = endIndex - 1;
    }

    if (stack.isStackable()) {

      while (!stack.isEmpty()) {

        if (reverseDirection) {

          if (i < startIndex) {
            break;
          }
        } else if (i >= endIndex) {
          break;
        }
        Slot slot = container.inventorySlots.get(i);
        ItemStack itemstack = slot.getStack();

        if (!itemstack.isEmpty() && areItemsAndTagsEqual(stack, itemstack)) {
          int j = itemstack.getCount() + stack.getCount();
          int maxSize = Math.min(slot.getSlotStackLimit(), stack.getMaxStackSize());

          if (j <= maxSize) {
            stack.setCount(0);
            itemstack.setCount(j);
            slot.onSlotChanged();
            flag = true;
          } else if (itemstack.getCount() < maxSize) {
            stack.shrink(maxSize - itemstack.getCount());
            itemstack.setCount(maxSize);
            slot.onSlotChanged();
            flag = true;
          }
        }

        if (reverseDirection) {
          --i;
        } else {
          ++i;
        }
      }
    }

    if (!stack.isEmpty()) {

      if (reverseDirection) {
        i = endIndex - 1;
      } else {
        i = startIndex;
      }

      while (true) {

        if (reverseDirection) {

          if (i < startIndex) {
            break;
          }
        } else if (i >= endIndex) {
          break;
        }
        Slot slot1 = container.inventorySlots.get(i);
        ItemStack itemstack1 = slot1.getStack();

        if (itemstack1.isEmpty() && slot1.isItemValid(stack)) {

          if (stack.getCount() > slot1.getSlotStackLimit()) {
            slot1.putStack(stack.split(slot1.getSlotStackLimit()));
          } else {
            slot1.putStack(stack.split(stack.getCount()));
          }
          slot1.onSlotChanged();
          flag = true;
          break;
        }

        if (reverseDirection) {
          --i;
        } else {
          ++i;
        }
      }
    }
    return flag;
  }

  public static boolean areItemsAndTagsEqual(ItemStack stack1, ItemStack stack2) {
    return stack1.getItem() == stack2.getItem() && ItemStack.areItemStackTagsEqual(stack1, stack2);
  }
}
