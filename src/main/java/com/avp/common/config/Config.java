package com.avp.common.config;

import com.avp.common.config.property.ConfigPropertyContainer;

public class Config {

    public static Config empty(String name) {
        return new Config(name, new ConfigPropertyContainer());
    }

    public static Config of(String name, ConfigPropertyContainer properties) {
        return new Config(name, properties);
    }

    private final String name;

    private final ConfigPropertyContainer properties;

    private Config(String name, ConfigPropertyContainer properties) {
        this.name = name;
        this.properties = properties;
    }

    public Config merge(Config other) {
        var mergedPropertiesContainer = properties.merge(other.properties);
        return Config.of(other.name, mergedPropertiesContainer);
    }

    public String name() {
        return name;
    }

    public ConfigPropertyContainer properties() {
        return properties;
    }
}
