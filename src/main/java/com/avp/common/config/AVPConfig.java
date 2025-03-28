package com.avp.common.config;

import mod.azure.azurelib.common.api.common.config.Config;
import mod.azure.azurelib.common.internal.common.config.Configurable;

import com.avp.AVP;
import com.avp.common.entity.constant.*;

@Config(id = AVP.MOD_ID)
public class AVPConfig {

    @Configurable
    @Configurable.Synchronized
    @Configurable.Comment("Controls the Mobs Spawn Settings")
    public SpawnConfigs spawnConfigs = new SpawnConfigs();

    public static class SpawnConfigs {

        @Configurable
        @Configurable.Synchronized
        @Configurable.Comment(
            "If set to true, xenomorphs will spawn in the overworld naturally along with other monsters. Modifying this requires restarting the game."
        )
        public boolean NATURAL_SPAWNING_ENABLED = true;

        @Configurable
        @Configurable.Synchronized
        @Configurable.Comment(
            "If set to true, *ADULT* xenomorphs will spawn in the overworld naturally along with other monsters. Modifying this requires restarting the game."
        )
        public boolean ADULT_SPAWNING_ENABLED = true;

        @Configurable
        @Configurable.Synchronized
        @Configurable.Comment(
            "If set to true, *YOUNG* xenomorphs (eggs, facehuggers, bursters, etc.) will spawn in the overworld naturally along with other monsters. Modifying this requires restarting the game."
        )
        public boolean YOUNG_SPAWNING_ENABLED = true;

        @Configurable
        @Configurable.Synchronized
        @Configurable.Comment(
            {
                "WARNING: This might break mob farms.",
                "If set to true, certain hostile monster spawns will be removed. This will allow other hostile monsters (xenomorphs, for example) to spawn more frequently. Modifying this requires restarting the game.",
                "The list of mobs that have their spawns removed can be viewed (and modified) with the avp:tags/entity/remove_vanilla_spawns tag.",
                "ENABLING THIS MAY BREAK CERTAIN FARMS THAT RELY ON MOB SPAWNS. Enable at your own risk, you have been warned!"
            }
        )
        public boolean REMOVE_VANILLA_SPAWNS = false;

        @Configurable
        @Configurable.Synchronized
        @Configurable.Comment("Modifying these will require restarting the game.")
        public SpawnSettings CHESTBURSTER_SPAWN = new SpawnSettings(true, -24, -64, 1, 2, 10, true);

        @Configurable
        @Configurable.Synchronized
        @Configurable.Comment("Modifying these will require restarting the game.")
        public SpawnSettings DRONE_SPAWN = new SpawnSettings(true, 75, -64, 1, 2, 50, true);

        @Configurable
        @Configurable.Synchronized
        @Configurable.Comment("Modifying these will require restarting the game.")
        public SpawnSettings NETHER_CHESTBURSTER_SPAWN = new SpawnSettings(true, 128, 0, 1, 2, 10, false);

        @Configurable
        @Configurable.Synchronized
        @Configurable.Comment("Modifying these will require restarting the game.")
        public SpawnSettings NETHER_DRONE_SPAWN = new SpawnSettings(true, 128, 0, 1, 2, 50, false);

        @Configurable
        @Configurable.Synchronized
        @Configurable.Comment("Modifying these will require restarting the game.")
        public SpawnSettings NETHER_OVAMORPH_SPAWN = new SpawnSettings(true, 128, 0, 1, 3, 10, false);

        @Configurable
        @Configurable.Synchronized
        @Configurable.Comment("Nether Praetorian spawn settings. Modifying these will require restarting the game.")
        public SpawnSettings NETHER_PRAETORIAN_SPAWN = new SpawnSettings(true, 128, 0, 1, 1, 10, false);

        @Configurable
        @Configurable.Synchronized
        @Configurable.Comment("Nether Warrior spawn settings. Modifying these will require restarting the game.")
        public SpawnSettings NETHER_WARRIOR_SPAWN = new SpawnSettings(true, 128, 0, 1, 2, 25, false);

        @Configurable
        @Configurable.Synchronized
        @Configurable.Comment("Nether Queen spawn settings. Modifying these will require restarting the game.")
        public SpawnSettings NETHER_QUEEN_SPAWN = new SpawnSettings(true, 128, 0, 1, 1, 5, true);

        @Configurable
        @Configurable.Synchronized
        @Configurable.Comment("Ovamorph spawn settings. Modifying these will require restarting the game.")
        public SpawnSettings OVAMORPH_SPAWN = new SpawnSettings(true, -24, -64, 1, 3, 25, true);

        @Configurable
        @Configurable.Synchronized
        @Configurable.Comment("Praetorian spawn settings. Modifying these will require restarting the game.")
        public SpawnSettings PRAETORIAN_SPAWN = new SpawnSettings(true, 0, -64, 1, 1, 10, true);

        @Configurable
        @Configurable.Synchronized
        @Configurable.Comment("Queen spawn settings. Modifying these will require restarting the game.")
        public SpawnSettings QUEEN_SPAWN = new SpawnSettings(true, -24, -64, 1, 1, 5, false);

        @Configurable
        @Configurable.Synchronized
        @Configurable.Comment("Warrior spawn settings. Modifying these will require restarting the game.")
        public SpawnSettings WARRIOR_SPAWN = new SpawnSettings(true, 32, -64, 1, 2, 25, true);

        @Configurable
        @Configurable.Synchronized
        @Configurable.Comment("Yautja spawn settings. Modifying these will require restarting the game.")
        public SpawnSettings YAUTJA_SPAWN = new SpawnSettings(true, 100, 62, 1, 1, 10, false);

        public SpawnSettings MARINE_SPAWN = new SpawnSettings(true, 120, 64, 1, 1, 1, false);

        public static class SpawnSettings {

            @Configurable
            @Configurable.Synchronized
            @Configurable.Comment("If true, spawning is enabled.")
            public boolean enabled;

            @Configurable
            @Configurable.Synchronized
            @Configurable.Comment("The maximum Y-level at which this entity can spawn.")
            public int maxY;

            @Configurable
            @Configurable.Synchronized
            @Configurable.Comment("The minimum Y-level at which this entity can spawn.")
            public int minY;

            @Configurable
            @Configurable.Synchronized
            @Configurable.Comment("The minimum group size for this entity's spawn.")
            public int minGroupSize;

            @Configurable
            @Configurable.Synchronized
            @Configurable.Comment("The maximum group size for this entity's spawn.")
            public int maxGroupSize;

            @Configurable
            @Configurable.Synchronized
            @Configurable.Comment("The spawn weight for this entity.")
            public int weight;

            @Configurable
            @Configurable.Synchronized
            @Configurable.Comment("If true, spawning requires the entity to be near resin.")
            public boolean requiresResin;

            public SpawnSettings(
                boolean enabled,
                int maxY,
                int minY,
                int minGroupSize,
                int maxGroupSize,
                int weight,
                boolean requiresResin
            ) {
                this.enabled = enabled;
                this.maxY = maxY;
                this.minY = minY;
                this.minGroupSize = minGroupSize;
                this.maxGroupSize = maxGroupSize;
                this.weight = weight;
                this.requiresResin = requiresResin;
            }
        }
    }

    @Configurable
    @Configurable.Synchronized
    @Configurable.Comment("Controls the Mobs Stats")
    public StatsConfigs statsConfigs = new StatsConfigs();

    public static class StatsConfigs {

        @Configurable
        @Configurable.Synchronized
        @Configurable.Comment("Aberrant stats multiplier.")
        public float ABERRANT_STATS_MULTIPLIER = 0.9f;

        @Configurable
        @Configurable.Synchronized
        @Configurable.Comment("Irradiated stats multiplier.")
        public float IRRADIATED_STATS_MULTIPLIER = 1.2f;

        @Configurable
        @Configurable.Synchronized
        @Configurable.Comment("The acid stats for attack damage.")
        public float ACID_ATTACK_DAMAGE = 1.0f;

        @Configurable
        @Configurable.Synchronized
        @Configurable.Comment("Modifying any of these will require restarting the game.")
        public AdvancedStats FACEHUGGER_STATS = new AdvancedStats(
            HealthConstants.FACEHUGGER_HEALTH,
            0,
            0,
            KnockbackResistanceConstants.FACEHUGGER_KNOCKBACK_RESISTANCE,
            MoveSpeedConstants.FACEHUGGER_SPEED,
            0f,
            0f,
            0,
            FollowRangeConstants.FACEHUGGER_FOLLOW_RANGE
        );

        @Configurable
        @Configurable.Synchronized
        @Configurable.Comment("Modifying any of these will require restarting the game.")
        public AdvancedStats CHESTBURSTER_STATS = new AdvancedStats(
            HealthConstants.CHESTBURSTER_HEALTH,
            AttackDamageConstants.CHESTBURSTER_ATTACK_DAMAGE,
            HealthRegenConstants.CHESTBURSTER_HEALTH_REGEN,
            KnockbackResistanceConstants.CHESTBURSTER_KNOCKBACK_RESISTANCE,
            MoveSpeedConstants.CHESTBURSTER_SPEED,
            0.0F,
            0.0f,
            750,
            0
        );

        @Configurable
        @Configurable.Synchronized
        @Configurable.Comment("Modifying any of these will require restarting the game.")
        public AdvancedStats DRONE_STATS = new AdvancedStats(
            HealthConstants.DRONE_HEALTH,
            AttackDamageConstants.DRONE_ATTACK_DAMAGE,
            HealthRegenConstants.DRONE_HEALTH_REGEN,
            KnockbackResistanceConstants.DRONE_KNOCKBACK_RESISTANCE,
            MoveSpeedConstants.DRONE_SPEED,
            ArmorConstants.DRONE_ARMOR,
            0.0f,
            20,
            FollowRangeConstants.DRONE_FOLLOW_RANGE
        );

        @Configurable
        @Configurable.Synchronized
        @Configurable.Comment("Modifying any of these will require restarting the game.")
        public AdvancedStats OVAMORPH_STATS = new AdvancedStats(
            HealthConstants.OVAMORPH_HEALTH,
            0,
            HealthRegenConstants.OVAMORPH_HEALTH_REGEN,
            KnockbackResistanceConstants.OVAMORPH_KNOCKBACK_RESISTANCE,
            0,
            0,
            0,
            0,
            0
        );

        @Configurable
        @Configurable.Synchronized
        @Configurable.Comment("Modifying any of these will require restarting the game.")
        public AdvancedStats PRAETORIAN_STATS = new AdvancedStats(
            HealthConstants.PRAETORIAN_HEALTH,
            AttackDamageConstants.PRAETORIAN_ATTACK_DAMAGE,
            HealthRegenConstants.PRAETORIAN_HEALTH_REGEN,
            KnockbackResistanceConstants.PRAETORIAN_KNOCKBACK_RESISTANCE,
            MoveSpeedConstants.PRAETORIAN_SPEED,
            ArmorConstants.PRAETORIAN_ARMOR,
            ArmorToughnessConstants.PRAETORIAN_ARMOR_TOUGHNESS,
            80,
            FollowRangeConstants.PRAETORIAN_FOLLOW_RANGE
        );

        @Configurable
        @Configurable.Synchronized
        @Configurable.Comment("Modifying any of these will require restarting the game.")
        public AdvancedStats QUEEN_STATS = new AdvancedStats(
            HealthConstants.QUEEN_HEALTH,
            AttackDamageConstants.QUEEN_ATTACK_DAMAGE,
            HealthRegenConstants.QUEEN_HEALTH_REGEN,
            KnockbackResistanceConstants.QUEEN_KNOCKBACK_RESISTANCE,
            MoveSpeedConstants.QUEEN_SPEED,
            ArmorConstants.QUEEN_ARMOR,
            ArmorToughnessConstants.QUEEN_ARMOR_TOUGHNESS,
            160,
            FollowRangeConstants.QUEEN_FOLLOW_RANGE
        );

        @Configurable
        @Configurable.Synchronized
        @Configurable.Comment("Modifying any of these will require restarting the game.")
        public AdvancedStats WARRIOR_STATS = new AdvancedStats(
            HealthConstants.WARRIOR_HEALTH,
            AttackDamageConstants.WARRIOR_ATTACK_DAMAGE,
            HealthRegenConstants.WARRIOR_HEALTH_REGEN,
            KnockbackResistanceConstants.WARRIOR_KNOCKBACK_RESISTANCE,
            MoveSpeedConstants.WARRIOR_SPEED,
            ArmorConstants.WARRIOR_ARMOR,
            0.0f,
            40,
            FollowRangeConstants.WARRIOR_FOLLOW_RANGE
        );

        @Configurable
        @Configurable.Synchronized
        @Configurable.Comment("Modifying any of these will require restarting the game.")
        public AdvancedStats YAUTJA_STATS = new AdvancedStats(
            HealthConstants.YAUTJA_HEALTH,
            AttackDamageConstants.YAUTJA_ATTACK_DAMAGE,
            0.0f,
            KnockbackResistanceConstants.YAUTJA_KNOCKBACK_RESISTANCE,
            MoveSpeedConstants.YAUTJA_SPEED,
            ArmorConstants.YAUTJA_ARMOR,
            ArmorToughnessConstants.YAUTJA_ARMOR_TOUGHNESS,
            0,
            FollowRangeConstants.YAUTJA_FOLLOW_RANGE
        );

        @Configurable
        @Configurable.Synchronized
        @Configurable.Comment("Modifying any of these will require restarting the game.")
        public AdvancedStats MARINE_STATS = new AdvancedStats(
            HealthConstants.PLAYER_HEALTH,
            AttackDamageConstants.MARINE_ATTACK_DAMAGE,
            0.0f,
            0.0f,
            MoveSpeedConstants.PLAYER_SPRINT_JUMP_SPEED,
            ArmorConstants.MARINE_ARMOR,
            0.0f,
            0,
            FollowRangeConstants.MARINE_FOLLOW_RANGE
        );

        public static class AdvancedStats {

            @Configurable
            @Configurable.Synchronized
            @Configurable.Comment("The entity's health.")
            public float health;

            @Configurable
            @Configurable.Synchronized
            @Configurable.Comment("The entity's attack damage.")
            public float attackDamage;

            @Configurable
            @Configurable.Synchronized
            @Configurable.Comment("The amount of health the entity regenerates per second.")
            public float healthRegenPerSecond;

            @Configurable
            @Configurable.Synchronized
            @Configurable.Comment("The entity's knockback resistance.")
            public float knockbackResistance;

            @Configurable
            @Configurable.Synchronized
            @Configurable.Comment("The entity's movement speed.")
            public float moveSpeed;

            @Configurable
            @Configurable.Synchronized
            @Configurable.Comment("The entity's armor value.")
            public float armor;

            @Configurable
            @Configurable.Synchronized
            @Configurable.Comment("The entity's armor toughness.")
            public float armorToughness;

            @Configurable
            @Configurable.Synchronized
            @Configurable.Comment("The entity's nest tick rate.")
            public int nestTickrate;

            @Configurable
            @Configurable.Synchronized
            @Configurable.Comment("The entity's max follow range.")
            public float followRange;

            public AdvancedStats(
                float health,
                float attackDamage,
                float healthRegenPerSecond,
                float knockbackResistance,
                float moveSpeed,
                float armor,
                float armorToughness,
                int nestTickrate,
                float followRange
            ) {
                this.health = health;
                this.attackDamage = attackDamage;
                this.healthRegenPerSecond = healthRegenPerSecond;
                this.knockbackResistance = knockbackResistance;
                this.moveSpeed = moveSpeed;
                this.armor = armor;
                this.nestTickrate = nestTickrate;
                this.armorToughness = armorToughness;
                this.followRange = followRange;
            }
        }
    }

    @Configurable
    @Configurable.Synchronized
    @Configurable.Comment("Controls the Hive Settings")
    public HiveConfigs hiveConfigs = new HiveConfigs();

    public static class HiveConfigs {

        @Configurable
        @Configurable.Synchronized
        @Configurable.Comment(
            {
                "The minimum distance between hive centers in blocks. This controls how far apart hives are.",
                "If this value is less than 2x the hive radius, hives will begin to overlap.",
                "If this value is more than 2x the hive radius, then there will be buffer zones between hives where no hives will form." }
        )
        public int MINIMUM_DISTANCE_BETWEEN_HIVES_IN_BLOCKS = 1024;

        @Configurable
        @Configurable.Synchronized
        @Configurable.Comment(
            {
                "The minimum distance between hive centers in blocks. This controls how far apart hives are.",
                "If this value is less than 2x the hive radius, hives will begin to overlap.",
                "If this value is more than 2x the hive radius, then there will be buffer zones between hives where no hives will form." }
        )
        public int HIVE_RADIUS_IN_BLOCKS = 64;

        @Configurable
        @Configurable.Synchronized
        @Configurable.Comment("The maximum distance away from a hive that xenomorphs are allowed to join or remain as a member of a hive.")
        public int HIVE_LEASH_RADIUS_IN_BLOCKS = 98;

        @Configurable
        @Configurable.Synchronized
        @Configurable.Comment("The maximum number of praetorians allowed within a hive.")
        public int HIVE_MAX_PRAETORIAN_COUNT = 6;

        @Configurable
        @Configurable.Synchronized
        @Configurable.Comment(
            {
                "The number of hive members required for a single praetorian to appear.",
                "For example, if set to 8, then there will be a praetorian for every 8 hive members."
            }
        )
        public int HIVE_MEMBERS_REQUIRED_FOR_PRAETORIAN = 8;

        @Configurable
        @Configurable.Synchronized
        @Configurable.Comment("Determines if the screen should darken when the hive boss bar appears.")
        public boolean HIVE_DARKEN_SCREEN = true;

        @Configurable
        @Configurable.Synchronized
        @Configurable.Comment("Enables hive debugging.")
        public boolean HIVE_DEBUG_ENABLED = false;

        @Configurable
        @Configurable.Synchronized
        @Configurable.Comment("Requires hive debugging to be enabled.")
        public boolean HIVE_DEBUG_HIGHLIGHT_LEADER = true;

        @Configurable
        @Configurable.Synchronized
        @Configurable.Comment("Requires hive debugging to be enabled.")
        public boolean HIVE_DEBUG_HIGHLIGHT_ALL_MEMBERS = false;

        @Configurable
        @Configurable.Synchronized
        @Configurable.Comment("Requires hive debugging to be enabled.")
        public boolean HIVE_DEBUG_MARK_HIVE_CENTER = true;

        @Configurable
        @Configurable.Synchronized
        public int CHESTBURSTER_MAX_GROWTH_TIMER_SECONDS = 1200;

        @Configurable
        @Configurable.Synchronized
        public int DRONE_MAX_GROWTH_TIMER_SECONDS = 800;

        @Configurable
        @Configurable.Synchronized
        public int WARRIOR_MAX_GROWTH_TIMER_SECONDS = 1600;

        @Configurable
        @Configurable.Synchronized
        public int PRAETORIAN_MAX_GROWTH_TIMER_SECONDS = 3200;

        @Configurable
        @Configurable.Synchronized
        public int PRAETORIAN_SHORTCUT_TIMER_SECONDS = 600;
    }

    @Configurable
    @Configurable.Synchronized
    public WeaponConfigs weaponConfigs = new WeaponConfigs();

    public static class WeaponConfigs {

        @Configurable
        @Configurable.Synchronized
        @Configurable.Comment("If enabled, bullets from guns will damage blocks.")
        public boolean BULLETS_DAMAGE_BLOCKS_ENABLED = true;

        @Configurable
        @Configurable.Synchronized
        @Configurable.Comment("If enabled, nukes will explode.")
        public boolean ENABLE_NUKE_BLOCK_MECHS = false;

        @Configurable
        @Configurable.Synchronized
        @Configurable.Comment("FOV range that turrets can target")
        public int TURRET_FOV = 45;

        @Configurable
        @Configurable.Synchronized
        @Configurable.Comment("Block range that turrets can target")
        public int TURRET_RANGE = 32;

        @Configurable
        @Configurable.Synchronized
        @Configurable.Comment("Turret damage value")
        public float TURRET_DAMAGE = 4F;

        @Configurable
        @Configurable.Synchronized
        @Configurable.Comment("Block radius that a turret looks for an ammo chest")
        public int TURRET_AMMOCHEST_SEARCH_RANGE = 5;

        @Configurable
        @Configurable.Synchronized
        @Configurable.Comment("Block radius that a trip mine looks for a living entity")
        public double TRIP_MINE_SEARCH_RADIUS = 2;
    }
}
