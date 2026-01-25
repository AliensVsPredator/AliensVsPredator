package com.blib.api.common.property.v1;

public sealed interface BLibPropertyLine {

    String toFileLine();

    record Comment(String content) implements BLibPropertyLine {

        @Override
        public String toFileLine() {
            return content;
        }
    }

    enum Blank implements BLibPropertyLine {

        INSTANCE;

        @Override
        public String toFileLine() {
            return "";
        }
    }

    record Property(
        String key,
        String rawValue
    ) implements BLibPropertyLine {

        @Override
        public String toFileLine() {
            return key + " = " + rawValue;
        }
    }

    static BLibPropertyLine parse(String line) {
        if (line == null) {
            return Blank.INSTANCE;
        }

        var trimmed = line.trim();

        // Blank line
        if (trimmed.isEmpty()) {
            return Blank.INSTANCE;
        }

        // Comment line
        if (trimmed.startsWith("#")) {
            return new Comment(line);
        }

        // Property line
        var equalsIndex = line.indexOf('=');

        if (equalsIndex == -1) {
            // Malformed line, treat as text to preserve it
            return new Comment(line);
        }

        var key = line.substring(0, equalsIndex).trim();
        var rawValue = line.substring(equalsIndex + 1).trim();

        return new Property(key, rawValue);
    }
}
