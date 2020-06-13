package top.theillusivec4.polymorph.common.integrations;

import shadows.fastbench.gui.ContainerFastBench;
import top.theillusivec4.polymorph.api.PolymorphApi;
import top.theillusivec4.polymorph.common.provider.WorkbenchProvider;

public class FastWorkbenchModule {

  public static void setup() {
    PolymorphApi.addProvider(ContainerFastBench.class, new WorkbenchProvider());
  }
}
