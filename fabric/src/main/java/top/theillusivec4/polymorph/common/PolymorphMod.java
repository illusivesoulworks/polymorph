package top.theillusivec4.polymorph.common;

import net.fabricmc.api.ModInitializer;
import net.minecraft.block.entity.AbstractFurnaceBlockEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.screen.slot.Slot;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import top.theillusivec4.polymorph.api.PolymorphApi;
import top.theillusivec4.polymorph.api.common.base.PolymorphCommon;
import top.theillusivec4.polymorph.common.component.FurnaceRecipeData;
import top.theillusivec4.polymorph.common.integration.PolymorphIntegrations;
import top.theillusivec4.polymorph.common.network.PolymorphNetwork;
import top.theillusivec4.polymorph.server.PolymorphCommands;

public class PolymorphMod implements ModInitializer {

  public static final String MOD_ID = "polymorph";
  public static final Logger LOGGER = LogManager.getLogger();

  @Override
  public void onInitialize() {
    PolymorphNetwork.setup();
    PolymorphCommands.setup();
    CommonEventsListener.setup();
    PolymorphCommon commonApi = PolymorphApi.common();
    commonApi.registerBlockEntity2RecipeData(AbstractFurnaceBlockEntity.class,
        blockEntity -> new FurnaceRecipeData((AbstractFurnaceBlockEntity) blockEntity));
    commonApi.registerScreenHandler2BlockEntity(container -> {
      for (Slot inventorySlot : container.slots) {
        Inventory inventory = inventorySlot.inventory;

        if (inventory instanceof BlockEntity) {
          return (BlockEntity) inventory;
        }
      }
      return null;
    });
    PolymorphIntegrations.init();
    PolymorphIntegrations.setup();
  }
}
