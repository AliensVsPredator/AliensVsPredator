package com.avp.fabric.data.lang.en_us.provider;

import net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider;

import java.util.function.Consumer;

public class EnUsConfigProvider {

    public static final Consumer<FabricLanguageProvider.TranslationBuilder> CONSUMER = builder -> {
        builder.add("config.screen.avp", "AVP Config");

        builder.add("config.avp.option.blockConfigs", "Block Setting Configs");
        builder.add("config.avp.option.RESONATOR_REPLACE_TICKS", "Resonator Replace Ticks");
        builder.add("config.avp.option.RESONATOR_REPLACE_RADIUS", "Resonator Replace Radius");

        builder.add("config.avp.option.hiveConfigs", "Hive Configs");
        builder.add(
            "config.avp.option.MINIMUM_DISTANCE_BETWEEN_HIVES_IN_BLOCKS",
            "Minimum distance between hive centers in blocks"
        );
        builder.add("config.avp.option.HIVE_RADIUS_IN_BLOCKS", "The radius of hives in blocks");
        builder.add(
            "config.avp.option.HIVE_LEASH_RADIUS_IN_BLOCKS",
            "Maximum distance away from a hive that Xenomorphs can join or remain as a member"
        );
        builder.add("config.avp.option.HIVE_MAX_PRAETORIAN_COUNT", "Maximum number of Praetorians allowed within a hive");
        builder.add(
            "config.avp.option.HIVE_MEMBERS_REQUIRED_FOR_PRAETORIAN",
            "Number of hive members required to spawn a Praetorian"
        );
        builder.add(
            "config.avp.option.HIVE_DARKEN_SCREEN",
            "Determines if the screen should darken when the hive boss bar appears"
        );
        builder.add("config.avp.option.HIVE_DEBUG_ENABLED", "Enables hive debugging");
        builder.add("config.avp.option.HIVE_DEBUG_HIGHLIGHT_LEADER", "Applies a glow effect to the hive leader");
        builder.add("config.avp.option.HIVE_DEBUG_HIGHLIGHT_ALL_MEMBERS", "Applies a glow effect to all hive members");
        builder.add("config.avp.option.HIVE_DEBUG_MARK_HIVE_CENTER", "Marks the hive center with a block");
        builder.add("config.avp.option.CHESTBURSTER_MAX_GROWTH_TIMER_SECONDS", "Chestburster Max Growth Timer Seconds");
        builder.add("config.avp.option.DRONE_MAX_GROWTH_TIMER_SECONDS", "Drone Max Growth Timer Seconds");
        builder.add("config.avp.option.WARRIOR_MAX_GROWTH_TIMER_SECONDS", "Warrior Max Growth Timer Seconds");
        builder.add("config.avp.option.PRAETORIAN_MAX_GROWTH_TIMER_SECONDS", "Praetorian Max Growth Timer Seconds");
        builder.add("config.avp.option.PRAETORIAN_SHORTCUT_TIMER_SECONDS", "Praetorian Shortcut Timer Seconds");

        builder.add("config.avp.option.spawnConfigs", "Mob Spawn Configs");
        builder.add("config.avp.option.NATURAL_SPAWNING_ENABLED", "Enable natural spawning for Xenomorphs in the overworld.");
        builder.add(
            "config.avp.option.ADULT_SPAWNING_ENABLED",
            "Enable natural spawning for adult Xenomorphs in the overworld."
        );
        builder.add(
            "config.avp.option.YOUNG_SPAWNING_ENABLED",
            "Enable natural spawning for young Xenomorphs (eggs, facehuggers, bursters, etc.) in the overworld."
        );
        builder.add(
            "config.avp.option.REMOVE_VANILLA_SPAWNS",
            "Removes certain hostile monster spawns, allowing others like Xenomorphs to spawn more frequently."
        );
        builder.add("config.avp.option.CHESTBURSTER_SPAWN", "Chestburster spawn settings");
        builder.add("config.avp.option.DRONE_SPAWN", "Drone spawn settings");
        builder.add("config.avp.option.NETHER_CHESTBURSTER_SPAWN", "Nether Chestburster spawn settings");
        builder.add("config.avp.option.NETHER_DRONE_SPAWN", "Nether Drone spawn settings");
        builder.add("config.avp.option.NETHER_OVOMORPH_SPAWN", "Nether Ovomorph spawn settings");
        builder.add("config.avp.option.NETHER_PRAETORIAN_SPAWN", "Nether Praetorian spawn settings");
        builder.add("config.avp.option.NETHER_WARRIOR_SPAWN", "Nether Warrior spawn settings");
        builder.add("config.avp.option.NETHER_QUEEN_SPAWN", "Nether Queen spawn settings");
        builder.add("config.avp.option.OVOMORPH_SPAWN", "Ovomorph spawn settings");
        builder.add("config.avp.option.PRAETORIAN_SPAWN", "Praetorian spawn settings");
        builder.add("config.avp.option.QUEEN_SPAWN", "Queen spawn settings");
        builder.add("config.avp.option.WARRIOR_SPAWN", "Warrior spawn settings");
        builder.add("config.avp.option.enabled", "Enable spawning");
        builder.add("config.avp.option.maxY", "Maximum Y-level at for spawn");
        builder.add("config.avp.option.minY", "Minimum Y-level at for spawn");
        builder.add("config.avp.option.minGroupSize", "Minimum group size for spawns");
        builder.add("config.avp.option.maxGroupSize", "Maximum group size for spawns");
        builder.add("config.avp.option.weight", "Spawn weight");
        builder.add("config.avp.option.requiresResin", "Requires resin for Nether Ovomorph spawning");

        builder.add("config.avp.option.statsConfigs", "Mob Stat Configs");
        builder.add("config.avp.option.ABERRANT_STATS_MULTIPLIER", "Aberrant Stats Multiplier");
        builder.add("config.avp.option.IRRADIATED_STATS_MULTIPLIER", "Irradiated Stats Multiplier");
        builder.add("config.avp.option.ACID_ATTACK_DAMAGE", "Acid Damage per tick");
        builder.add("config.avp.option.CHESTBURSTER_STATS", "Chestburster stats");
        builder.add("config.avp.option.health", "Health value");
        builder.add("config.avp.option.attackDamage", "Attack damage");
        builder.add("config.avp.option.healthRegenPerSecond", "Health regeneration per second");
        builder.add("config.avp.option.knockbackResistance", "Knockback resistance");
        builder.add("config.avp.option.moveSpeed", "Movement speed");
        builder.add("config.avp.option.armorToughness", "Armor toughness.");
        builder.add("config.avp.option.armor", "Armor value");
        builder.add("config.avp.option.nestTickrate", "Nest tickrate");
        builder.add("config.avp.option.followRange", "Follow range");
        builder.add("config.avp.option.DRONE_STATS", "Drone stats");
        builder.add("config.avp.option.OVOMORPH_STATS", "Ovomorph stats");
        builder.add("config.avp.option.PRAETORIAN_STATS", "Praetorian stats");
        builder.add("config.avp.option.QUEEN_STATS", "Queen stats");
        builder.add("config.avp.option.WARRIOR_STATS", "Warrior stats");
        builder.add("config.avp.option.MARINE_STATS", "Marine stats");
        builder.add("config.avp.option.FACEHUGGER_STATS", "Facehugger stats");

        builder.add("config.avp.option.weaponConfigs", "Weapon Options");
        builder.add("config.avp.option.BULLETS_DAMAGE_BLOCKS_ENABLED", "Enable bullet collision damage to blocks");
        builder.add("config.avp.option.ENABLE_NUKE_BLOCK_MECHS", "Allow nukes to work");
        builder.add("config.avp.option.TURRET_FOV", "Turret FOV");
        builder.add("config.avp.option.TURRET_RANGE", "Turret range");
        builder.add("config.avp.option.TURRET_DAMAGE", "Turret damage");
        builder.add("config.avp.option.TURRET_AMMOCHEST_SEARCH_RANGE", "Turret ammo chest search range");

    };
}
