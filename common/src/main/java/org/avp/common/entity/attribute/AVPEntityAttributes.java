package org.avp.common.entity.attribute;

import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;

import org.avp.common.entity.AVPEntityTypes;

public class AVPEntityAttributes {

    private static final AttributeSupplier BELUGABURSTER = AVPEntityAttributesBindingRegistry.builder()
        .add(Attributes.ATTACK_DAMAGE, 0.5D)
        .add(Attributes.MAX_HEALTH, 14.0D)
        .add(Attributes.MOVEMENT_SPEED, 0.6499999761581421D)
        .build();

    private static final AttributeSupplier BELUGAMORPH = AVPEntityAttributesBindingRegistry.builder()
        .add(Attributes.ATTACK_DAMAGE, 4.5D)
        .add(Attributes.MAX_HEALTH, 100.0D)
        .add(Attributes.MOVEMENT_SPEED, 0.4700000238418579D)
        .build();

    public static void addBindings() {
        // Unclassified
        AVPEntityAttributesBindingRegistry.addBinding(AVPEntityTypes.BELUGABURSTER, BELUGABURSTER);
        AVPEntityAttributesBindingRegistry.addBinding(AVPEntityTypes.BELUGAMORPH, BELUGAMORPH);
    }
}
