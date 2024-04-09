package org.avp.common.entity.attribute;

import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;

import org.avp.common.entity.AVPExoticAlienEntityTypes;

public class AVPExoticAlienEntityAttributes {

    private static final AttributeSupplier DEACON_ADULT_ENGINEER = AVPEntityAttributesBindingRegistry.builder()
        .add(Attributes.ATTACK_DAMAGE, 4D)
        .add(Attributes.MAX_HEALTH, 150.0D)
        .add(Attributes.MOVEMENT_SPEED, 0.4700000238418579D)
        .build();

    private static final AttributeSupplier DRACOBURSTER = AVPEntityAttributesBindingRegistry.builder()
        .add(Attributes.ATTACK_DAMAGE, 0.5D)
        .add(Attributes.MAX_HEALTH, 25.0D)
        .add(Attributes.MOVEMENT_SPEED, 0.6499999761581421D)
        .build();

    private static final AttributeSupplier DRACOMORPH = AVPEntityAttributesBindingRegistry.builder()
        .add(Attributes.ATTACK_DAMAGE, 8.0D)
        .add(Attributes.KNOCKBACK_RESISTANCE, 0.75D)
        .add(Attributes.MAX_HEALTH, 400.0D)
        .add(Attributes.MOVEMENT_SPEED, 0.45199999761581421D)
        .build();

    private static final AttributeSupplier OCTOHUGGER = AVPEntityAttributesBindingRegistry.builder()
        .add(Attributes.ATTACK_DAMAGE, 0.50D)
        .add(Attributes.MAX_HEALTH, 14.0D)
        .add(Attributes.MOVEMENT_SPEED, 0.55D)
        .build();

    private static final AttributeSupplier OVAMORPH_DRACO = AVPEntityAttributesBindingRegistry.builder()
        .add(Attributes.KNOCKBACK_RESISTANCE, 1.0D)
        .add(Attributes.MAX_HEALTH, 16.0D)
        .add(Attributes.MOVEMENT_SPEED, 0.0D)
        .build();

    private static final AttributeSupplier ULTRAMORPH = AVPEntityAttributesBindingRegistry.builder()
        .add(Attributes.ATTACK_DAMAGE, 5.5D)
        .add(Attributes.MAX_HEALTH, 230.0D)
        .add(Attributes.MOVEMENT_SPEED, 0.4700000238418579D)
        .build();

    public static void addBindings() {
        AVPEntityAttributesBindingRegistry.addBinding(
            AVPExoticAlienEntityTypes.DEACON_ADULT_ENGINEER,
            DEACON_ADULT_ENGINEER
        );
        AVPEntityAttributesBindingRegistry.addBinding(AVPExoticAlienEntityTypes.DRACOBURSTER, DRACOBURSTER);
        AVPEntityAttributesBindingRegistry.addBinding(AVPExoticAlienEntityTypes.DRACOMORPH, DRACOMORPH);
        AVPEntityAttributesBindingRegistry.addBinding(AVPExoticAlienEntityTypes.OCTOHUGGER, OCTOHUGGER);
        AVPEntityAttributesBindingRegistry.addBinding(AVPExoticAlienEntityTypes.OVAMORPH_DRACO, OVAMORPH_DRACO);
        AVPEntityAttributesBindingRegistry.addBinding(AVPExoticAlienEntityTypes.ULTRAMORPH, ULTRAMORPH);
    }
}
