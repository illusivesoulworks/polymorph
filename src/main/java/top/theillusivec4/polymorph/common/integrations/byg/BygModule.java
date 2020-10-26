package top.theillusivec4.polymorph.common.integrations.byg;

import corgiaoc.byg.client.gui.BYGWorkbenchContainer;
import top.theillusivec4.polymorph.api.PolymorphApi;
import top.theillusivec4.polymorph.common.integrations.CompatibilityModule;
import top.theillusivec4.polymorph.common.provider.WorkbenchProvider;

public class BygModule extends CompatibilityModule {

  @Override
  public void setup() {
    PolymorphApi.addProvider(BYGWorkbenchContainer.class, WorkbenchProvider::new);
  }
}
