package org.avp.common.entity.attribute;

import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;

import org.avp.common.entity.AVPEntitySpeedConstants;
import org.avp.common.entity.type.AVPEntityTypes;

public class AVPEntityAttributes {

    private static final AttributeSupplier BELUGABURSTER = AVPEntityAttributesBindingRegistry.builder()
        .add(Attributes.ATTACK_DAMAGE, 0.5)
        .add(Attributes.MAX_HEALTH, 14)
        .add(Attributes.MOVEMENT_SPEED, AVPEntitySpeedConstants.CHESTBURSTER_SPEED)
        .build();

    private static final AttributeSupplier BELUGAMORPH = AVPEntityAttributesBindingRegistry.builder()
        .add(Attributes.ATTACK_DAMAGE, 4.5)
        .add(Attributes.MAX_HEALTH, 100)
        .add(Attributes.MOVEMENT_SPEED, AVPEntitySpeedConstants.BELUGAMORPH_SPEED)
        .build();

    public static void addBindings() {
        // Unclassified
        AVPEntityAttributesBindingRegistry.addBinding(AVPEntityTypes.INSTANCE.belugaburster, BELUGABURSTER);
        AVPEntityAttributesBindingRegistry.addBinding(AVPEntityTypes.INSTANCE.belugamorph, BELUGAMORPH);
    }

    private AVPEntityAttributes() {
        throw new UnsupportedOperationException();
    }
}
