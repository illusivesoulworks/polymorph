package top.theillusivec4.polymorph.common.integration.occultism;

import com.github.klikli_dev.occultism.common.container.storage.StorageControllerContainerBase;
import top.theillusivec4.polymorph.api.PolymorphApi;
import top.theillusivec4.polymorph.common.PolymorphMod;
import top.theillusivec4.polymorph.common.integration.AbstractCompatibilityModule;

public class OccultismModule extends AbstractCompatibilityModule {

  @Override
  public void clientSetup() {
    PolymorphApi.client().registerWidget(pContainerScreen -> {
      if (pContainerScreen.getContainer() instanceof StorageControllerContainerBase) {
        return PolymorphApi.client().findCraftingResultSlot(pContainerScreen)
            .map(slot -> new StorageControllerWidget(pContainerScreen, slot)).orElse(null);
      }
      return null;
    });
  }
}
