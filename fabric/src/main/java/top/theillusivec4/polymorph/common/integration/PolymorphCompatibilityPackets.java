package top.theillusivec4.polymorph.common.integration;

import net.minecraft.util.Identifier;
import top.theillusivec4.polymorph.common.PolymorphMod;

public class PolymorphCompatibilityPackets {

  public static final Identifier SELECT_AE2_CRAFT =
      new Identifier(PolymorphMod.MOD_ID, "select_ae2_craft");
  public static final Identifier UPDATE_AE2_CRAFT =
      new Identifier(PolymorphMod.MOD_ID, "update_ae2_craft");
  public static final Identifier SELECT_AE2_PATTERN =
      new Identifier(PolymorphMod.MOD_ID, "select_ae2_pattern");
  public static final Identifier UPDATE_AE2_PATTERN =
      new Identifier(PolymorphMod.MOD_ID, "update_ae2_pattern");
}
