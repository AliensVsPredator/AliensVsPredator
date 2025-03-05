package com.avp.common.config.template;

import com.avp.common.config.ConfigProperties;

public class ConfigTemplates {

    public static final ConfigTemplate HIVES = ConfigTemplateBuilder.create()
        // Block breaking properties
        .comment("The minimum distance between hive centers in blocks. This controls how far apart hives are.")
        .comment("If this value is less than 2x the hive radius, hives will begin to overlap.")
        .comment("If this value is more than 2x the hive radius, then there will be buffer zones between hives where no hives will form.")
        .property(ConfigProperties.MINIMUM_DISTANCE_BETWEEN_HIVES_IN_BLOCKS)
        .newLine()
        .comment("The radius of hives in blocks. This controls how large hives can grow.")
        .comment("If this value is more than 0.5x the distance between hives, hives will begin to overlap.")
        .comment(
            "If this value is less than 0.5x the distance between hives, then there will be buffer zones between hives where no hives will form."
        )
        .property(ConfigProperties.HIVE_RADIUS_IN_BLOCKS)
        .newLine()
        .comment("The maximum distance away from a hive that xenomorphs are allowed to join or remain as a member of a hive.")
        .property(ConfigProperties.HIVE_LEASH_RADIUS_IN_BLOCKS)
        .newLine()
        .comment("The maximum number of praetorians allowed within a hive.")
        .property(ConfigProperties.HIVE_MAX_PRAETORIAN_COUNT)
        .newLine()
        .comment("The number of hive members required for a single praetorian to appear.")
        .comment("For example, if set to 8, then there will be a praetorian for every 8 hive members.")
        .property(ConfigProperties.HIVE_MEMBERS_REQUIRED_FOR_PRAETORIAN)
        .newLine()
        .comment("Determines if the screen should darken when the hive boss bar appears.")
        .property(ConfigProperties.HIVE_DARKEN_SCREEN)
        .newLine()
        .comment("Enables hive debugging.")
        .property(ConfigProperties.HIVE_DEBUG_ENABLED)
        .newLine()
        .comment("Applies a glow effect to the hive leader. Requires hive debugging to be enabled.")
        .property(ConfigProperties.HIVE_DEBUG_HIGHLIGHT_LEADER)
        .newLine()
        .comment("Applies a glow effect to all hive members. Requires hive debugging to be enabled.")
        .property(ConfigProperties.HIVE_DEBUG_HIGHLIGHT_ALL_MEMBERS)
        .newLine()
        .comment("Marks the hive center with a special block. Requires hive debugging to be enabled.")
        .property(ConfigProperties.HIVE_DEBUG_MARK_HIVE_CENTER)
        .build();

    public static final ConfigTemplate SPAWNING = ConfigTemplateBuilder.create()
        .comment(
            "If set to true, xenomorphs will spawn in the overworld naturally along with other monsters. Modifying this requires restarting the game."
        )
        .property(ConfigProperties.NATURAL_SPAWNING_ENABLED)
        .newLine()
        .comment(
            "If set to true, *ADULT* xenomorphs will spawn in the overworld naturally along with other monsters. Modifying this requires restarting the game."
        )
        .property(ConfigProperties.ADULT_SPAWNING_ENABLED)
        .newLine()
        .comment(
            "If set to true, *YOUNG* xenomorphs (eggs, facehuggers, bursters, etc.) will spawn in the overworld naturally along with other monsters. Modifying this requires restarting the game."
        )
        .property(ConfigProperties.YOUNG_SPAWNING_ENABLED)
        .newLine()
        .comment(
            "If set to true, certain hostile monster spawns will be removed. This will allow other hostile monsters (xenomorphs, for example) to spawn more frequently. Modifying this requires restarting the game."
        )
        .comment(
            "The list of mobs that have their spawns removed can be viewed (and modified) with the avp:tags/entity/remove_vanilla_spawns tag."
        )
        .comment(
            "ENABLING THIS MAY BREAK CERTAIN FARMS THAT RELY ON MOB SPAWNS. Enable at your own risk, you have been warned!"
        )
        .property(ConfigProperties.REMOVE_VANILLA_SPAWNS)
        .newLine()
        .comment(
            "If set to true, aliens will have a separate spawn cap from hostile monsters. Modifying this requires restarting the game."
        )
        .comment("THIS MAY BREAK YOUR GAME IF YOU HAVE OTHER MODS INSTALLED. Enable at your own risk, you have been warned!")
        .property(ConfigProperties.ALIEN_CUSTOM_MOB_CATEGORY_ENABLED)
        .newLine()
        .comment("The maximum spawn count for aliens within the custom alien mob category. Modifying this requires restarting the game.")
        .comment("Changing this value will only work if the custom mob category for aliens is enabled.")
        .property(ConfigProperties.ALIEN_CUSTOM_MOB_CATEGORY_SPAWN_LIMIT)
        .newLine()
        .comment("Chestburster spawn values. Modifying any of these will require restarting the game.")
        .properties(ConfigProperties.CHESTBURSTER_SPAWNING)
        .newLine()
        .comment("Drone spawn values. Modifying any of these will require restarting the game.")
        .properties(ConfigProperties.DRONE_SPAWNING)
        .newLine()
        .comment("Nether Chestburster spawn values. Modifying any of these will require restarting the game.")
        .properties(ConfigProperties.NETHER_CHESTBURSTER_SPAWNING)
        .newLine()
        .comment("Nether Drone spawn values. Modifying any of these will require restarting the game.")
        .properties(ConfigProperties.NETHER_DRONE_SPAWNING)
        .newLine()
        .comment("Nether Ovamorph spawn values. Modifying any of these will require restarting the game.")
        .properties(ConfigProperties.NETHER_OVAMORPH_SPAWNING)
        .newLine()
        .comment("Nether Praetorian spawn values. Modifying any of these will require restarting the game.")
        .properties(ConfigProperties.NETHER_PRAETORIAN_SPAWNING)
        .newLine()
        .comment("Nether Warrior spawn values. Modifying any of these will require restarting the game.")
        .properties(ConfigProperties.NETHER_WARRIOR_SPAWNING)
        .newLine()
        .comment("Ovamorph spawn values. Modifying any of these will require restarting the game.")
        .properties(ConfigProperties.OVAMORPH_SPAWNING)
        .newLine()
        .comment("Praetorian spawn values. Modifying any of these will require restarting the game.")
        .properties(ConfigProperties.PRAETORIAN_SPAWNING)
        .newLine()
        .comment("Queen spawn values. Modifying any of these will require restarting the game.")
        .properties(ConfigProperties.QUEEN_SPAWNING)
        .newLine()
        .comment("Warrior spawn values. Modifying any of these will require restarting the game.")
        .properties(ConfigProperties.WARRIOR_SPAWNING)
        .newLine()
        .comment("Yautja spawn values. Modifying any of these will require restarting the game.")
        .properties(ConfigProperties.YAUTJA_SPAWNING)
        .build();

    public static final ConfigTemplate STATS = ConfigTemplateBuilder.create()
        // Acid properties
        .comment("Acid stats.")
        .property(ConfigProperties.ACID_ATTACK_DAMAGE)
        .newLine()
        .comment("Chestburster stats. Modifying any of these will require restarting the game.")
        .properties(ConfigProperties.CHESTBURSTER_ATTRIBUTES)
        .newLine()
        .comment("Drone stats. Modifying any of these will require restarting the game.")
        .properties(ConfigProperties.DRONE_ATTRIBUTES)
        .newLine()
        .comment("Ovamorph stats. Modifying any of these will require restarting the game.")
        .properties(ConfigProperties.OVAMORPH_ATTRIBUTES)
        .newLine()
        .comment("Praetorian stats. Modifying any of these will require restarting the game.")
        .properties(ConfigProperties.PRAETORIAN_ATTRIBUTES)
        .newLine()
        .comment("Queen stats. Modifying any of these will require restarting the game.")
        .properties(ConfigProperties.QUEEN_ATTRIBUTES)
        .newLine()
        .comment("Warrior stats. Modifying any of these will require restarting the game.")
        .properties(ConfigProperties.WARRIOR_ATTRIBUTES)
        .newLine()
        .comment("Yautja stats. Modifying any of these will require restarting the game.")
        .properties(ConfigProperties.YAUTJA_ATTRIBUTES)
        .build();

    public static final ConfigTemplate WEAPONS = ConfigTemplateBuilder.create()
        // Block breaking properties
        .comment("If enabled, bullets from guns will damage blocks.")
        .property(ConfigProperties.BULLETS_DAMAGE_BLOCKS_ENABLED)
        .build();
}
