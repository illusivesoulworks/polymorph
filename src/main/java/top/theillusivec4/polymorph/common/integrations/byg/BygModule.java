package top.theillusivec4.polymorph.common.integrations.byg;

import top.theillusivec4.polymorph.api.PolymorphApi;
import top.theillusivec4.polymorph.common.integrations.CompatibilityModule;
import top.theillusivec4.polymorph.common.provider.WorkbenchProvider;
import voronoiaoc.byg.client.gui.BYGWorkbenchContainer;

public class BygModule extends CompatibilityModule {

  @Override
  public void setup() {
    PolymorphApi.addProvider(BYGWorkbenchContainer.class, WorkbenchProvider::new);
  }
}
