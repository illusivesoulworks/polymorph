package top.theillusivec4.polymorph.common.integration.prettypipes;

import de.ellpeck.prettypipes.terminal.containers.CraftingTerminalContainer;
import top.theillusivec4.polymorph.api.PolymorphApi;
import top.theillusivec4.polymorph.common.integration.AbstractCompatibilityModule;

public class PrettyPipesModule extends AbstractCompatibilityModule {

  @Override
  public void clientSetup() {
    PolymorphApi.client().registerWidget(containerScreen -> {
      if (containerScreen.getContainer() instanceof CraftingTerminalContainer) {
        return PolymorphApi.client().getCraftingPair(containerScreen).map(
            data -> new CraftingTerminalRecipesWidget(containerScreen, data.getSecond(),
                data.getFirst())).orElse(null);
      }
      return null;
    });
  }
}
