package com.blib.api.common.property.v1.serializer;

import org.jetbrains.annotations.Nullable;

public final class BLibPropertySerializers {

    private BLibPropertySerializers() {}

    public static final BLibPropertySerializer<Boolean> BOOLEAN = new BLibPropertySerializer<>() {

        @Override
        public @Nullable Boolean deserializeOrNull(String raw) {
            if (raw == null || raw.isBlank()) {
                return null;
            }

            var trimmed = raw.trim();

            if (trimmed.equalsIgnoreCase("true")) {
                return true;
            }

            if (trimmed.equalsIgnoreCase("false")) {
                return false;
            }

            return null;
        }

        @Override
        public String serialize(Boolean value) {
            return String.valueOf(value);
        }
    };

    public static final BLibPropertySerializer<Double> DOUBLE = new BLibPropertySerializer<>() {

        @Override
        public @Nullable Double deserializeOrNull(String raw) {
            if (raw == null || raw.isBlank()) {
                return null;
            }

            try {
                return Double.parseDouble(raw.trim());
            } catch (NumberFormatException e) {
                return null;
            }
        }

        @Override
        public String serialize(Double value) {
            return String.valueOf(value);
        }
    };

    public static final BLibPropertySerializer<Float> FLOAT = new BLibPropertySerializer<>() {

        @Override
        public @Nullable Float deserializeOrNull(String raw) {
            if (raw == null || raw.isBlank()) {
                return null;
            }

            try {
                return Float.parseFloat(raw.trim());
            } catch (NumberFormatException e) {
                return null;
            }
        }

        @Override
        public String serialize(Float value) {
            return String.valueOf(value);
        }
    };

    public static final BLibPropertySerializer<Integer> INT = new BLibPropertySerializer<>() {

        @Override
        public @Nullable Integer deserializeOrNull(String raw) {
            if (raw == null || raw.isBlank()) {
                return null;
            }

            try {
                return Integer.parseInt(raw.trim());
            } catch (NumberFormatException e) {
                return null;
            }
        }

        @Override
        public String serialize(Integer value) {
            return String.valueOf(value);
        }
    };

    public static final BLibPropertySerializer<Long> LONG = new BLibPropertySerializer<>() {

        @Override
        public @Nullable Long deserializeOrNull(String raw) {
            if (raw == null || raw.isBlank()) {
                return null;
            }

            try {
                return Long.parseLong(Long.trim());
            } catch (NumberFormatException e) {
                return null;
            }
        }

        @Override
        public String serialize(Long value) {
            return String.valueOf(value);
        }
    };

    public static final BLibPropertySerializer<String> STRING = new BLibPropertySerializer<>() {

        @Override
        public @Nullable String deserializeOrNull(String raw) {
            if (raw == null || raw.isBlank()) {
                return null;
            }

            var trimmed = raw.trim();

            if (trimmed.startsWith("\"") && trimmed.endsWith("\"") && trimmed.length() >= 2) {
                var inner = trimmed.substring(1, trimmed.length() - 1);
                var unescaped = inner.replace("\\\"", "\"").replace("\\\\", "\\");

                return unescaped;
            }

            return null;
        }

        @Override
        public String serialize(String value) {
            return "\"" + value.replace("\\", "\\\\").replace("\"", "\\\"") + "\"";
        }
    };
}
