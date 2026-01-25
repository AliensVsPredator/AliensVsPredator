package com.blib.api.common.config.v1;

/**
 * Represents a single line in a properties file. This allows preserving comments, blank lines, and property order when
 * reading and writing.
 */
public sealed interface PropertyLine {

    /**
     * Converts this line back to its file representation.
     */
    String toFileLine();

    /**
     * A comment line (starts with #).
     */
    record Comment(String content) implements PropertyLine {

        @Override
        public String toFileLine() {
            return content;
        }
    }

    /**
     * A blank/whitespace-only line.
     */
    enum Blank implements PropertyLine {

        INSTANCE;

        @Override
        public String toFileLine() {
            return "";
        }
    }

    /**
     * A property line with key and raw string value.
     */
    record Property(
        String key,
        String rawValue
    ) implements PropertyLine {

        @Override
        public String toFileLine() {
            return key + " = " + rawValue;
        }
    }

    /**
     * Parses a line from a properties file.
     */
    static PropertyLine parse(String line) {
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
            // Malformed line, treat as comment to preserve it
            return new Comment(line);
        }

        var key = line.substring(0, equalsIndex).trim();
        var rawValue = line.substring(equalsIndex + 1).trim();

        return new Property(key, rawValue);
    }
}
