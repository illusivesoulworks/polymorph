package top.theillusivec4.polymorph.common.integration.appliedenergistics2.client;

import appeng.container.me.items.CraftingTermContainer;
import appeng.container.me.items.PatternTermContainer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.recipe.CraftingRecipe;
import net.minecraft.recipe.Recipe;
import net.minecraft.util.Identifier;
import top.theillusivec4.polymorph.common.integration.PolymorphCompatibilityPackets;
import top.theillusivec4.polymorph.mixin.integration.AccessorCraftingTermContainer;
import top.theillusivec4.polymorph.mixin.integration.AccessorPatternTermContainer;

public class PolymorphAppengClientNetwork {

  @SuppressWarnings("unchecked")
  public static void setup() {
    ClientPlayNetworking.registerGlobalReceiver(PolymorphCompatibilityPackets.UPDATE_AE2_CRAFT,
        (client, handler, buf, responseSender) -> {
          Identifier id = buf.readIdentifier();
          client.execute(() -> {
            ClientPlayerEntity player = client.player;

            if (player != null && player.currentScreenHandler instanceof CraftingTermContainer) {
              player.world.getRecipeManager().get(id).ifPresent(recipe -> {
                ((AccessorCraftingTermContainer) player.currentScreenHandler).setCurrentRecipe(
                    (Recipe<CraftingInventory>) recipe);
                player.currentScreenHandler.onContentChanged(player.inventory);
              });
            }
          });
        }
    );
    ClientPlayNetworking.registerGlobalReceiver(PolymorphCompatibilityPackets.UPDATE_AE2_PATTERN,
        (client, handler, buf, responseSender) -> {
          Identifier id = buf.readIdentifier();
          client.execute(() -> {
            ClientPlayerEntity player = client.player;

            if (player != null && player.currentScreenHandler instanceof PatternTermContainer) {
              player.world.getRecipeManager().get(id).ifPresent(recipe -> {
                ((AccessorPatternTermContainer) player.currentScreenHandler).setCurrentRecipe(
                    (CraftingRecipe) recipe);
                ((AccessorPatternTermContainer) player.currentScreenHandler).callGetAndUpdateOutput();
              });
            }
          });
        }
    );
  }
}
