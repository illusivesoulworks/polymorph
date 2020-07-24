package top.theillusivec4.polymorph.loader.client;

import top.theillusivec4.polymorph.core.base.client.ClientAccessor;
import top.theillusivec4.polymorph.core.base.client.ClientLoader;
import top.theillusivec4.polymorph.loader.impl.ClientAccessorImpl;

public class PolymorphClientLoader implements ClientLoader {

  private static final ClientAccessor ACCESSOR = new ClientAccessorImpl();

  @Override
  public ClientAccessor getClientAccessor() {
    return ACCESSOR;
  }
}
