package top.theillusivec4.polymorph.common.util;

import java.lang.reflect.InvocationTargetException;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.apache.commons.lang3.reflect.MethodUtils;
import top.theillusivec4.polymorph.common.PolymorphMod;

public class PolymorphAccessor {

  public static Object readField(Object pTarget, String pFieldName) {
    try {
      return FieldUtils.readField(pTarget, pFieldName, true);
    } catch (IllegalAccessException e) {
      PolymorphMod.LOGGER.error("Failed to read {} from {}", pFieldName, pTarget);
    } catch (IllegalArgumentException e) {
      PolymorphMod.LOGGER.debug("Failed to find {} from {}", pFieldName, pTarget);
    }
    return null;
  }

  public static void writeField(Object pTarget, String pFieldName, Object pValue) {
    try {
      FieldUtils.writeField(pTarget, pFieldName, pValue, true);
    } catch (IllegalAccessException e) {
      PolymorphMod.LOGGER.error("Failed to write {} to {} for {}", pValue, pFieldName, pTarget);
    } catch (IllegalArgumentException e) {
      PolymorphMod.LOGGER.debug("Failed to find {} from {}", pFieldName, pTarget);
    }
  }

  public static void invokeMethod(Object pTarget, String pMethodName) {
    try {
      MethodUtils.invokeMethod(pTarget, true, pMethodName);
    } catch (InvocationTargetException | IllegalAccessException e) {
      PolymorphMod.LOGGER.error("Failed to invoke {} for {}", pMethodName, pTarget);
    } catch (NoSuchMethodException e) {
      PolymorphMod.LOGGER.debug("Failed to find {} from {}", pMethodName, pTarget);
    }
  }
}
