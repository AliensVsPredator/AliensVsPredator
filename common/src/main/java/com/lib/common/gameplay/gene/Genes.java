package com.lib.common.gameplay.gene;

import net.minecraft.core.Holder;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.Attributes;

import com.avp.AVPResources;
import com.avp.common.registry.AVPDeferredHolder;

public class Genes {

    public static final AVPDeferredHolder<Gene> ACID_RESISTANCE = registerSimple("acid_resistance");

    // Controls how explosive xenomorph acid is. This results in boilers.
    public static final AVPDeferredHolder<Gene> ACID_VOLATILITY = registerSimple("acid_volatility");

    public static final AVPDeferredHolder<Gene> ACIDIC_BLOOD = registerSimple("acidic_blood");

    public static final AVPDeferredHolder<Gene> ARMOR = registerAttribute("armor", Attributes.ARMOR);

    public static final AVPDeferredHolder<Gene> ARMOR_TOUGHNESS = registerAttribute("armor_toughness", Attributes.ARMOR_TOUGHNESS);

    public static final AVPDeferredHolder<Gene> ATTACK_DAMAGE = registerAttribute("attack_damage", Attributes.ATTACK_DAMAGE);

    public static final AVPDeferredHolder<Gene> BONUS_EMBRYO_COUNT = registerSimple("bonus_embryo_count");

    public static final AVPDeferredHolder<Gene> BONUS_PARASITE_COUNT = registerSimple("bonus_parasite_count");

    public static final AVPDeferredHolder<Gene> COLD_RESISTANCE = registerSimple("cold_resistance");

    public static final AVPDeferredHolder<Gene> FIRE_RESISTANCE = registerSimple("fire_resistance");

    public static final AVPDeferredHolder<Gene> GENETIC_INTEGRITY = registerSimple("genetic_integrity");

    public static final AVPDeferredHolder<Gene> INTELLIGENCE = registerSimple("intelligence");

    public static final AVPDeferredHolder<Gene> KNOCKBACK_RESISTANCE = registerAttribute(
        "knockback_resistance",
        Attributes.KNOCKBACK_RESISTANCE
    );

    public static final AVPDeferredHolder<Gene> MAX_HEALTH = registerAttribute("max_health", Attributes.MAX_HEALTH);

    public static final AVPDeferredHolder<Gene> MOVE_SPEED = registerAttribute("move_speed", Attributes.MOVEMENT_SPEED);

    public static final AVPDeferredHolder<Gene> WARP = registerSimple("warp");

    public static void initialize() {}

    private static AVPDeferredHolder<Gene> registerAttribute(String name, Holder<Attribute> attribute) {
        return GeneRegistry.register(() -> new Gene.Attribute(AVPResources.location(name), attribute));
    }

    private static AVPDeferredHolder<Gene> registerSimple(String name) {
        return GeneRegistry.register(() -> new Gene.Simple(AVPResources.location(name)));
    }
}
