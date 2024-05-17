package org.avp.common.entity.attribute;

import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;

import org.avp.common.entity.AVPEntitySpeedConstants;
import org.avp.common.entity.type.AVPRunnerAlienEntityTypes;

public class AVPRunnerAlienEntityAttributes {

    private static final AttributeSupplier CHESTBURSTER_RUNNER = AVPEntityAttributesBindingRegistry.builder()
        .add(Attributes.ATTACK_DAMAGE, 0.5)
        .add(Attributes.MAX_HEALTH, 14)
        .add(Attributes.MOVEMENT_SPEED, AVPEntitySpeedConstants.CHESTBURSTER_SPEED)
        .build();

    private static final AttributeSupplier CRUSHER = AVPEntityAttributesBindingRegistry.builder()
        .add(Attributes.ATTACK_DAMAGE, 12)
        .add(Attributes.KNOCKBACK_RESISTANCE, 1)
        .add(Attributes.MAX_HEALTH, 90)
        .add(Attributes.MOVEMENT_SPEED, AVPEntitySpeedConstants.CRUSHER_SPEED)
        .build();

    private static final AttributeSupplier DRONE_RUNNER = AVPEntityAttributesBindingRegistry.builder()
        .add(Attributes.ATTACK_DAMAGE, 7)
        .add(Attributes.MAX_HEALTH, 30)
        .add(Attributes.MOVEMENT_SPEED, AVPEntitySpeedConstants.DRONE_RUNNER_SPEED)
        .build();

    private static final AttributeSupplier WARRIOR_RUNNER = AVPEntityAttributesBindingRegistry.builder()
        .add(Attributes.ATTACK_DAMAGE, 9)
        .add(Attributes.MAX_HEALTH, 40)
        .add(Attributes.MOVEMENT_SPEED, AVPEntitySpeedConstants.WARRIOR_RUNNER_SPEED)
        .build();

    public static void addBindings() {
        AVPEntityAttributesBindingRegistry.addBinding(AVPRunnerAlienEntityTypes.INSTANCE.chestbursterRunner, CHESTBURSTER_RUNNER);
        AVPEntityAttributesBindingRegistry.addBinding(AVPRunnerAlienEntityTypes.INSTANCE.crusher, CRUSHER);
        AVPEntityAttributesBindingRegistry.addBinding(AVPRunnerAlienEntityTypes.INSTANCE.droneRunner, DRONE_RUNNER);
        AVPEntityAttributesBindingRegistry.addBinding(AVPRunnerAlienEntityTypes.INSTANCE.warriorRunner, WARRIOR_RUNNER);
    }

    private AVPRunnerAlienEntityAttributes() {
        throw new UnsupportedOperationException();
    }
}
