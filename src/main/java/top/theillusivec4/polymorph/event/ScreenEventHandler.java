package top.theillusivec4.polymorph.event;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.inventory.CraftingScreen;
import net.minecraft.inventory.container.Slot;
import net.minecraft.inventory.container.WorkbenchContainer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.network.PacketDistributor;
import top.theillusivec4.polymorph.gui.RecipeSelectionGui;
import top.theillusivec4.polymorph.network.NetworkHandler;
import top.theillusivec4.polymorph.network.client.CPacketTransferOutput;
import top.theillusivec4.polymorph.util.ClientCraftingManager;
import top.theillusivec4.polymorph.util.ClientHelper;

public class ScreenEventHandler {

  @SubscribeEvent
  public void guiInit(GuiScreenEvent.InitGuiEvent.Post evt) {
    Screen screen = evt.getGui();

    if (screen instanceof CraftingScreen) {
      ClientCraftingManager craftingManager = ClientCraftingManager
          .refreshInstance((CraftingScreen) screen);
      evt.addWidget(craftingManager.getSwitchButton());
    }
  }

  @SubscribeEvent
  public void guiRender(GuiScreenEvent.DrawScreenEvent.Post evt) {
    Screen screen = evt.getGui();

    if (screen instanceof CraftingScreen) {
      ClientCraftingManager.getInstance().ifPresent(
          craftingManager -> craftingManager.getRecipeSelectionGui()
              .render(evt.getMouseX(), evt.getMouseY(), evt.getRenderPartialTicks()));
    }
  }

  @SubscribeEvent
  public void guiMouseClick(GuiScreenEvent.MouseClickedEvent.Pre evt) {
    Screen screen = evt.getGui();

    if (screen instanceof CraftingScreen) {
      ClientCraftingManager.getInstance().ifPresent(craftingManager -> {
        RecipeSelectionGui recipeSelectionGui = craftingManager.getRecipeSelectionGui();

        if (recipeSelectionGui.mouseClicked(evt.getMouseX(), evt.getMouseY(), evt.getButton())) {
          recipeSelectionGui.setVisible(false);
          evt.setCanceled(true);
        } else if (recipeSelectionGui.isVisible()) {

          if (!craftingManager.getSwitchButton().mouseClicked(evt.getMouseX(), evt.getMouseY(), evt.getButton())) {
            recipeSelectionGui.setVisible(false);
          }
          evt.setCanceled(true);
        }
        CraftingScreen craftingScreen = (CraftingScreen) screen;
        WorkbenchContainer workbenchContainer = craftingScreen.getContainer();
        int outputSlot = workbenchContainer.getOutputSlot();
        Slot slot = workbenchContainer.getSlot(outputSlot);

        if (slot == craftingScreen.getSlotUnderMouse()) {
          boolean isShiftKeyDown = ClientHelper.isShiftKeyDown();

          if (isShiftKeyDown) {
            craftingManager.getLastSelectedRecipe().ifPresent(
                recipe -> craftingManager.getCurrentCraftingMatrix()
                    .ifPresent(craftingInventory -> {
                      ClientPlayerEntity player = Minecraft.getInstance().player;

                      for (ItemStack itemstack = workbenchContainer
                          .transferStackInSlot(player, outputSlot);
                          !itemstack.isEmpty() && ItemStack
                              .areItemsEqual(slot.getStack(), itemstack);
                          itemstack = workbenchContainer.transferStackInSlot(player, outputSlot)) {
                        slot.putStack(recipe.getCraftingResult(craftingInventory));
                      }
                      NetworkHandler.INSTANCE.send(PacketDistributor.SERVER.noArg(),
                          new CPacketTransferOutput(recipe.getId().toString()));
                      craftingManager.update();
                      evt.setCanceled(true);
                    }));

          }
        }
      });
    }
  }
}
