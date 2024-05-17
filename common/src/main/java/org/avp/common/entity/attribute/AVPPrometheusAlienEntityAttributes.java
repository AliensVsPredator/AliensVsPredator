package org.avp.common.entity.attribute;

import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;

import org.avp.common.entity.AVPEntitySpeedConstants;
import org.avp.common.entity.type.AVPPrometheusAlienEntityTypes;

public class AVPPrometheusAlienEntityAttributes {

    private static final AttributeSupplier DEACON_ADULT = AVPEntityAttributesBindingRegistry.builder()
        .add(Attributes.ATTACK_DAMAGE, 3)
        .add(Attributes.MAX_HEALTH, 120)
        .add(Attributes.MOVEMENT_SPEED, AVPEntitySpeedConstants.DEACON_ADULT_SPEED)
        .build();

    private static final AttributeSupplier TRILOBITE = AVPEntityAttributesBindingRegistry.builder()
        .add(Attributes.ATTACK_DAMAGE, 4)
        .add(Attributes.KNOCKBACK_RESISTANCE, 1)
        .add(Attributes.MAX_HEALTH, 44)
        .add(Attributes.MOVEMENT_SPEED, AVPEntitySpeedConstants.TRILOBITE_SPEED)
        .build();

    private static final AttributeSupplier TRILOBITE_BABY = AVPEntityAttributesBindingRegistry.builder()
        .add(Attributes.ATTACK_DAMAGE, 0.5)
        .add(Attributes.MAX_HEALTH, 16)
        .add(Attributes.MOVEMENT_SPEED, AVPEntitySpeedConstants.TRILOBITE_BABY_SPEED)
        .build();

    public static void addBindings() {
        AVPEntityAttributesBindingRegistry.addBinding(AVPPrometheusAlienEntityTypes.INSTANCE.deacon, DEACON_ADULT); // TODO:
        AVPEntityAttributesBindingRegistry.addBinding(AVPPrometheusAlienEntityTypes.INSTANCE.deaconAdult, DEACON_ADULT);
        AVPEntityAttributesBindingRegistry.addBinding(AVPPrometheusAlienEntityTypes.INSTANCE.trilobite, TRILOBITE);
        AVPEntityAttributesBindingRegistry.addBinding(AVPPrometheusAlienEntityTypes.INSTANCE.trilobiteBaby, TRILOBITE_BABY);
    }

    private AVPPrometheusAlienEntityAttributes() {
        throw new UnsupportedOperationException();
    }
}
