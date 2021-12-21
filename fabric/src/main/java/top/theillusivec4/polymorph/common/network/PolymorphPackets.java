package top.theillusivec4.polymorph.common.network;

import net.minecraft.util.Identifier;
import top.theillusivec4.polymorph.api.PolymorphApi;

public class PolymorphPackets {

  public static final Identifier PLAYER_SELECT =
      new Identifier(PolymorphApi.MOD_ID, "player_select");
  public static final Identifier PERSISTENT_SELECT =
      new Identifier(PolymorphApi.MOD_ID, "persistent_select");
  public static final Identifier STACK_SELECT = new Identifier(PolymorphApi.MOD_ID, "stack_select");
  public static final Identifier RECIPES_LIST = new Identifier(PolymorphApi.MOD_ID, "recipes_list");
  public static final Identifier HIGHLIGHT_RECIPE =
      new Identifier(PolymorphApi.MOD_ID, "highlight_recipe");
  public static final Identifier RECIPE_SYNC = new Identifier(PolymorphApi.MOD_ID, "recipe_sync");
}
