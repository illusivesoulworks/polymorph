package top.theillusivec4.polymorph.api;

import dev.onyxstudios.cca.api.v3.component.ComponentKey;
import dev.onyxstudios.cca.api.v3.component.ComponentRegistry;
import net.minecraft.util.Identifier;
import top.theillusivec4.polymorph.api.type.PersistentSelector;
import top.theillusivec4.polymorph.core.Polymorph;

public class PolymorphComponent {

  public static final ComponentKey<PersistentSelector> SELECTOR = ComponentRegistry
      .getOrCreate(new Identifier(Polymorph.MODID, "selector"), PersistentSelector.class);
}
