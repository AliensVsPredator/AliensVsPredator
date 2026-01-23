package com.blib.common.config.property.serializer;

import com.just.core.functional.option.Option;

/**
 * Built-in property serializers for common types.
 */
public final class PropertySerializers {

    private PropertySerializers() {}

    /**
     * Serializer for Long values.
     */
    public static final PropertySerializer<Long> LONG = new PropertySerializer<>() {

        @Override
        public Option<Long> deserialize(String raw) {
            if (raw == null || raw.isBlank()) {
                return Option.none();
            }

            try {
                return Option.some(Long.parseLong(raw.trim()));
            } catch (NumberFormatException e) {
                return Option.none();
            }
        }

        @Override
        public String serialize(Long value) {
            return String.valueOf(value);
        }
    };

    /**
     * Serializer for Integer values.
     */
    public static final PropertySerializer<Integer> INTEGER = new PropertySerializer<>() {

        @Override
        public Option<Integer> deserialize(String raw) {
            if (raw == null || raw.isBlank()) {
                return Option.none();
            }

            try {
                return Option.some(Integer.parseInt(raw.trim()));
            } catch (NumberFormatException e) {
                return Option.none();
            }
        }

        @Override
        public String serialize(Integer value) {
            return String.valueOf(value);
        }
    };

    /**
     * Serializer for Double values.
     */
    public static final PropertySerializer<Double> DOUBLE = new PropertySerializer<>() {

        @Override
        public Option<Double> deserialize(String raw) {
            if (raw == null || raw.isBlank()) {
                return Option.none();
            }

            try {
                return Option.some(Double.parseDouble(raw.trim()));
            } catch (NumberFormatException e) {
                return Option.none();
            }
        }

        @Override
        public String serialize(Double value) {
            return String.valueOf(value);
        }
    };

    /**
     * Serializer for Float values.
     */
    public static final PropertySerializer<Float> FLOAT = new PropertySerializer<>() {

        @Override
        public Option<Float> deserialize(String raw) {
            if (raw == null || raw.isBlank()) {
                return Option.none();
            }

            try {
                return Option.some(Float.parseFloat(raw.trim()));
            } catch (NumberFormatException e) {
                return Option.none();
            }
        }

        @Override
        public String serialize(Float value) {
            return String.valueOf(value);
        }
    };

    /**
     * Serializer for String values. Expects quoted strings in the format "value" with escape sequences.
     */
    public static final PropertySerializer<String> STRING = new PropertySerializer<>() {

        @Override
        public Option<String> deserialize(String raw) {
            if (raw == null || raw.isBlank()) {
                return Option.none();
            }

            var trimmed = raw.trim();

            if (trimmed.startsWith("\"") && trimmed.endsWith("\"") && trimmed.length() >= 2) {
                var inner = trimmed.substring(1, trimmed.length() - 1);
                var unescaped = inner.replace("\\\"", "\"").replace("\\\\", "\\");
                return Option.some(unescaped);
            }

            return Option.none();
        }

        @Override
        public String serialize(String value) {
            return "\"" + value.replace("\\", "\\\\").replace("\"", "\\\"") + "\"";
        }
    };

    /**
     * Serializer for Boolean values.
     */
    public static final PropertySerializer<Boolean> BOOLEAN = new PropertySerializer<>() {

        @Override
        public Option<Boolean> deserialize(String raw) {
            if (raw == null || raw.isBlank()) {
                return Option.none();
            }

            var trimmed = raw.trim();

            if (trimmed.equalsIgnoreCase("true")) {
                return Option.some(true);
            }

            if (trimmed.equalsIgnoreCase("false")) {
                return Option.some(false);
            }

            return Option.none();
        }

        @Override
        public String serialize(Boolean value) {
            return String.valueOf(value);
        }
    };

}
