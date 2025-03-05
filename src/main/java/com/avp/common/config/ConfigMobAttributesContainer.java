package com.avp.common.config;

import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;

import com.avp.common.config.property.ConfigPropertyKey;

public record ConfigMobAttributesContainer(
    ConfigPropertyKey<Double> armor,
    ConfigPropertyKey<Double> armorToughness,
    ConfigPropertyKey<Double> attackDamage,
    ConfigPropertyKey<Double> followRange,
    ConfigPropertyKey<Double> health,
    ConfigPropertyKey<Float> healthRegenPerSecond,
    ConfigPropertyKey<Double> knockbackResistance,
    ConfigPropertyKey<Double> moveSpeed

) {

    public AttributeSupplier.Builder applyFrom(Config config, AttributeSupplier.Builder builder) {
        var properties = config.properties();

        properties.get(armor).ifPresent(value -> builder.add(Attributes.ARMOR, value));
        properties.get(armorToughness).ifPresent(value -> builder.add(Attributes.ARMOR_TOUGHNESS, value));
        properties.get(attackDamage).ifPresent(value -> builder.add(Attributes.ATTACK_DAMAGE, value));
        properties.get(followRange).ifPresent(value -> builder.add(Attributes.FOLLOW_RANGE, value));
        properties.get(knockbackResistance).ifPresent(value -> builder.add(Attributes.KNOCKBACK_RESISTANCE, value));
        properties.get(health).ifPresent(value -> builder.add(Attributes.MAX_HEALTH, value));
        properties.get(moveSpeed).ifPresent(value -> builder.add(Attributes.MOVEMENT_SPEED, value));

        return builder;
    }
}
