package top.theillusivec4.polymorph.api;

import top.theillusivec4.polymorph.api.client.base.IPolymorphClient;
import top.theillusivec4.polymorph.api.common.base.IPolymorphCommon;

public final class PolymorphApi {

  public static final String MOD_ID = "polymorph";

  public static IPolymorphCommon common() {
    throw new IllegalStateException("Polymorph Common API missing!");
  }

  public static IPolymorphClient client() {
    throw new IllegalStateException("Polymorph Client API missing!");
  }
}
