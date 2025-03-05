package com.avp.common.config.template;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

import com.avp.AVP;
import com.avp.common.config.ConfigAlienSpawningContainer;
import com.avp.common.config.ConfigMobAttributesContainer;
import com.avp.common.config.ConfigMobSpawningContainer;
import com.avp.common.config.property.ConfigPropertyKey;
import com.avp.common.config.property.ConfigPropertyRegistry;

public class ConfigTemplateBuilder {

    public static ConfigTemplateBuilder create() {
        return new ConfigTemplateBuilder();
    }

    private final List<Function<ConfigTemplateContext, Optional<String>>> lineGenerators;

    private ConfigTemplateBuilder() {
        this.lineGenerators = new ArrayList<>();
    }

    public ConfigTemplateBuilder comment(String comment) {
        lineGenerators.add(ctx -> Optional.of("# " + comment));
        return this;
    }

    public ConfigTemplateBuilder newLine() {
        lineGenerators.add(ctx -> Optional.of(""));
        return this;
    }

    public ConfigTemplateBuilder properties(ConfigAlienSpawningContainer container) {
        properties(container.mobSpawning());
        property(container.requiresResin());

        return this;
    }

    public ConfigTemplateBuilder properties(ConfigMobSpawningContainer container) {
        property(container.enabled());
        property(container.maxY());
        property(container.minY());
        property(container.minGroupSize());
        property(container.maxGroupSize());
        property(container.weight());

        return this;
    }

    public ConfigTemplateBuilder properties(ConfigMobAttributesContainer container) {
        property(container.armor());
        property(container.armorToughness());
        property(container.attackDamage());
        property(container.followRange());
        property(container.health());
        property(container.healthRegenPerSecond());
        property(container.knockbackResistance());
        property(container.moveSpeed());

        return this;
    }

    public <T> ConfigTemplateBuilder property(ConfigPropertyKey<T> key) {
        var serializer = ConfigPropertyRegistry.instance().getSerializerOrNull(key);

        if (serializer == null) {
            AVP.LOGGER.warn("Could not find serializer while building AVP config. ID: {}", key.identifier());
            return this;
        }

        lineGenerators.add(
            ctx -> ctx.config()
                .properties()
                .get(key)
                .map(value -> {
                    var stringValue = serializer.serialize(value);
                    var id = key.identifier();
                    var padded = padRight(id, ctx.maxPropertyIdLength() - id.length());
                    return padded + " = " + stringValue;
                })
        );

        return this;
    }

    public ConfigTemplate build() {
        return new ConfigTemplate(lineGenerators);
    }

    public static String padRight(String s, int n) {
        return s + " ".repeat(n);
    }
}
