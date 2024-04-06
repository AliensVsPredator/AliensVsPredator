package org.avp.common.entity.attribute;

import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;

import org.avp.common.entity.AVPYautjaEntityTypes;

/**
 * @author Boston Vanseghi
 */
public class AVPYautjaEntityAttributes {

    private static final AttributeSupplier YAUTJA = AVPEntityAttributesBindingRegistry.builder()
        .add(Attributes.ATTACK_DAMAGE, 12.0D)
        .add(Attributes.KNOCKBACK_RESISTANCE, 0.75D)
        .add(Attributes.MAX_HEALTH, 80.0D)
        .add(Attributes.MOVEMENT_SPEED, 0.5666666666D)
        .build();

    public static void addBindings() {
        AVPEntityAttributesBindingRegistry.addBinding(AVPYautjaEntityTypes.YAUTJA, YAUTJA);
    }
}
