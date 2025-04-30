package com.avp.common.gene;

import com.avp.AVPResources;

public class GeneKeys {

    // For boilers
    public static final GeneKey ACID_VOLATILITY = create("acid_volatility");

    public static final GeneKey ARMOR = create("armor");

    public static final GeneKey ARMOR_TOUGHNESS = create("armor_toughness");

    public static final GeneKey ATTACK_DAMAGE = create("attack_damage");

    public static final GeneKey BONUS_FACEHUGGER_COUNT = create("bonus_facehugger_count");

    public static final GeneKey BONUS_RESIN_PRODUCTION = create("bonus_resin_production");

    public static final GeneKey BONUS_RESIN_RECYCLE = create("bonus_resin_recycle");

    public static final GeneKey COLD_RESISTANCE = create("cold_resistance");

    public static final GeneKey EGG_LAY_SPEED = create("egg_lay_speed");

    public static final GeneKey EGG_RESIN_COST = create("eggsack_resin_cost");

    public static final GeneKey EGGSACK_RESIN_COST = create("eggsack_resin_cost");

    public static final GeneKey FIRE_RESISTANCE = create("fire_resistance");

    // For aberrant strain
    public static final GeneKey GENETIC_INTEGRITY = create("genetic_integrity");

    public static final GeneKey GROWTH_SPEED = create("growth_speed");

    public static final GeneKey HATCH_SPEED = create("hatch_speed");

    public static final GeneKey HEALTH = create("health");

    public static final GeneKey INTELLIGENCE = create("intelligence");

    public static final GeneKey MOVE_SPEED = create("move_speed");

    private static GeneKey create(String location) {
        return new GeneKey(AVPResources.location(location));
    }
}
