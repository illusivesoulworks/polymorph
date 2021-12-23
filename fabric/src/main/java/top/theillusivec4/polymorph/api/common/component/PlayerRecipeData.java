package top.theillusivec4.polymorph.api.common.component;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.screen.ScreenHandler;

public interface PlayerRecipeData extends RecipeData<PlayerEntity> {

  void setScreenHandler(ScreenHandler screenHandler);

  ScreenHandler getScreenHandler();
}
