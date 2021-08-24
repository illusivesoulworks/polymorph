package top.theillusivec4.polymorph.common.integration.appliedenergistics2;

import appeng.container.me.items.CraftingTermContainer;
import appeng.container.me.items.PatternTermContainer;
import net.minecraft.screen.ScreenHandler;
import top.theillusivec4.polymorph.api.PolymorphApi;
import top.theillusivec4.polymorph.common.integration.AbstractCompatibilityModule;
import top.theillusivec4.polymorph.common.integration.appliedenergistics2.client.PolymorphAppengClientNetwork;

public class AppliedEnergisticsModule extends AbstractCompatibilityModule {

  @Override
  public void setup() {
    PolymorphApi.getInstance().addRecipeController(handledScreen -> {
      ScreenHandler screenHandler = handledScreen.getScreenHandler();

      if (screenHandler instanceof CraftingTermContainer) {
        return new CraftingTerminalRecipeController(handledScreen);
      } else if (screenHandler instanceof PatternTermContainer) {
        return new PatternTerminalRecipeController(handledScreen);
      }
      return null;
    });
    PolymorphAppengNetwork.setup();
  }

  @Override
  public void clientSetup() {
    PolymorphAppengClientNetwork.setup();
  }
}
