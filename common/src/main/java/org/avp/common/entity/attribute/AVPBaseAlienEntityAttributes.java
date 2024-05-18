package org.avp.common.entity.attribute;

import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;

import org.avp.common.entity.AVPEntitySpeedConstants;
import org.avp.common.entity.type.AVPBaseAlienEntityTypes;

public class AVPBaseAlienEntityAttributes {

    private static final AttributeSupplier BOILER = AVPEntityAttributesBindingRegistry.builder()
        .add(Attributes.ATTACK_DAMAGE, 4)
        .add(Attributes.MAX_HEALTH, 30)
        .add(Attributes.MOVEMENT_SPEED, AVPEntitySpeedConstants.BASE_SPEED)
        .build();

    private static final AttributeSupplier CHESTBURSTER = AVPEntityAttributesBindingRegistry.builder()
        .add(Attributes.ATTACK_DAMAGE, 0.5)
        .add(Attributes.MAX_HEALTH, 14)
        .add(Attributes.MOVEMENT_SPEED, AVPEntitySpeedConstants.CHESTBURSTER_SPEED)
        .build();

    private static final AttributeSupplier DRONE = AVPEntityAttributesBindingRegistry.builder()
        .add(Attributes.ATTACK_DAMAGE, 7)
        .add(Attributes.MAX_HEALTH, 40)
        .add(Attributes.MOVEMENT_SPEED, AVPEntitySpeedConstants.DRONE_SPEED)
        .build();

    private static final AttributeSupplier FACEHUGGER = AVPEntityAttributesBindingRegistry.builder()
        .add(Attributes.MAX_HEALTH, 5)
        .add(Attributes.MOVEMENT_SPEED, AVPEntitySpeedConstants.FACEHUGGER_SPEED)
        .build();

    private static final AttributeSupplier FACEHUGGER_ROYAL = AVPEntityAttributesBindingRegistry.builder()
        .add(Attributes.MAX_HEALTH, 14)
        .add(Attributes.MOVEMENT_SPEED, AVPEntitySpeedConstants.FACEHUGGER_ROYAL_SPEED)
        .build();

    private static final AttributeSupplier NAUTICOMORPH = AVPEntityAttributesBindingRegistry.builder()
        .add(Attributes.ATTACK_DAMAGE, 7)
        .add(Attributes.MAX_HEALTH, 40)
        .add(Attributes.MOVEMENT_SPEED, AVPEntitySpeedConstants.DRONE_SPEED) // TODO:
        .build();

    private static final AttributeSupplier OVAMORPH = AVPEntityAttributesBindingRegistry.builder()
        .add(Attributes.KNOCKBACK_RESISTANCE, 1)
        .add(Attributes.MAX_HEALTH, 8)
        .add(Attributes.MOVEMENT_SPEED, 0)
        .build();

    private static final AttributeSupplier PRAETORIAN = AVPEntityAttributesBindingRegistry.builder()
        .add(Attributes.ATTACK_DAMAGE, 12)
        .add(Attributes.KNOCKBACK_RESISTANCE, 1)
        .add(Attributes.MAX_HEALTH, 100)
        .add(Attributes.MOVEMENT_SPEED, AVPEntitySpeedConstants.PRAETORIAN_SPEED)
        .build();

    private static final AttributeSupplier QUEEN = AVPEntityAttributesBindingRegistry.builder()
        .add(Attributes.ATTACK_DAMAGE, 24)
        .add(Attributes.KNOCKBACK_RESISTANCE, 1)
        .add(Attributes.MAX_HEALTH, 300)
        .add(Attributes.MOVEMENT_SPEED, AVPEntitySpeedConstants.QUEEN_SPEED)
        .build();

    private static final AttributeSupplier SPITTER = AVPEntityAttributesBindingRegistry.builder()
        .add(Attributes.ATTACK_DAMAGE, 4)
        .add(Attributes.MAX_HEALTH, 30)
        .add(Attributes.MOVEMENT_SPEED, AVPEntitySpeedConstants.BASE_SPEED)
        .build();

    private static final AttributeSupplier WARRIOR = AVPEntityAttributesBindingRegistry.builder()
        .add(Attributes.ATTACK_DAMAGE, 9)
        .add(Attributes.MAX_HEALTH, 50)
        .add(Attributes.MOVEMENT_SPEED, AVPEntitySpeedConstants.WARRIOR_SPEED)
        .build();

    public static void addBindings() {
        AVPEntityAttributesBindingRegistry.addBinding(AVPBaseAlienEntityTypes.INSTANCE.boiler, BOILER);
        AVPEntityAttributesBindingRegistry.addBinding(AVPBaseAlienEntityTypes.INSTANCE.chestburster, CHESTBURSTER);
        AVPEntityAttributesBindingRegistry.addBinding(AVPBaseAlienEntityTypes.INSTANCE.drone, DRONE);
        AVPEntityAttributesBindingRegistry.addBinding(AVPBaseAlienEntityTypes.INSTANCE.facehugger, FACEHUGGER);
        AVPEntityAttributesBindingRegistry.addBinding(AVPBaseAlienEntityTypes.INSTANCE.facehuggerRoyal, FACEHUGGER_ROYAL);
        AVPEntityAttributesBindingRegistry.addBinding(AVPBaseAlienEntityTypes.INSTANCE.nauticomorph, NAUTICOMORPH);
        AVPEntityAttributesBindingRegistry.addBinding(AVPBaseAlienEntityTypes.INSTANCE.ovamorph, OVAMORPH);
        AVPEntityAttributesBindingRegistry.addBinding(AVPBaseAlienEntityTypes.INSTANCE.praetorian, PRAETORIAN);
        AVPEntityAttributesBindingRegistry.addBinding(AVPBaseAlienEntityTypes.INSTANCE.queen, QUEEN);
        AVPEntityAttributesBindingRegistry.addBinding(AVPBaseAlienEntityTypes.INSTANCE.spitter, SPITTER);
        AVPEntityAttributesBindingRegistry.addBinding(AVPBaseAlienEntityTypes.INSTANCE.warrior, WARRIOR);
    }

    private AVPBaseAlienEntityAttributes() {
        throw new UnsupportedOperationException();
    }
}
