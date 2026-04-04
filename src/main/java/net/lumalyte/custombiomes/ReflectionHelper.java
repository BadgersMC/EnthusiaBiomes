package net.lumalyte.custombiomes;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * Helper class for reflection operations on obfuscated NMS classes
 * Based on TerraformGenerator's reflection utilities
 */
public class ReflectionHelper {

    /**
     * Gets a field from a class, trying both the deobfuscated and obfuscated names
     */
    public static class FieldHelper {
        public final Field field;

        public FieldHelper(Class<?> clazz, String deobfName, String obfName) throws NoSuchFieldException {
            Field foundField = null;

            // Try deobfuscated name first
            try {
                foundField = clazz.getDeclaredField(deobfName);
            } catch (NoSuchFieldException e) {
                // Try obfuscated name
                try {
                    foundField = clazz.getDeclaredField(obfName);
                } catch (NoSuchFieldException e2) {
                    throw new NoSuchFieldException("Could not find field " + deobfName + " or " + obfName + " in " + clazz.getName());
                }
            }

            foundField.setAccessible(true);
            this.field = foundField;
        }
    }

    /**
     * Gets a method from a class, trying multiple possible names
     */
    public static class MethodHelper {
        public final Method method;

        public MethodHelper(Class<?> clazz, String[] possibleNames, Class<?>... parameterTypes) throws NoSuchMethodException {
            Method foundMethod = null;

            for (String name : possibleNames) {
                try {
                    foundMethod = clazz.getDeclaredMethod(name, parameterTypes);
                    break;
                } catch (NoSuchMethodException e) {
                    // Continue trying other names
                }
            }

            if (foundMethod == null) {
                throw new NoSuchMethodException("Could not find method " + String.join(" or ", possibleNames) + " in " + clazz.getName());
            }

            foundMethod.setAccessible(true);
            this.method = foundMethod;
        }
    }
}
