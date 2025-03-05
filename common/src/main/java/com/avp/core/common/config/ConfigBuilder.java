package com.avp.core.common.config;

import java.util.HashMap;
import java.util.Map;

import com.avp.core.AVP;
import com.avp.core.common.config.property.ConfigPropertyContainer;
import com.avp.core.common.config.property.ConfigPropertyKey;
import com.avp.core.common.config.property.ConfigPropertyRegistry;

public class ConfigBuilder {

    public static ConfigBuilder create(String name) {
        return new ConfigBuilder(name);
    }

    private final String name;

    private final Map<ConfigPropertyKey<?>, Object> propertyMap;

    private ConfigBuilder(String name) {
        this.name = name;
        this.propertyMap = new HashMap<>();
    }

    public <T> ConfigBuilder property(ConfigPropertyKey<T> key, T value) {
        var serializer = ConfigPropertyRegistry.instance().getSerializerOrNull(key);

        if (serializer == null) {
            AVP.LOGGER.warn("Could not find serializer while building AVP config. ID: {}", key.identifier());
            return this;
        }

        propertyMap.put(key, value);

        return this;
    }

    public Config build() {
        return Config.of(name, new ConfigPropertyContainer(propertyMap));
    }
}
