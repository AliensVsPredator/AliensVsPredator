package org.avp.common.entity.attribute;

import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;

import org.avp.common.entity.AVPEntitySpeedConstants;
import org.avp.common.entity.type.AVPEngineerEntityTypes;

public class AVPPrometheusEngineerEntityAttributes {

    private static final AttributeSupplier ENGINEER = AVPEntityAttributesBindingRegistry.builder()
        .add(Attributes.ATTACK_DAMAGE, 8)
        .add(Attributes.KNOCKBACK_RESISTANCE, 0.85)
        .add(Attributes.MAX_HEALTH, 160)
        .add(Attributes.MOVEMENT_SPEED, AVPEntitySpeedConstants.ENGINEER_SPEED)
        .build();

    public static void addBindings() {
        AVPEntityAttributesBindingRegistry.addBinding(AVPEngineerEntityTypes.INSTANCE.engineer, ENGINEER);
    }

    private AVPPrometheusEngineerEntityAttributes() {
        throw new UnsupportedOperationException();
    }
}
