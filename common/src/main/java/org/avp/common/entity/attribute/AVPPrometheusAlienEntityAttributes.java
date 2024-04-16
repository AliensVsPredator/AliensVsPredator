package org.avp.common.entity.attribute;

import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Monster;

import org.avp.common.entity.type.AVPPrometheusAlienEntityTypes;

public class AVPPrometheusAlienEntityAttributes {

    private static AttributeSupplier.Builder createAvPAttributes() {
        return Monster.createMonsterAttributes()
            .add(Attributes.ARMOR, 0)
            .add(Attributes.FOLLOW_RANGE, 16)
            .add(Attributes.MAX_HEALTH, 20)
            .add(Attributes.MOVEMENT_SPEED, 1);
    }

    private static final AttributeSupplier DEACON_ADULT = createAvPAttributes()
        .add(Attributes.ATTACK_DAMAGE, 3D)
        .add(Attributes.MAX_HEALTH, 120.0D)
        .add(Attributes.MOVEMENT_SPEED, 0.4700000238418579D)
        .build();

    private static final AttributeSupplier TRILOBITE = createAvPAttributes()
        .add(Attributes.ATTACK_DAMAGE, 4.0D)
        .add(Attributes.KNOCKBACK_RESISTANCE, 1F)
        .add(Attributes.MAX_HEALTH, 44.0D)
        .add(Attributes.MOVEMENT_SPEED, 0.6999999761581421D)
        .build();

    private static final AttributeSupplier TRILOBITE_BABY = createAvPAttributes()
        .add(Attributes.ATTACK_DAMAGE, 0.5D)
        .add(Attributes.MAX_HEALTH, 16.0D)
        .add(Attributes.MOVEMENT_SPEED, 0.6499999761581421D)
        .build();

    public static void addBindings() {
        AVPEntityAttributesBindingRegistry.addBinding(AVPPrometheusAlienEntityTypes.DEACON, DEACON_ADULT); // TODO:
        AVPEntityAttributesBindingRegistry.addBinding(AVPPrometheusAlienEntityTypes.DEACON_ADULT, DEACON_ADULT);
        AVPEntityAttributesBindingRegistry.addBinding(AVPPrometheusAlienEntityTypes.TRILOBITE, TRILOBITE);
        AVPEntityAttributesBindingRegistry.addBinding(AVPPrometheusAlienEntityTypes.TRILOBITE_BABY, TRILOBITE_BABY);
    }
}
