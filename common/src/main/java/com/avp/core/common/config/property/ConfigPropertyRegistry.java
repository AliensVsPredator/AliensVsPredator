package com.avp.core.common.config.property;

import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

import com.avp.core.common.config.property.deserializer.ConfigPropertyDeserializer;
import com.avp.core.common.config.property.serializer.ConfigPropertySerializer;

public class ConfigPropertyRegistry {

    private static final ConfigPropertyRegistry INSTANCE = new ConfigPropertyRegistry();

    public static ConfigPropertyRegistry instance() {
        return INSTANCE;
    }

    private final Map<ConfigPropertyKey<?>, ConfigPropertyDeserializer<?>> propertyKeyToDeserializerMap;

    private final Map<ConfigPropertyKey<?>, ConfigPropertySerializer<?>> propertyKeyToSerializerMap;

    private final Map<String, ConfigPropertyKey<?>> rawPropertyStringToPropertyKeyMap;

    private ConfigPropertyRegistry() {
        this.propertyKeyToDeserializerMap = new HashMap<>();
        this.propertyKeyToSerializerMap = new HashMap<>();
        this.rawPropertyStringToPropertyKeyMap = new HashMap<>();
    }

    public <T> ConfigPropertyKey<T> register(
        ConfigPropertyKey<T> key,
        ConfigPropertyDeserializer<T> deserializer,
        ConfigPropertySerializer<T> serializer
    ) {
        rawPropertyStringToPropertyKeyMap.put(key.identifier(), key);
        propertyKeyToDeserializerMap.put(key, deserializer);
        propertyKeyToSerializerMap.put(key, serializer);

        return key;
    }

    @SuppressWarnings("unchecked")
    public <T> @Nullable ConfigPropertyDeserializer<T> getDeserializerOrNull(ConfigPropertyKey<T> key) {
        return (ConfigPropertyDeserializer<T>) propertyKeyToDeserializerMap.get(key);
    }

    @SuppressWarnings("unchecked")
    public <T> @Nullable ConfigPropertyKey<T> getKeyOrNull(String id) {
        return (ConfigPropertyKey<T>) rawPropertyStringToPropertyKeyMap.get(id);
    }

    @SuppressWarnings("unchecked")
    public <T> @Nullable ConfigPropertySerializer<T> getSerializerOrNull(ConfigPropertyKey<T> key) {
        return (ConfigPropertySerializer<T>) propertyKeyToSerializerMap.get(key);
    }
}
