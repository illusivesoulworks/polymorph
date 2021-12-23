package top.theillusivec4.polymorph.client.recipe.widget;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.screen.slot.Slot;
import net.minecraft.util.Identifier;
import top.theillusivec4.polymorph.api.PolymorphApi;
import top.theillusivec4.polymorph.api.client.widget.AbstractRecipesWidget;
import top.theillusivec4.polymorph.api.common.base.PolymorphCommon;

public class PlayerRecipesWidget extends AbstractRecipesWidget {

  final Slot outputSlot;

  public PlayerRecipesWidget(HandledScreen<?> pHandledScreen, Slot pOutputSlot) {
    super(pHandledScreen);
    this.outputSlot = pOutputSlot;
  }

  @Override
  public void selectRecipe(Identifier pIdentifier) {
    PolymorphCommon commonApi = PolymorphApi.common();
    PlayerEntity player = MinecraftClient.getInstance().player;

    if (player != null) {
      player.world.getRecipeManager().get(pIdentifier).ifPresent(
          recipe -> commonApi.getRecipeData(player)
              .ifPresent(recipeData -> recipeData.selectRecipe(recipe)));
    }
    commonApi.getPacketDistributor().sendPlayerRecipeSelectionC2S(pIdentifier);
  }

  @Override
  public Slot getOutputSlot() {
    return this.outputSlot;
  }
}
