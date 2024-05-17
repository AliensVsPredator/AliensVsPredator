package org.avp.common.entity.attribute;

import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;

import org.avp.common.entity.AVPEntitySpeedConstants;
import org.avp.common.entity.type.AVPExoticAlienEntityTypes;

public class AVPExoticAlienEntityAttributes {

    private static final AttributeSupplier DEACON_ADULT_ENGINEER = AVPEntityAttributesBindingRegistry.builder()
        .add(Attributes.ATTACK_DAMAGE, 4)
        .add(Attributes.MAX_HEALTH, 150)
        .add(Attributes.MOVEMENT_SPEED, AVPEntitySpeedConstants.DEACON_ADULT_ENGINEER_SPEED)
        .build();

    private static final AttributeSupplier DRACOBURSTER = AVPEntityAttributesBindingRegistry.builder()
        .add(Attributes.ATTACK_DAMAGE, 0.5)
        .add(Attributes.MAX_HEALTH, 25)
        .add(Attributes.MOVEMENT_SPEED, AVPEntitySpeedConstants.CHESTBURSTER_SPEED)
        .build();

    private static final AttributeSupplier DRACOMORPH = AVPEntityAttributesBindingRegistry.builder()
        .add(Attributes.ATTACK_DAMAGE, 8)
        .add(Attributes.KNOCKBACK_RESISTANCE, 0.75)
        .add(Attributes.MAX_HEALTH, 400)
        .add(Attributes.MOVEMENT_SPEED, AVPEntitySpeedConstants.DRACOMORPH_SPEED)
        .build();

    private static final AttributeSupplier OCTOHUGGER = AVPEntityAttributesBindingRegistry.builder()
        .add(Attributes.ATTACK_DAMAGE, 0.5)
        .add(Attributes.MAX_HEALTH, 14.0)
        .add(Attributes.MOVEMENT_SPEED, AVPEntitySpeedConstants.OCTOHUGGER_SPEED)
        .build();

    private static final AttributeSupplier OVAMORPH_DRACO = AVPEntityAttributesBindingRegistry.builder()
        .add(Attributes.KNOCKBACK_RESISTANCE, 1)
        .add(Attributes.MAX_HEALTH, 16)
        .add(Attributes.MOVEMENT_SPEED, 0)
        .build();

    private static final AttributeSupplier ULTRAMORPH = AVPEntityAttributesBindingRegistry.builder()
        .add(Attributes.ATTACK_DAMAGE, 5.5)
        .add(Attributes.MAX_HEALTH, 230)
        .add(Attributes.MOVEMENT_SPEED, AVPEntitySpeedConstants.ULTRAMORPH_SPEED)
        .build();

    public static void addBindings() {
        AVPEntityAttributesBindingRegistry.addBinding(AVPExoticAlienEntityTypes.INSTANCE.deaconAdultEngineer, DEACON_ADULT_ENGINEER);
        AVPEntityAttributesBindingRegistry.addBinding(AVPExoticAlienEntityTypes.INSTANCE.chestbursterDraco, DRACOBURSTER);
        AVPEntityAttributesBindingRegistry.addBinding(AVPExoticAlienEntityTypes.INSTANCE.dracomorph, DRACOMORPH);
        AVPEntityAttributesBindingRegistry.addBinding(AVPExoticAlienEntityTypes.INSTANCE.octohugger, OCTOHUGGER);
        AVPEntityAttributesBindingRegistry.addBinding(AVPExoticAlienEntityTypes.INSTANCE.ovamorphDraco, OVAMORPH_DRACO);
        AVPEntityAttributesBindingRegistry.addBinding(AVPExoticAlienEntityTypes.INSTANCE.ultramorph, ULTRAMORPH);
    }

    private AVPExoticAlienEntityAttributes() {
        throw new UnsupportedOperationException();
    }
}
