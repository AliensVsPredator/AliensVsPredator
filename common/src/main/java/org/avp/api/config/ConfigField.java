package org.avp.api.config;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.avp.common.AVPConstants;

import static org.avp.api.config.ConfigConstants.COMMENT;
import static org.avp.api.config.ConfigConstants.DEFAULT;
import static org.avp.api.config.ConfigConstants.EQUALS;
import static org.avp.api.config.ConfigConstants.INTERNAL_COMMENT;
import static org.avp.api.config.ConfigConstants.INTERNAL_COMMENT_SEPARATOR;
import static org.avp.api.config.ConfigConstants.MAX;
import static org.avp.api.config.ConfigConstants.MIN;
import static org.avp.api.config.ConfigConstants.NEW_LINE;
import static org.avp.api.config.ConfigConstants.NEW_LINE_REGEX;
import static org.avp.api.config.ConfigConstants.VALID_TYPES;

public record ConfigField(
    Field field,
    @NotNull Config config,
    @Nullable ConfigCategory category
) {

    String getName() {
        return !config.value().isBlank() ? config().value() : field.getName();
    }

    @Nullable
    String getCategoryName() {
        return category != null ? category.getName() : null;
    }

    StringBuilder getComment() {
        if (config.comment().isBlank()) {
            return null;
        }

        var commentLines = config.comment().split(NEW_LINE_REGEX);
        var stringBuilder = new StringBuilder();

        for (var commentLine : commentLines) {
            stringBuilder
                .append(COMMENT)
                .append(" ")
                .append(commentLine)
                .append(NEW_LINE);
        }

        return stringBuilder;
    }

    StringBuilder getInternalComment() throws IllegalAccessException {
        // Since primitives can't be null, we'll assume any value for them is a default value
        var hasDefault = !field.getType().isPrimitive() && field.get(null) == null;
        var hasMin = config.min() != Float.MIN_NORMAL;
        var hasMax = config.max() != Float.MAX_VALUE;

        if (!hasDefault && !hasMin && !hasMax) {
            return null;
        }

        var stringBuilder = new StringBuilder();
        stringBuilder.append(INTERNAL_COMMENT).append(" ");
        var internalCommentParts = new ArrayList<String>();

        if (hasDefault) {
            internalCommentParts.add(DEFAULT + " " + field.get(null));
        }
        if (hasMin) {
            internalCommentParts.add(MIN + " " + config.min());
        }
        if (hasMax) {
            internalCommentParts.add(MAX + " " + config.max());
        }

        stringBuilder
            .append(String.join(INTERNAL_COMMENT_SEPARATOR, internalCommentParts))
            .append(NEW_LINE);
        return stringBuilder;
    }

    StringBuilder getConfig() throws IllegalAccessException {
        var name = getName();
        var rawValue = field.get(null);
        var value = rawValue == null ? "" : rawValue.toString();
        var stringBuilder = new StringBuilder();

        stringBuilder
            .append(name)
            .append(" ")
            .append(EQUALS)
            .append(" ")
            .append(value)
            .append(NEW_LINE)
            .append(NEW_LINE);

        return stringBuilder;
    }

    public static Optional<ConfigField> create(@Nullable ConfigCategory configCategory, Field field) {
        int modifiers = field.getModifiers();

        if (!Modifier.isPublic(modifiers))
            return Optional.empty();
        if (!Modifier.isStatic(modifiers))
            return Optional.empty();

        var type = field.getType();

        if (!VALID_TYPES.contains(type)) {
            AVPConstants.LOGGER.warn("The config '{}' value type {} is not supported!", field.getName(), type.getName());
            return Optional.empty();
        }

        var annotation = field.getAnnotation(Config.class);

        return Optional.ofNullable(annotation)
            .map(config -> new ConfigField(field, config, configCategory));
    }

    public static List<ConfigField> getAllFromType(Class<?> configClass) {
        AVPConstants.LOGGER.debug("Getting config fields from class {}", configClass.getName());
        var configs = new ArrayList<ConfigField>();

        // Uncategorized configs
        addConfigFieldsFromClass(null, configClass, configs);

        // Categorized configs
        for (var innerClass : configClass.getDeclaredClasses()) {
            // Skip private classes
            if (Modifier.isPrivate(innerClass.getModifiers()))
                continue;

            var category = innerClass.getAnnotation(Category.class);

            if (category == null)
                continue;

            var configCategory = new ConfigCategory(innerClass, category);
            addConfigFieldsFromClass(configCategory, innerClass, configs);
        }

        AVPConstants.LOGGER.debug("Got {} configs", configs.size());
        return configs;
    }

    private static void addConfigFieldsFromClass(@Nullable ConfigCategory category, Class<?> configClass, List<ConfigField> configs) {
        for (var field : configClass.getDeclaredFields()) {
            var config = ConfigField.create(category, field);
            config.ifPresent(configs::add);
        }
    }
}
