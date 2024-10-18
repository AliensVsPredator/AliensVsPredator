package org.avp.api.common.config;

import java.util.Set;

public class ConfigConstants {

    protected static final String CATEGORY_START = "#>";

    protected static final String CATEGORY_END = "<#";

    protected static final String COMMENT = "#";

    protected static final String COMMENT_REGEX = "#\\s*";

    protected static final String DEFAULT = "default:";

    protected static final String EQUALS = "=";

    protected static final String INTERNAL_COMMENT = "#~";

    protected static final String INTERNAL_COMMENT_REGEX = "#~\\s*";

    protected static final String INTERNAL_COMMENT_SEPARATOR = ", ";

    protected static final String INTERNAL_COMMENT_SEPARATOR_REGEX = "\\s*,\\s*";

    protected static final String MIN = "min:";

    protected static final String MAX = "max:";

    protected static final String NEW_LINE = System.lineSeparator();

    protected static final String NEW_LINE_REGEX = "\\n";

    public static final Set<Class<?>> VALID_TYPES = Set.of(
        boolean.class,
        Boolean.class,
        int.class,
        Integer.class,
        float.class,
        Float.class,
        String.class
    );

    private ConfigConstants() {
        throw new UnsupportedOperationException();
    }
}
