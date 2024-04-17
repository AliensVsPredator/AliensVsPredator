package org.avp.common.entity.attribute;

import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;

import org.avp.common.entity.type.AVPRunnerAlienEntityTypes;

public class AVPRunnerAlienEntityAttributes {

    private static final AttributeSupplier CHESTBURSTER_RUNNER = AVPEntityAttributesBindingRegistry.builder()
        .add(Attributes.ATTACK_DAMAGE, 0.5D)
        .add(Attributes.MAX_HEALTH, 14.0D)
        .add(Attributes.MOVEMENT_SPEED, 0.6499999761581421D)
        .build();

    private static final AttributeSupplier CRUSHER = AVPEntityAttributesBindingRegistry.builder()
        .add(Attributes.ATTACK_DAMAGE, 12.0D)
        .add(Attributes.KNOCKBACK_RESISTANCE, 1F)
        .add(Attributes.MAX_HEALTH, 90.0D)
        .add(Attributes.MOVEMENT_SPEED, 0.5D)
        .build();

    private static final AttributeSupplier DRONE_RUNNER = AVPEntityAttributesBindingRegistry.builder()
        .add(Attributes.ATTACK_DAMAGE, 7.0D)
        .add(Attributes.MAX_HEALTH, 30.0D)
        .add(Attributes.MOVEMENT_SPEED, 0.63D)
        .build();

    private static final AttributeSupplier WARRIOR_RUNNER = AVPEntityAttributesBindingRegistry.builder()
        .add(Attributes.ATTACK_DAMAGE, 9.0D)
        .add(Attributes.MAX_HEALTH, 40.0D)
        .add(Attributes.MOVEMENT_SPEED, 0.6D)
        .build();

    public static void addBindings() {
        AVPEntityAttributesBindingRegistry.addBinding(AVPRunnerAlienEntityTypes.INSTANCE.chestbursterRunner, CHESTBURSTER_RUNNER);
        AVPEntityAttributesBindingRegistry.addBinding(AVPRunnerAlienEntityTypes.INSTANCE.crusher, CRUSHER);
        AVPEntityAttributesBindingRegistry.addBinding(AVPRunnerAlienEntityTypes.INSTANCE.droneRunner, DRONE_RUNNER);
        AVPEntityAttributesBindingRegistry.addBinding(AVPRunnerAlienEntityTypes.INSTANCE.warriorRunner, WARRIOR_RUNNER);
    }
}
