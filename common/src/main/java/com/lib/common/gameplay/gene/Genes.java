package com.lib.common.gameplay.gene;

import net.minecraft.world.entity.ai.attributes.Attributes;

import com.avp.common.registry.AVPDeferredHolder;

public class Genes {

    public static final AVPDeferredHolder<Gene> ARMOR = GeneRegistry.register(
        () -> new Gene.Attribute("armor", Attributes.ARMOR)
    );

    public static final AVPDeferredHolder<Gene> ARMOR_TOUGHNESS = GeneRegistry.register(
        () -> new Gene.Attribute("armor_toughness", Attributes.ARMOR_TOUGHNESS)
    );

    public static final AVPDeferredHolder<Gene> ATTACK_DAMAGE = GeneRegistry.register(
        () -> new Gene.Attribute("attack_damage", Attributes.ATTACK_DAMAGE)
    );

    public static final AVPDeferredHolder<Gene> BONUS_EMBRYO_COUNT = GeneRegistry.register(
        () -> new Gene.Simple("bonus_embryo_count")
    );

    public static final AVPDeferredHolder<Gene> BONUS_PARASITE_COUNT = GeneRegistry.register(
        () -> new Gene.Simple("bonus_parasite_count")
    );

    public static final AVPDeferredHolder<Gene> COLD_RESISTANCE = GeneRegistry.register(() -> new Gene.Simple("cold_resistance"));

    public static final AVPDeferredHolder<Gene> FIRE_RESISTANCE = GeneRegistry.register(() -> new Gene.Simple("fire_resistance"));

    public static final AVPDeferredHolder<Gene> GENETIC_INTEGRITY = GeneRegistry.register(() -> new Gene.Simple("genetic_integrity"));

    public static final AVPDeferredHolder<Gene> INTELLIGENCE = GeneRegistry.register(() -> new Gene.Simple("intelligence"));

    public static final AVPDeferredHolder<Gene> KNOCKBACK_RESISTANCE = GeneRegistry.register(
        () -> new Gene.Attribute("knockback_resistance", Attributes.KNOCKBACK_RESISTANCE)
    );

    public static final AVPDeferredHolder<Gene> MAX_HEALTH = GeneRegistry.register(
        () -> new Gene.Attribute("max_health", Attributes.MAX_HEALTH)
    );

    public static final AVPDeferredHolder<Gene> MOVE_SPEED = GeneRegistry.register(
        () -> new Gene.Attribute("move_speed", Attributes.MOVEMENT_SPEED)
    );

    public static void initialize() {}
}
