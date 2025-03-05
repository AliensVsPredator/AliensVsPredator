package com.avp.common.config;

import com.avp.common.entity.constant.ArmorConstants;
import com.avp.common.entity.constant.ArmorToughnessConstants;
import com.avp.common.entity.constant.AttackDamageConstants;
import com.avp.common.entity.constant.FollowRangeConstants;
import com.avp.common.entity.constant.HealthConstants;
import com.avp.common.entity.constant.HealthRegenConstants;
import com.avp.common.entity.constant.KnockbackResistanceConstants;
import com.avp.common.entity.constant.MoveSpeedConstants;

public class Configs {

    private static final int NETHER_TOP_Y_LEVEL = 128;

    private static final int NETHER_BOTTOM_Y_LEVEL = 0;

    private static final int OVERWORLD_SEA_LEVEL = 62;

    private static final int OVERWORLD_BOTTOM_Y_LEVEL = -64;

    public static final Config HIVES = ConfigBuilder.create("hives")
        // 64 chunks by 16 blocks each chunk = 1024 blocks
        .property(ConfigProperties.MINIMUM_DISTANCE_BETWEEN_HIVES_IN_BLOCKS, 64 * 16)
        .property(ConfigProperties.HIVE_RADIUS_IN_BLOCKS, 64)
        .property(ConfigProperties.HIVE_LEASH_RADIUS_IN_BLOCKS, 98)
        .property(ConfigProperties.HIVE_MAX_PRAETORIAN_COUNT, 6)
        .property(ConfigProperties.HIVE_MEMBERS_REQUIRED_FOR_PRAETORIAN, 8)
        .property(ConfigProperties.HIVE_DEBUG_ENABLED, false)
        .property(ConfigProperties.HIVE_DEBUG_HIGHLIGHT_ALL_MEMBERS, false)
        .property(ConfigProperties.HIVE_DEBUG_HIGHLIGHT_LEADER, true)
        .property(ConfigProperties.HIVE_DEBUG_MARK_HIVE_CENTER, true)
        .property(ConfigProperties.HIVE_DARKEN_SCREEN, true)
        .build();

    public static final Config SPAWNING = ConfigBuilder.create("spawning")
        // Natural spawning properties
        .property(ConfigProperties.NATURAL_SPAWNING_ENABLED, true)
        .property(ConfigProperties.ADULT_SPAWNING_ENABLED, true)
        .property(ConfigProperties.YOUNG_SPAWNING_ENABLED, true)
        .property(ConfigProperties.REMOVE_VANILLA_SPAWNS, false)
        .property(ConfigProperties.ALIEN_CUSTOM_MOB_CATEGORY_ENABLED, false)
        .property(ConfigProperties.ALIEN_CUSTOM_MOB_CATEGORY_SPAWN_LIMIT, 70)
        // Mob spawning properties
        .property(ConfigProperties.CHESTBURSTER_SPAWNING.mobSpawning().enabled(), true)
        .property(ConfigProperties.CHESTBURSTER_SPAWNING.mobSpawning().maxGroupSize(), 2)
        .property(ConfigProperties.CHESTBURSTER_SPAWNING.mobSpawning().maxY(), -24)
        .property(ConfigProperties.CHESTBURSTER_SPAWNING.mobSpawning().minGroupSize(), 1)
        .property(ConfigProperties.CHESTBURSTER_SPAWNING.mobSpawning().minY(), OVERWORLD_BOTTOM_Y_LEVEL)
        .property(ConfigProperties.CHESTBURSTER_SPAWNING.mobSpawning().weight(), 10)
        .property(ConfigProperties.CHESTBURSTER_SPAWNING.requiresResin(), true)

        .property(ConfigProperties.DRONE_SPAWNING.mobSpawning().enabled(), true)
        .property(ConfigProperties.DRONE_SPAWNING.mobSpawning().maxGroupSize(), 2)
        .property(ConfigProperties.DRONE_SPAWNING.mobSpawning().maxY(), 75)
        .property(ConfigProperties.DRONE_SPAWNING.mobSpawning().minGroupSize(), 1)
        .property(ConfigProperties.DRONE_SPAWNING.mobSpawning().minY(), OVERWORLD_BOTTOM_Y_LEVEL)
        .property(ConfigProperties.DRONE_SPAWNING.mobSpawning().weight(), 50)
        .property(ConfigProperties.DRONE_SPAWNING.requiresResin(), true)

        .property(ConfigProperties.NETHER_CHESTBURSTER_SPAWNING.mobSpawning().enabled(), true)
        .property(ConfigProperties.NETHER_CHESTBURSTER_SPAWNING.mobSpawning().maxGroupSize(), 2)
        .property(ConfigProperties.NETHER_CHESTBURSTER_SPAWNING.mobSpawning().maxY(), NETHER_TOP_Y_LEVEL)
        .property(ConfigProperties.NETHER_CHESTBURSTER_SPAWNING.mobSpawning().minGroupSize(), 1)
        .property(ConfigProperties.NETHER_CHESTBURSTER_SPAWNING.mobSpawning().minY(), NETHER_BOTTOM_Y_LEVEL)
        .property(ConfigProperties.NETHER_CHESTBURSTER_SPAWNING.mobSpawning().weight(), 10)
        .property(ConfigProperties.NETHER_CHESTBURSTER_SPAWNING.requiresResin(), false)

        .property(ConfigProperties.NETHER_DRONE_SPAWNING.mobSpawning().enabled(), true)
        .property(ConfigProperties.NETHER_DRONE_SPAWNING.mobSpawning().maxGroupSize(), 2)
        .property(ConfigProperties.NETHER_DRONE_SPAWNING.mobSpawning().maxY(), NETHER_TOP_Y_LEVEL)
        .property(ConfigProperties.NETHER_DRONE_SPAWNING.mobSpawning().minGroupSize(), 1)
        .property(ConfigProperties.NETHER_DRONE_SPAWNING.mobSpawning().minY(), NETHER_BOTTOM_Y_LEVEL)
        .property(ConfigProperties.NETHER_DRONE_SPAWNING.mobSpawning().weight(), 50)
        .property(ConfigProperties.NETHER_DRONE_SPAWNING.requiresResin(), false)

        .property(ConfigProperties.NETHER_OVAMORPH_SPAWNING.mobSpawning().enabled(), true)
        .property(ConfigProperties.NETHER_OVAMORPH_SPAWNING.mobSpawning().maxGroupSize(), 3)
        .property(ConfigProperties.NETHER_OVAMORPH_SPAWNING.mobSpawning().maxY(), NETHER_TOP_Y_LEVEL)
        .property(ConfigProperties.NETHER_OVAMORPH_SPAWNING.mobSpawning().minGroupSize(), 1)
        .property(ConfigProperties.NETHER_OVAMORPH_SPAWNING.mobSpawning().minY(), NETHER_BOTTOM_Y_LEVEL)
        .property(ConfigProperties.NETHER_OVAMORPH_SPAWNING.mobSpawning().weight(), 25)
        .property(ConfigProperties.NETHER_OVAMORPH_SPAWNING.requiresResin(), false)

        .property(ConfigProperties.NETHER_PRAETORIAN_SPAWNING.mobSpawning().enabled(), true)
        .property(ConfigProperties.NETHER_PRAETORIAN_SPAWNING.mobSpawning().maxGroupSize(), 1)
        .property(ConfigProperties.NETHER_PRAETORIAN_SPAWNING.mobSpawning().maxY(), NETHER_TOP_Y_LEVEL)
        .property(ConfigProperties.NETHER_PRAETORIAN_SPAWNING.mobSpawning().minGroupSize(), 1)
        .property(ConfigProperties.NETHER_PRAETORIAN_SPAWNING.mobSpawning().minY(), NETHER_BOTTOM_Y_LEVEL)
        .property(ConfigProperties.NETHER_PRAETORIAN_SPAWNING.mobSpawning().weight(), 10)
        .property(ConfigProperties.NETHER_PRAETORIAN_SPAWNING.requiresResin(), false)

        .property(ConfigProperties.NETHER_WARRIOR_SPAWNING.mobSpawning().enabled(), true)
        .property(ConfigProperties.NETHER_WARRIOR_SPAWNING.mobSpawning().maxGroupSize(), 2)
        .property(ConfigProperties.NETHER_WARRIOR_SPAWNING.mobSpawning().maxY(), NETHER_TOP_Y_LEVEL)
        .property(ConfigProperties.NETHER_WARRIOR_SPAWNING.mobSpawning().minGroupSize(), 1)
        .property(ConfigProperties.NETHER_WARRIOR_SPAWNING.mobSpawning().minY(), NETHER_BOTTOM_Y_LEVEL)
        .property(ConfigProperties.NETHER_WARRIOR_SPAWNING.mobSpawning().weight(), 25)
        .property(ConfigProperties.NETHER_WARRIOR_SPAWNING.requiresResin(), false)

        .property(ConfigProperties.OVAMORPH_SPAWNING.mobSpawning().enabled(), true)
        .property(ConfigProperties.OVAMORPH_SPAWNING.mobSpawning().maxGroupSize(), 3)
        .property(ConfigProperties.OVAMORPH_SPAWNING.mobSpawning().maxY(), -24)
        .property(ConfigProperties.OVAMORPH_SPAWNING.mobSpawning().minGroupSize(), 1)
        .property(ConfigProperties.OVAMORPH_SPAWNING.mobSpawning().minY(), OVERWORLD_BOTTOM_Y_LEVEL)
        .property(ConfigProperties.OVAMORPH_SPAWNING.mobSpawning().weight(), 25)
        .property(ConfigProperties.OVAMORPH_SPAWNING.requiresResin(), true)

        .property(ConfigProperties.PRAETORIAN_SPAWNING.mobSpawning().enabled(), true)
        .property(ConfigProperties.PRAETORIAN_SPAWNING.mobSpawning().maxGroupSize(), 1)
        .property(ConfigProperties.PRAETORIAN_SPAWNING.mobSpawning().maxY(), 0)
        .property(ConfigProperties.PRAETORIAN_SPAWNING.mobSpawning().minGroupSize(), 1)
        .property(ConfigProperties.PRAETORIAN_SPAWNING.mobSpawning().minY(), OVERWORLD_BOTTOM_Y_LEVEL)
        .property(ConfigProperties.PRAETORIAN_SPAWNING.mobSpawning().weight(), 10)
        .property(ConfigProperties.PRAETORIAN_SPAWNING.requiresResin(), true)

        .property(ConfigProperties.QUEEN_SPAWNING.mobSpawning().enabled(), true)
        .property(ConfigProperties.QUEEN_SPAWNING.mobSpawning().maxGroupSize(), 1)
        .property(ConfigProperties.QUEEN_SPAWNING.mobSpawning().maxY(), -24)
        .property(ConfigProperties.QUEEN_SPAWNING.mobSpawning().minGroupSize(), 1)
        .property(ConfigProperties.QUEEN_SPAWNING.mobSpawning().minY(), OVERWORLD_BOTTOM_Y_LEVEL)
        .property(ConfigProperties.QUEEN_SPAWNING.mobSpawning().weight(), 5)
        .property(ConfigProperties.QUEEN_SPAWNING.requiresResin(), false)

        .property(ConfigProperties.WARRIOR_SPAWNING.mobSpawning().enabled(), true)
        .property(ConfigProperties.WARRIOR_SPAWNING.mobSpawning().maxGroupSize(), 2)
        .property(ConfigProperties.WARRIOR_SPAWNING.mobSpawning().maxY(), 32)
        .property(ConfigProperties.WARRIOR_SPAWNING.mobSpawning().minGroupSize(), 1)
        .property(ConfigProperties.WARRIOR_SPAWNING.mobSpawning().minY(), OVERWORLD_BOTTOM_Y_LEVEL)
        .property(ConfigProperties.WARRIOR_SPAWNING.mobSpawning().weight(), 25)
        .property(ConfigProperties.WARRIOR_SPAWNING.requiresResin(), true)

        .property(ConfigProperties.YAUTJA_SPAWNING.enabled(), true)
        .property(ConfigProperties.YAUTJA_SPAWNING.maxGroupSize(), 1)
        .property(ConfigProperties.YAUTJA_SPAWNING.maxY(), 100)
        .property(ConfigProperties.YAUTJA_SPAWNING.minGroupSize(), 1)
        .property(ConfigProperties.YAUTJA_SPAWNING.minY(), OVERWORLD_SEA_LEVEL)
        .property(ConfigProperties.YAUTJA_SPAWNING.weight(), 10)
        .build();

    public static final Config STATS = ConfigBuilder.create("stats")
        // Acid properties
        .property(ConfigProperties.ACID_ATTACK_DAMAGE, 1F)
        // Chestburster properties
        .property(ConfigProperties.CHESTBURSTER_ATTRIBUTES.attackDamage(), AttackDamageConstants.CHESTBURSTER_ATTACK_DAMAGE)
        .property(ConfigProperties.CHESTBURSTER_ATTRIBUTES.health(), HealthConstants.CHESTBURSTER_HEALTH)
        .property(
            ConfigProperties.CHESTBURSTER_ATTRIBUTES.healthRegenPerSecond(),
            HealthRegenConstants.CHESTBURSTER_HEALTH_REGEN
        )
        .property(
            ConfigProperties.CHESTBURSTER_ATTRIBUTES.knockbackResistance(),
            KnockbackResistanceConstants.CHESTBURSTER_KNOCKBACK_RESISTANCE
        )
        .property(ConfigProperties.CHESTBURSTER_ATTRIBUTES.moveSpeed(), MoveSpeedConstants.CHESTBURSTER_SPEED)
        // Drone properties
        .property(ConfigProperties.DRONE_ATTRIBUTES.armor(), ArmorConstants.DRONE_ARMOR)
        .property(ConfigProperties.DRONE_ATTRIBUTES.attackDamage(), AttackDamageConstants.DRONE_ATTACK_DAMAGE)
        .property(ConfigProperties.DRONE_ATTRIBUTES.followRange(), FollowRangeConstants.DRONE_FOLLOW_RANGE)
        .property(ConfigProperties.DRONE_ATTRIBUTES.health(), HealthConstants.DRONE_HEALTH)
        .property(ConfigProperties.DRONE_ATTRIBUTES.healthRegenPerSecond(), HealthRegenConstants.DRONE_HEALTH_REGEN)
        .property(
            ConfigProperties.DRONE_ATTRIBUTES.knockbackResistance(),
            KnockbackResistanceConstants.DRONE_KNOCKBACK_RESISTANCE
        )
        .property(ConfigProperties.DRONE_ATTRIBUTES.moveSpeed(), MoveSpeedConstants.DRONE_SPEED)
        // Ovamorph properties
        .property(ConfigProperties.OVAMORPH_ATTRIBUTES.health(), HealthConstants.OVAMORPH_HEALTH)
        .property(ConfigProperties.OVAMORPH_ATTRIBUTES.healthRegenPerSecond(), HealthRegenConstants.OVAMORPH_HEALTH_REGEN)
        .property(
            ConfigProperties.OVAMORPH_ATTRIBUTES.knockbackResistance(),
            KnockbackResistanceConstants.OVAMORPH_KNOCKBACK_RESISTANCE
        )
        // Praetorian properties
        .property(ConfigProperties.PRAETORIAN_ATTRIBUTES.armor(), ArmorConstants.PRAETORIAN_ARMOR)
        .property(ConfigProperties.PRAETORIAN_ATTRIBUTES.armorToughness(), ArmorToughnessConstants.PRAETORIAN_ARMOR_TOUGHNESS)
        .property(ConfigProperties.PRAETORIAN_ATTRIBUTES.attackDamage(), AttackDamageConstants.PRAETORIAN_ATTACK_DAMAGE)
        .property(ConfigProperties.PRAETORIAN_ATTRIBUTES.followRange(), FollowRangeConstants.PRAETORIAN_FOLLOW_RANGE)
        .property(ConfigProperties.PRAETORIAN_ATTRIBUTES.health(), HealthConstants.PRAETORIAN_HEALTH)
        .property(ConfigProperties.PRAETORIAN_ATTRIBUTES.healthRegenPerSecond(), HealthRegenConstants.PRAETORIAN_HEALTH_REGEN)
        .property(
            ConfigProperties.PRAETORIAN_ATTRIBUTES.knockbackResistance(),
            KnockbackResistanceConstants.PRAETORIAN_KNOCKBACK_RESISTANCE
        )
        .property(ConfigProperties.PRAETORIAN_ATTRIBUTES.moveSpeed(), MoveSpeedConstants.PRAETORIAN_SPEED)
        // Queen properties
        .property(ConfigProperties.QUEEN_ATTRIBUTES.armor(), ArmorConstants.QUEEN_ARMOR)
        .property(ConfigProperties.QUEEN_ATTRIBUTES.armorToughness(), ArmorToughnessConstants.QUEEN_ARMOR_TOUGHNESS)
        .property(ConfigProperties.QUEEN_ATTRIBUTES.attackDamage(), AttackDamageConstants.QUEEN_ATTACK_DAMAGE)
        .property(ConfigProperties.QUEEN_ATTRIBUTES.followRange(), FollowRangeConstants.QUEEN_FOLLOW_RANGE)
        .property(ConfigProperties.QUEEN_ATTRIBUTES.health(), HealthConstants.QUEEN_HEALTH)
        .property(ConfigProperties.QUEEN_ATTRIBUTES.healthRegenPerSecond(), HealthRegenConstants.QUEEN_HEALTH_REGEN)
        .property(
            ConfigProperties.QUEEN_ATTRIBUTES.knockbackResistance(),
            KnockbackResistanceConstants.QUEEN_KNOCKBACK_RESISTANCE
        )
        .property(ConfigProperties.QUEEN_ATTRIBUTES.moveSpeed(), MoveSpeedConstants.QUEEN_SPEED)
        // Warrior properties
        .property(ConfigProperties.WARRIOR_ATTRIBUTES.armor(), ArmorConstants.WARRIOR_ARMOR)
        .property(ConfigProperties.WARRIOR_ATTRIBUTES.attackDamage(), AttackDamageConstants.WARRIOR_ATTACK_DAMAGE)
        .property(ConfigProperties.WARRIOR_ATTRIBUTES.followRange(), FollowRangeConstants.WARRIOR_FOLLOW_RANGE)
        .property(ConfigProperties.WARRIOR_ATTRIBUTES.health(), HealthConstants.WARRIOR_HEALTH)
        .property(ConfigProperties.WARRIOR_ATTRIBUTES.healthRegenPerSecond(), HealthRegenConstants.WARRIOR_HEALTH_REGEN)
        .property(
            ConfigProperties.WARRIOR_ATTRIBUTES.knockbackResistance(),
            KnockbackResistanceConstants.WARRIOR_KNOCKBACK_RESISTANCE
        )
        .property(ConfigProperties.WARRIOR_ATTRIBUTES.moveSpeed(), MoveSpeedConstants.WARRIOR_SPEED)
        // Yautja properties
        .property(ConfigProperties.YAUTJA_ATTRIBUTES.armor(), ArmorConstants.YAUTJA_ARMOR)
        .property(ConfigProperties.YAUTJA_ATTRIBUTES.armorToughness(), ArmorToughnessConstants.YAUTJA_ARMOR_TOUGHNESS)
        .property(ConfigProperties.YAUTJA_ATTRIBUTES.attackDamage(), AttackDamageConstants.YAUTJA_ATTACK_DAMAGE)
        .property(ConfigProperties.YAUTJA_ATTRIBUTES.followRange(), FollowRangeConstants.YAUTJA_FOLLOW_RANGE)
        .property(ConfigProperties.YAUTJA_ATTRIBUTES.health(), HealthConstants.YAUTJA_HEALTH)
        .property(
            ConfigProperties.YAUTJA_ATTRIBUTES.knockbackResistance(),
            KnockbackResistanceConstants.YAUTJA_KNOCKBACK_RESISTANCE
        )
        .property(ConfigProperties.YAUTJA_ATTRIBUTES.moveSpeed(), MoveSpeedConstants.YAUTJA_SPEED)
        .build();

    public static final Config WEAPONS = ConfigBuilder.create("weapons")
        .property(ConfigProperties.BULLETS_DAMAGE_BLOCKS_ENABLED, true)
        .build();
}
