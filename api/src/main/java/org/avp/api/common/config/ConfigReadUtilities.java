package org.avp.api.common.config;

public class ConfigReadUtilities {

    public static Object parseValueToType(String value, Class<?> type) {
        if (value.isBlank() && !type.isPrimitive())
            return null;
        if (value.isBlank() && (type == int.class || type == float.class))
            return 0;
        if (type == boolean.class || type == Boolean.class)
            return Boolean.parseBoolean(value);
        if (type == int.class || type == Integer.class)
            return Integer.parseInt(value);
        if (type == float.class || type == Float.class)
            return Float.parseFloat(value);
        if (type == String.class)
            return value;
        throw new IllegalStateException("Config value '" + value + "' cannot be parsed to type " + type.getName());
    }

    public static boolean isLessThan(String min, Object value) {
        if (min == null || !(value instanceof Number)) {
            return false;
        }

        if (value instanceof Integer intValue) {
            return intValue < Float.parseFloat(min);
        }

        if (value instanceof Float floatValue) {
            return floatValue < Float.parseFloat(min);
        }

        return false;
    }

    public static boolean isGreaterThan(String max, Object value) {
        if (max == null || !(value instanceof Number)) {
            return false;
        }

        if (value instanceof Integer intValue) {
            return intValue > Float.parseFloat(max);
        }

        if (value instanceof Float floatValue) {
            return floatValue > Float.parseFloat(max);
        }

        return false;
    }

    private ConfigReadUtilities() {
        throw new UnsupportedOperationException();
    }
}
