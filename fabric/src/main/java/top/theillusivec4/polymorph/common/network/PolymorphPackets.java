package top.theillusivec4.polymorph.common.network;

import net.minecraft.util.Identifier;
import top.theillusivec4.polymorph.common.PolymorphMod;

public class PolymorphPackets {

  public static final Identifier GET_RECIPES = new Identifier(PolymorphMod.MOD_ID, "get_recipes");
  public static final Identifier SEND_RECIPES = new Identifier(PolymorphMod.MOD_ID, "send_recipes");
  public static final Identifier SELECT_CRAFT = new Identifier(PolymorphMod.MOD_ID, "select_craft");
  public static final Identifier SELECT_PERSIST =
      new Identifier(PolymorphMod.MOD_ID, "select_persist");
  public static final Identifier HIGHLIGHT_RECIPE =
      new Identifier(PolymorphMod.MOD_ID, "highlight_recipe");
  public static final Identifier ADD_CRAFTER = new Identifier(PolymorphMod.MOD_ID, "add_crafter");
}
