package top.theillusivec4.polymorph.common.util;

import org.apache.commons.lang3.reflect.FieldUtils;
import top.theillusivec4.polymorph.common.PolymorphMod;

public class FieldAccessor {

  public static Object read(Object target, String fieldName) {
    try {
      return FieldUtils.readField(target, fieldName, true);
    } catch (IllegalAccessException e) {
      PolymorphMod.LOGGER.error("Failed to read {} from {}", fieldName, target);
    }
    return null;
  }

  public static void write(Object target, String fieldName, Object value) {
    try {
      FieldUtils.writeField(target, fieldName, value, true);
    } catch (IllegalAccessException e) {
      PolymorphMod.LOGGER.error("Failed to write {} to {} for {}", value, fieldName, target);
    } catch (IllegalArgumentException e) {
      PolymorphMod.LOGGER.debug("Failed to find {} from {}", fieldName, target);
    }
  }
}
