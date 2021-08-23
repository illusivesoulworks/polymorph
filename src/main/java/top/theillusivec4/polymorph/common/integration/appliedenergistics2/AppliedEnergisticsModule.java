package top.theillusivec4.polymorph.common.integration.appliedenergistics2;

import appeng.container.me.items.CraftingTermContainer;
import top.theillusivec4.polymorph.api.PolymorphApi;
import top.theillusivec4.polymorph.common.integration.AbstractCompatibilityModule;
import top.theillusivec4.polymorph.common.integration.appliedenergistics2.client.PolymorphAppengClientNetwork;

public class AppliedEnergisticsModule extends AbstractCompatibilityModule {

  @Override
  public void setup() {
    PolymorphApi.getInstance().addRecipeController(handledScreen -> {
      if (handledScreen.getScreenHandler() instanceof CraftingTermContainer) {
        return new AppliedEnergisticsRecipeController(handledScreen);
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
