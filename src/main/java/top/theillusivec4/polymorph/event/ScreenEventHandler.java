package top.theillusivec4.polymorph.event;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.inventory.CraftingScreen;
import net.minecraft.client.gui.widget.button.ImageButton;
import net.minecraft.inventory.container.Slot;
import net.minecraft.inventory.container.WorkbenchContainer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.network.PacketDistributor;
import top.theillusivec4.polymorph.util.ClientCraftingManager;
import top.theillusivec4.polymorph.util.ClientHelper;
import top.theillusivec4.polymorph.Polymorph;
import top.theillusivec4.polymorph.gui.RecipeSelectionGui;
import top.theillusivec4.polymorph.network.NetworkHandler;
import top.theillusivec4.polymorph.network.client.CPacketTransferOutput;

public class ScreenEventHandler {

  private static final ResourceLocation SWITCH = new ResourceLocation(Polymorph.MODID,
      "textures/gui/switch.png");

  @SubscribeEvent
  public void guiInit(GuiScreenEvent.InitGuiEvent.Post evt) {
    Screen screen = evt.getGui();

    if (screen instanceof CraftingScreen) {
      RecipeSelectionGui recipeSelectionGui = ClientCraftingManager.createRecipeSelectionGui();
      int x = screen.width / 2;
      int y = screen.height / 2;
      ImageButton button = new ImageButton(x + 36, y - 72, 16, 16, 0, 0, 17, SWITCH,
          clickWidget -> recipeSelectionGui.setVisible(!recipeSelectionGui.isVisible()));
      evt.addWidget(button);
      button.visible = ClientCraftingManager.getRecipesList().size() > 1;
      ClientCraftingManager.setSwitchButton(button);
    }
  }

  @SubscribeEvent
  public void guiRender(GuiScreenEvent.DrawScreenEvent.Post evt) {
    Screen screen = evt.getGui();

    if (screen instanceof CraftingScreen) {
      ClientCraftingManager.getRecipeSelectionGui().ifPresent(
          recipeSelectionGui -> recipeSelectionGui
              .render(evt.getMouseX(), evt.getMouseY(), evt.getRenderPartialTicks()));
    }
  }

  @SubscribeEvent
  public void guiMouseClick(GuiScreenEvent.MouseClickedEvent.Pre evt) {
    Screen screen = evt.getGui();

    if (screen instanceof CraftingScreen) {
      ClientCraftingManager.getRecipeSelectionGui().ifPresent(recipeSelectionGui -> {
        if (recipeSelectionGui.mouseClicked(evt.getMouseX(), evt.getMouseY(), evt.getButton())) {
          recipeSelectionGui.setVisible(false);
          evt.setCanceled(true);
        }
      });
      CraftingScreen craftingScreen = (CraftingScreen) screen;
      WorkbenchContainer workbenchContainer = craftingScreen.getContainer();
      int outputSlot = workbenchContainer.getOutputSlot();
      Slot slot = workbenchContainer.getSlot(outputSlot);

      if (slot == craftingScreen.getSlotUnderMouse()) {
        boolean isShiftKeyDown = ClientHelper.isShiftKeyDown();

        if (isShiftKeyDown) {
          ClientCraftingManager.getLastSelectedRecipe().ifPresent(recipe -> {
            ClientCraftingManager.getCurrentCraftingMatrix().ifPresent(craftingInventory -> {
              ClientPlayerEntity player = Minecraft.getInstance().player;

              for (
                  ItemStack itemstack = workbenchContainer.transferStackInSlot(player, outputSlot);
                  !itemstack.isEmpty() && ItemStack.areItemsEqual(slot.getStack(), itemstack);
                  itemstack = workbenchContainer.transferStackInSlot(player, outputSlot)) {
                slot.putStack(recipe.getCraftingResult(craftingInventory));
              }
              NetworkHandler.INSTANCE.send(PacketDistributor.SERVER.noArg(),
                  new CPacketTransferOutput(recipe.getId().toString()));
              evt.setCanceled(true);
            });
          });

        }
      }
    }
  }
}
