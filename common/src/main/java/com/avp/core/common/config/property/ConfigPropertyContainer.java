package com.avp.core.common.config.property;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

public class ConfigPropertyContainer {

    private final Map<ConfigPropertyKey<?>, Object> propertiesToValues;

    public ConfigPropertyContainer() {
        this(new HashMap<>());
    }

    public ConfigPropertyContainer(Map<ConfigPropertyKey<?>, Object> propertyMap) {
        this.propertiesToValues = propertyMap;
    }

    public <T> Optional<T> get(ConfigPropertyKey<T> propertyKey) {
        return Optional.ofNullable(getOrNull(propertyKey));
    }

    public <T> @NotNull T getOrDefault(ConfigPropertyKey<T> propertyKey, T defaultValue) {
        var mapValue = getOrNull(propertyKey);
        return mapValue == null ? defaultValue : mapValue;
    }

    public <T> @Nullable T getOrNull(ConfigPropertyKey<T> propertyKey) {
        return propertyKey.cast(propertiesToValues.get(propertyKey));
    }

    public <T> @NotNull T getOrThrow(ConfigPropertyKey<T> propertyKey) {
        var mapValue = getOrNull(propertyKey);

        if (mapValue == null) {
            throw new RuntimeException("Property not found: " + propertyKey.identifier());
        }

        return mapValue;
    }

    public <T> void put(ConfigPropertyKey<T> propertyKey, T value) {
        propertiesToValues.put(propertyKey, value);
    }

    public ConfigPropertyContainer merge(ConfigPropertyContainer properties) {
        var propertyMap = new HashMap<ConfigPropertyKey<?>, Object>();
        propertyMap.putAll(propertiesToValues);
        propertyMap.putAll(properties.propertiesToValues);
        return new ConfigPropertyContainer(propertyMap);
    }

    public Set<ConfigPropertyKey<?>> keys() {
        return propertiesToValues.keySet();
    }
}
