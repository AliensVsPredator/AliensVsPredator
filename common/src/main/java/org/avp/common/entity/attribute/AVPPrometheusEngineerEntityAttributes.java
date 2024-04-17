package org.avp.common.entity.attribute;

import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Monster;

import org.avp.common.entity.type.AVPEngineerEntityTypes;

public class AVPPrometheusEngineerEntityAttributes {

    private static AttributeSupplier.Builder createAvPAttributes() {
        return Monster.createMonsterAttributes()
            .add(Attributes.ARMOR, 0)
            .add(Attributes.FOLLOW_RANGE, 16)
            .add(Attributes.MAX_HEALTH, 20)
            .add(Attributes.MOVEMENT_SPEED, 1);
    }

    private static final AttributeSupplier ENGINEER = createAvPAttributes()
        .add(Attributes.ATTACK_DAMAGE, 8.0D)
        .add(Attributes.KNOCKBACK_RESISTANCE, 0.85D)
        .add(Attributes.MAX_HEALTH, 160.0D)
        .add(Attributes.MOVEMENT_SPEED, 0.5199999761581421D)
        .build();

    public static void addBindings() {
        AVPEntityAttributesBindingRegistry.addBinding(AVPEngineerEntityTypes.INSTANCE.ENGINEER, ENGINEER);
    }
}
