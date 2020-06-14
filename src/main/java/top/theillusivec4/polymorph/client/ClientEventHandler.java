package top.theillusivec4.polymorph.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.recipebook.IRecipeShownListener;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.client.gui.screen.inventory.CraftingScreen;
import net.minecraft.client.gui.screen.inventory.InventoryScreen;
import net.minecraft.client.gui.widget.button.ImageButton;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.inventory.container.PlayerContainer;
import net.minecraft.inventory.container.WorkbenchContainer;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.event.TickEvent.ClientTickEvent;
import net.minecraftforge.event.TickEvent.Phase;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import top.theillusivec4.polymorph.Polymorph;
import top.theillusivec4.polymorph.api.PolymorphApi;
import top.theillusivec4.polymorph.common.integrations.CraftingStationModule;

public class ClientEventHandler {

  @SubscribeEvent
  public void tick(ClientTickEvent evt) {

    if (evt.phase == Phase.END) {
      ClientWorld world = Minecraft.getInstance().world;

      if (world != null) {
        RecipeConflictManager.getInstance().ifPresent(RecipeConflictManager::tick);
      }
    }
  }

  @SubscribeEvent
  public void guiInit(GuiScreenEvent.InitGuiEvent.Post evt) {
    Screen screen = evt.getGui();
    RecipeConflictManager<?> conflictManager = null;

    if (screen instanceof ContainerScreen) {
      ContainerScreen<?> containerScreen = (ContainerScreen<?>) screen;

      if (screen instanceof CraftingScreen && containerScreen
          .getContainer() instanceof WorkbenchContainer) {
        CraftingScreen craftingScreen = (CraftingScreen) screen;
        WorkbenchContainer workbenchContainer = (WorkbenchContainer) containerScreen.getContainer();
        conflictManager = PolymorphApi.getProvider(workbenchContainer)
            .map(provider -> RecipeConflictManager.refreshInstance(craftingScreen, provider))
            .orElse(null);
      } else if (screen instanceof InventoryScreen && containerScreen
          .getContainer() instanceof PlayerContainer) {
        InventoryScreen inventoryScreen = (InventoryScreen) screen;
        PlayerContainer playerContainer = (PlayerContainer) containerScreen.getContainer();
        conflictManager = PolymorphApi.getProvider(playerContainer)
            .map(provider -> RecipeConflictManager.refreshInstance(inventoryScreen, provider))
            .orElse(null);
      } else if (Polymorph.isCraftingStationLoaded) {
        conflictManager = CraftingStationModule.Client.getConflictManager(containerScreen);
      }
    }

    if (conflictManager != null) {
      evt.addWidget(conflictManager.getSwitchButton());
    } else {
      RecipeConflictManager.clearInstance();
    }
  }

  @SubscribeEvent
  public void guiRender(GuiScreenEvent.DrawScreenEvent.Post evt) {

    if (evt.getGui() instanceof ContainerScreen) {
      RecipeConflictManager.getInstance().ifPresent(conflictManager -> conflictManager
          .renderRecipeSelectionGui(evt.getMouseX(), evt.getMouseY(), evt.getRenderPartialTicks()));
    }
  }

  @SubscribeEvent
  public void guiMouseClick(GuiScreenEvent.MouseClickedEvent.Pre evt) {

    if (evt.getGui() instanceof ContainerScreen) {
      RecipeConflictManager.getInstance().ifPresent(RecipeConflictManager::updatePosition);

      if (RecipeConflictManager.getInstance().map(conflictManager -> conflictManager
          .mouseClicked(evt.getMouseX(), evt.getMouseY(), evt.getButton())).orElse(false)) {
        evt.setCanceled(true);
      }
    }
  }
}
