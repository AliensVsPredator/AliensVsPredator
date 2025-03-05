package com.avp.common.config.io;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

import com.avp.AVP;
import com.avp.common.config.Config;
import com.avp.common.config.property.ConfigPropertyContainer;
import com.avp.common.config.property.ConfigPropertyKey;
import com.avp.common.config.property.ConfigPropertyRegistry;

public class ConfigLoader {

    public static Optional<Config> load(String name) {
        var configPath = ConfigIOUtil.resolveConfigPath(name);

        if (!Files.exists(configPath)) {
            return Optional.empty();
        }

        var lines = readLines(configPath);
        var properties = parsePropertiesFromConfigLines(lines);

        // TODO: Provide line callbacks here.
        return Optional.of(Config.of(name, properties));
    }

    private static ConfigPropertyContainer parsePropertiesFromConfigLines(List<String> lines) {
        var propertyMap = new HashMap<ConfigPropertyKey<?>, Object>();

        for (var line : lines) {
            var cleanedLine = line.trim();

            if (cleanedLine.isEmpty() || cleanedLine.isBlank() || !Character.isLetter(cleanedLine.charAt(0))) {
                continue;
            }

            var parts = cleanedLine.split("=");

            if (parts.length != 2) {
                continue;
            }

            var id = parts[0].trim().toLowerCase();
            var value = parts[1].trim();

            var propertyKey = ConfigPropertyRegistry.instance().getKeyOrNull(id);

            if (propertyKey != null) {
                var deserializer = ConfigPropertyRegistry.instance().getDeserializerOrNull(propertyKey);

                if (deserializer != null) {
                    propertyMap.put(propertyKey, deserializer.deserialize(value));
                } else {
                    AVP.LOGGER.warn("No deserializer found for config property. ID: '{}'.", id);
                }
            } else {
                AVP.LOGGER.warn("Unrecognized config property found. ID: {}", id);
            }

        }

        return new ConfigPropertyContainer(propertyMap);
    }

    private static List<String> readLines(Path configPath) {
        try {
            return Files.readAllLines(configPath);
        } catch (IOException e) {
            return List.of();
        }
    }
}
