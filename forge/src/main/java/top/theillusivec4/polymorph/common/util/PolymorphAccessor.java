package top.theillusivec4.polymorph.common.util;

import java.lang.reflect.InvocationTargetException;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.apache.commons.lang3.reflect.MethodUtils;
import top.theillusivec4.polymorph.common.PolymorphMod;

public class PolymorphAccessor {

  public static Object readField(Object target, String fieldName) {
    try {
      return FieldUtils.readField(target, fieldName, true);
    } catch (IllegalAccessException e) {
      PolymorphMod.LOGGER.error("Failed to read {} from {}", fieldName, target);
    } catch (IllegalArgumentException e) {
      PolymorphMod.LOGGER.debug("Failed to find {} from {}", fieldName, target);
    }
    return null;
  }

  public static void writeField(Object target, String fieldName, Object value) {
    try {
      FieldUtils.writeField(target, fieldName, value, true);
    } catch (IllegalAccessException e) {
      PolymorphMod.LOGGER.error("Failed to write {} to {} for {}", value, fieldName, target);
    } catch (IllegalArgumentException e) {
      PolymorphMod.LOGGER.debug("Failed to find {} from {}", fieldName, target);
    }
  }

  public static void invokeMethod(Object target, String methodName) {
    try {
      MethodUtils.invokeMethod(target, methodName);
    } catch (InvocationTargetException | IllegalAccessException e) {
      PolymorphMod.LOGGER.error("Failed to invoke {} for {}", methodName, target);
    } catch (NoSuchMethodException e) {
      PolymorphMod.LOGGER.error("Failed to find {} from {}", methodName, target);
    }
  }
}
