package top.theillusivec4.polymorph.util;

import net.minecraft.client.Minecraft;
import net.minecraft.client.util.InputMappings;

public class ClientHelper {

  private static final int GLFW_LEFT_SHIFT = 340;
  private static final int GLFW_RIGHT_SHIFT = 344;

  public static boolean isShiftKeyDown() {
    long handle = Minecraft.getInstance().getMainWindow().getHandle();
    return InputMappings.isKeyDown(handle, GLFW_LEFT_SHIFT) || InputMappings
        .isKeyDown(handle, GLFW_RIGHT_SHIFT);
  }
}
