package org.avp.common.entity.attribute;

import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;

import org.avp.common.entity.AVPEntitySpeedConstants;
import org.avp.common.entity.type.AVPYautjaEntityTypes;

public class AVPYautjaEntityAttributes {

    private static final AttributeSupplier YAUTJA = AVPEntityAttributesBindingRegistry.builder()
        .add(Attributes.ATTACK_DAMAGE, 12)
        .add(Attributes.KNOCKBACK_RESISTANCE, 0.75)
        .add(Attributes.MAX_HEALTH, 80)
        .add(Attributes.MOVEMENT_SPEED, AVPEntitySpeedConstants.YAUTJA_SPEED)
        .build();

    public static void addBindings() {
        AVPEntityAttributesBindingRegistry.addBinding(AVPYautjaEntityTypes.INSTANCE.yautja, YAUTJA);
    }

    private AVPYautjaEntityAttributes() {
        throw new UnsupportedOperationException();
    }
}
