package top.theillusivec4.polymorph.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.inventory.CraftingScreen;
import net.minecraft.client.util.InputMappings;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.inventory.container.Slot;
import net.minecraft.inventory.container.WorkbenchContainer;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.event.TickEvent.ClientTickEvent;
import net.minecraftforge.event.TickEvent.Phase;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.network.PacketDistributor;
import top.theillusivec4.polymorph.client.gui.RecipeSelectionGui;
import top.theillusivec4.polymorph.common.network.NetworkHandler;
import top.theillusivec4.polymorph.common.network.client.CPacketTransferRecipe;

public class ClientEventHandler {

  @SubscribeEvent
  public void tick(ClientTickEvent evt) {

    if (evt.phase == Phase.END) {
      ClientWorld world = Minecraft.getInstance().world;

      if (world != null) {
        RecipeConflictManager.getInstance().ifPresent(craftingManager -> {

          if (craftingManager.needsUpdate()) {
            craftingManager.update();
          }
        });
      }
    }
  }

  @SubscribeEvent
  public void guiInit(GuiScreenEvent.InitGuiEvent.Post evt) {
    Screen screen = evt.getGui();

    if (screen instanceof CraftingScreen) {
      RecipeConflictManager craftingManager = RecipeConflictManager
          .refreshInstance((CraftingScreen) screen);
      evt.addWidget(craftingManager.getSwitchButton());
    }
  }

  @SubscribeEvent
  public void guiRender(GuiScreenEvent.DrawScreenEvent.Post evt) {
    Screen screen = evt.getGui();

    if (screen instanceof CraftingScreen) {
      RecipeConflictManager.getInstance().ifPresent(
          craftingManager -> craftingManager.getRecipeSelectionGui()
              .render(evt.getMouseX(), evt.getMouseY(), evt.getRenderPartialTicks()));
    }
  }

  @SubscribeEvent
  public void guiMouseClick(GuiScreenEvent.MouseClickedEvent.Pre evt) {
    Screen screen = evt.getGui();

    if (screen instanceof CraftingScreen) {
      RecipeConflictManager.getInstance().ifPresent(craftingManager -> {
        RecipeSelectionGui recipeSelectionGui = craftingManager.getRecipeSelectionGui();

        if (recipeSelectionGui.mouseClicked(evt.getMouseX(), evt.getMouseY(), evt.getButton())) {
          recipeSelectionGui.setVisible(false);
          evt.setCanceled(true);
        } else if (recipeSelectionGui.isVisible()) {

          if (!craftingManager.getSwitchButton()
              .mouseClicked(evt.getMouseX(), evt.getMouseY(), evt.getButton())) {
            recipeSelectionGui.setVisible(false);
          }
          evt.setCanceled(true);
        }
        CraftingScreen craftingScreen = (CraftingScreen) screen;
        WorkbenchContainer workbenchContainer = craftingScreen.getContainer();
        int outputSlot = workbenchContainer.getOutputSlot();
        Slot slot = workbenchContainer.getSlot(outputSlot);

        if (slot == craftingScreen.getSlotUnderMouse() && isShiftKeyDown()) {
          craftingManager.getLastSelectedRecipe().ifPresent(
              recipe -> craftingManager.getCurrentCraftingMatrix().ifPresent(craftingInventory -> {
                NetworkHandler.INSTANCE.send(PacketDistributor.SERVER.noArg(),
                    new CPacketTransferRecipe(recipe.getId().toString()));
                evt.setCanceled(true);
              }));

        }
      });
    }
  }

  private static final int GLFW_LEFT_SHIFT = 340;
  private static final int GLFW_RIGHT_SHIFT = 344;

  private static boolean isShiftKeyDown() {
    long handle = Minecraft.getInstance().getMainWindow().getHandle();
    return InputMappings.isKeyDown(handle, GLFW_LEFT_SHIFT) || InputMappings
        .isKeyDown(handle, GLFW_RIGHT_SHIFT);
  }
}
