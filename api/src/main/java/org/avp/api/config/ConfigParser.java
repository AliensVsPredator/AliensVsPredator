package org.avp.api.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

import static org.avp.api.config.ConfigConstants.CATEGORY_END;
import static org.avp.api.config.ConfigConstants.CATEGORY_START;
import static org.avp.api.config.ConfigConstants.COMMENT;
import static org.avp.api.config.ConfigConstants.COMMENT_REGEX;
import static org.avp.api.config.ConfigConstants.DEFAULT;
import static org.avp.api.config.ConfigConstants.EQUALS;
import static org.avp.api.config.ConfigConstants.INTERNAL_COMMENT;
import static org.avp.api.config.ConfigConstants.INTERNAL_COMMENT_REGEX;
import static org.avp.api.config.ConfigConstants.INTERNAL_COMMENT_SEPARATOR_REGEX;
import static org.avp.api.config.ConfigConstants.MAX;
import static org.avp.api.config.ConfigConstants.MIN;
import static org.avp.api.config.ConfigConstants.NEW_LINE;

/**
 * Simple config handler for properties files with support for categories, comments, defaults and numerical range
 * limits.
 *
 * @author Boston Vanseghi
 * @author bright_spark
 */
public class ConfigParser {

    private static final Logger LOGGER = LoggerFactory.getLogger(ConfigParser.class);

    /**
     * Reads the config from the file at the path to the configClass.
     *
     * @param path        The location of the config file
     * @param configClass The config class
     */
    public static void read(Path path, Class<?> configClass) throws IOException, IllegalAccessException {
        if (Files.notExists(path)) {
            return;
        }

        var entries = readEntries(path);

        var configFields = ConfigField.getAllFromType(configClass);
        for (var configField : configFields) {
            var field = configField.field();
            var name = configField.getName();

            var entryKey = new ConfigEntryKey(name, configField.getCategoryName());
            var entry = entries.get(entryKey);
            if (entry != null) {
                var type = field.getType();
                var newValue = ConfigReadUtilities.parseValueToType(entry.value(), type);

                var internalComment = entry.internalComment();
                if (!internalComment.isBlank()) {
                    String defaultValue = null;
                    String min = null;
                    String max = null;
                    var parts = internalComment.split(INTERNAL_COMMENT_SEPARATOR_REGEX);
                    for (var part : parts) {
                        var colonIndex = part.indexOf(":");
                        var partValue = part.substring(colonIndex + 1).trim();
                        if (part.startsWith(DEFAULT)) {
                            defaultValue = partValue;
                        } else if (part.startsWith(MIN)) {
                            min = partValue;
                        } else if (part.startsWith(MAX)) {
                            max = partValue;
                        } else {
                            LOGGER.warn("Invalid part of internal comment: {}", part);
                        }
                    }

                    if (newValue == null && defaultValue != null) {
                        newValue = ConfigReadUtilities.parseValueToType(defaultValue, type);
                    }
                    if (ConfigReadUtilities.isLessThan(min, newValue)) {
                        throw new IllegalStateException("Config '" + name + " value '" + newValue + "' is less than the minimum of " + min);
                    }
                    if (ConfigReadUtilities.isGreaterThan(max, newValue)) {
                        throw new IllegalStateException(
                            "Config '" + name + " value '" + newValue + "' is greater than the maximum of " + max
                        );
                    }
                }

                field.set(null, newValue);
            } else {
                LOGGER.warn("Config '{}' has no value in file!", name);
            }
        }
    }

    private static Map<ConfigEntryKey, ConfigEntry> readEntries(Path path) throws IOException {
        var internalComment = "";
        var comments = new ArrayList<String>();
        var entries = new HashMap<ConfigEntryKey, ConfigEntry>();
        String category = null;

        for (var line : Files.readAllLines(path)) {
            // Ignore blank links and category borders
            if (line.isBlank() || line.startsWith("##")) {
                continue;
            }

            if (line.startsWith(CATEGORY_START) && line.endsWith(CATEGORY_END)) {
                // Category line
                category = line.substring(2, line.length() - 2).trim();
            } else if (line.startsWith(INTERNAL_COMMENT)) {
                // Internal comment line - should be max 1 per config
                internalComment = line.replaceFirst(INTERNAL_COMMENT_REGEX, "");
            } else if (line.startsWith(COMMENT)) {
                // Comment line
                comments.add(line.replaceFirst(COMMENT_REGEX, ""));
            } else {
                var parts = line.split(EQUALS, 2);
                if (parts.length != 2) {
                    LOGGER.warn("Invalid config line -> '{}'", line);
                    continue;
                }

                // Config line
                var comment = comments.isEmpty() ? "" : String.join(NEW_LINE, comments);
                var name = parts[0].trim();
                var value = parts[1].trim();
                var entryKey = new ConfigEntryKey(name, category);
                var entry = new ConfigEntry(name, value, category, comment, internalComment);
                entries.put(entryKey, entry);
            }
        }

        return entries;
    }

    /**
     * Writes the config to the file at the path from the configClass.
     *
     * @param path        The location of the config file
     * @param configClass The config class
     */
    public static void write(Path path, Class<?> configClass) throws IOException, IllegalAccessException {
        var stringBuilder = new StringBuilder();
        ConfigCategory lastConfigCategory = null;
        var configFields = ConfigField.getAllFromType(configClass);

        for (var configField : configFields) {
            if (configField.category() != lastConfigCategory) {
                lastConfigCategory = configField.category();
                stringBuilder.append(lastConfigCategory.getCategory());
            }

            var comment = configField.getComment();
            if (comment != null) {
                stringBuilder.append(comment);
            }

            var internalComment = configField.getInternalComment();
            if (internalComment != null) {
                stringBuilder.append(internalComment);
            }

            stringBuilder.append(configField.getConfig());
        }

        Files.writeString(path, stringBuilder.toString());
    }

    private ConfigParser() {
        throw new UnsupportedOperationException();
    }
}
