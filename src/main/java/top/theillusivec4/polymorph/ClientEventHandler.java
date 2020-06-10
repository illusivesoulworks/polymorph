package top.theillusivec4.polymorph;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.client.util.InputMappings;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.CraftResultInventory;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.inventory.container.Slot;
import net.minecraft.inventory.container.WorkbenchContainer;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.ICraftingRecipe;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.event.TickEvent.ClientTickEvent;
import net.minecraftforge.event.TickEvent.Phase;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;

public class ClientEventHandler {

  private static final Field CRAFT_MATRIX = ObfuscationReflectionHelper
      .findField(WorkbenchContainer.class, "field_75162_e");

  private static IRecipe<CraftingInventory> lastRecipe;
  private static CraftingInventory craftingMatrix;
  private static boolean fetch = false;

  @SubscribeEvent
  public void mouseReleased(GuiScreenEvent.MouseReleasedEvent.Pre evt) {

    if (evt.getGui() instanceof ContainerScreen) {
      ContainerScreen<?> containerScreen = (ContainerScreen<?>) evt.getGui();
      Slot slot = containerScreen.getSlotUnderMouse();
      boolean isShiftKeyDown =
          InputMappings.isKeyDown(Minecraft.getInstance().getMainWindow().getHandle(), 340)
              || InputMappings.isKeyDown(Minecraft.getInstance().getMainWindow().getHandle(), 344);

      if (slot != null && (slot.inventory instanceof CraftingInventory
          || slot.inventory instanceof CraftResultInventory || (
          slot.inventory instanceof PlayerInventory && isShiftKeyDown))) {

        if (containerScreen.getContainer() instanceof WorkbenchContainer) {
          WorkbenchContainer workbenchContainer = (WorkbenchContainer) containerScreen
              .getContainer();
          CraftingInventory craftingInventory = null;

          try {
            craftingInventory = (CraftingInventory) CRAFT_MATRIX.get(workbenchContainer);
          } catch (IllegalAccessException e) {
            Polymorph.LOGGER.error("beep beep");
          }
          ClientWorld world = Minecraft.getInstance().world;

          if (world != null && craftingInventory != null) {
            fetch = true;
            craftingMatrix = craftingInventory;
          }
        }
      }
    }
  }

  @SubscribeEvent
  public void tick(ClientTickEvent evt) {

    if (evt.phase == Phase.END && fetch) {
      fetch = false;
      ClientWorld world = Minecraft.getInstance().world;

      if (world != null) {

        if (lastRecipe == null || !lastRecipe.matches(craftingMatrix, world)) {
          List<ICraftingRecipe> recipes = world.getRecipeManager()
              .getRecipes(IRecipeType.CRAFTING, craftingMatrix, world);
          List<ItemStack> outputStacks = new ArrayList<>();
          recipes.forEach(recipe -> outputStacks.add(recipe.getRecipeOutput()));
          ClientCraftingManager.setResultsList(outputStacks);
          lastRecipe = recipes.isEmpty() ? null : recipes.get(0);
        }
      }
    }
  }
}
