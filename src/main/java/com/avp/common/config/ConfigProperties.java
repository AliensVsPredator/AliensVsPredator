package com.avp.common.config;

import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import com.avp.common.config.property.ConfigPropertyKey;
import com.avp.common.config.property.ConfigPropertyRegistry;
import com.avp.common.config.property.deserializer.ConfigPropertyDeserializer;
import com.avp.common.config.property.deserializer.ConfigPropertyDeserializers;
import com.avp.common.config.property.serializer.ConfigPropertySerializer;
import com.avp.common.config.property.serializer.ConfigPropertySerializers;

public class ConfigProperties {

    private static final Map<String, ConfigPropertyKey<?>> CONFIG_PROPERTY_KEYS_BY_NAME = new HashMap<>();

    @SuppressWarnings("unchecked")
    public static @Nullable <T> ConfigPropertyKey<T> keyOrNull(String identifier) {
        return (ConfigPropertyKey<T>) CONFIG_PROPERTY_KEYS_BY_NAME.get(identifier);
    }

    public static Collection<ConfigPropertyKey<?>> values() {
        return Collections.unmodifiableCollection(CONFIG_PROPERTY_KEYS_BY_NAME.values());
    }

    public static final ConfigPropertyKey<Float> ACID_ATTACK_DAMAGE = registerFloat("acid.stats.attack.damage");

    public static final ConfigPropertyKey<Boolean> ADULT_SPAWNING_ENABLED = registerBoolean("spawning.adult.enabled");

    public static final ConfigPropertyKey<Boolean> ALIEN_CUSTOM_MOB_CATEGORY_ENABLED = registerBoolean(
        "spawning.custom_mob_category.alien.enabled"
    );

    public static final ConfigPropertyKey<Integer> ALIEN_CUSTOM_MOB_CATEGORY_SPAWN_LIMIT = registerInt(
        "spawning.custom_mob_category.alien.limit"
    );

    public static final ConfigPropertyKey<Boolean> BULLETS_DAMAGE_BLOCKS_ENABLED = registerBoolean("bullets_damage_blocks.enabled");

    public static final ConfigMobAttributesContainer CHESTBURSTER_ATTRIBUTES = registerAttributes("chestburster");

    public static final ConfigAlienSpawningContainer CHESTBURSTER_SPAWNING = registerAlienSpawning("chestburster");

    public static final ConfigMobAttributesContainer DRONE_ATTRIBUTES = registerAttributes("drone");

    public static final ConfigAlienSpawningContainer DRONE_SPAWNING = registerAlienSpawning("drone");

    public static final ConfigPropertyKey<Boolean> HIVE_DEBUG_ENABLED = registerBoolean("hive.debug.enabled");

    public static final ConfigPropertyKey<Boolean> HIVE_DEBUG_HIGHLIGHT_LEADER = registerBoolean("hive.debug.highlight_leader");

    public static final ConfigPropertyKey<Boolean> HIVE_DEBUG_HIGHLIGHT_ALL_MEMBERS = registerBoolean(
        "hive.debug.highlight_all_members"
    );

    public static final ConfigPropertyKey<Boolean> HIVE_DEBUG_MARK_HIVE_CENTER = registerBoolean("hive.debug.mark_hive_center");

    public static final ConfigPropertyKey<Integer> HIVE_LEASH_RADIUS_IN_BLOCKS = registerInt("hive.leash_radius_in_blocks");

    public static final ConfigPropertyKey<Integer> HIVE_MAX_PRAETORIAN_COUNT = registerInt("hive.max_praetorian_count");

    public static final ConfigPropertyKey<Integer> HIVE_MEMBERS_REQUIRED_FOR_PRAETORIAN = registerInt(
        "hive.member_count_required_for_praetorian"
    );

    public static final ConfigPropertyKey<Integer> HIVE_RADIUS_IN_BLOCKS = registerInt("hive_radius_in_blocks");

    public static final ConfigPropertyKey<Boolean> HIVE_DARKEN_SCREEN = registerBoolean("hive_darken_screen");

    public static final ConfigPropertyKey<Integer> MINIMUM_DISTANCE_BETWEEN_HIVES_IN_BLOCKS = registerInt(
        "minimum_distance_between_hives_in_blocks"
    );

    public static final ConfigPropertyKey<Boolean> NATURAL_SPAWNING_ENABLED = registerBoolean("spawning.natural.enabled");

    public static final ConfigAlienSpawningContainer NETHER_CHESTBURSTER_SPAWNING = registerAlienSpawning("nether_chestburster");

    public static final ConfigAlienSpawningContainer NETHER_DRONE_SPAWNING = registerAlienSpawning("nether_drone");

    public static final ConfigAlienSpawningContainer NETHER_OVAMORPH_SPAWNING = registerAlienSpawning("nether_ovamorph");

    public static final ConfigAlienSpawningContainer NETHER_PRAETORIAN_SPAWNING = registerAlienSpawning("nether_praetorian");

    public static final ConfigAlienSpawningContainer NETHER_WARRIOR_SPAWNING = registerAlienSpawning("nether_warrior");

    public static final ConfigMobAttributesContainer OVAMORPH_ATTRIBUTES = registerAttributes("ovamorph");

    public static final ConfigAlienSpawningContainer OVAMORPH_SPAWNING = registerAlienSpawning("ovamorph");

    public static final ConfigMobAttributesContainer PRAETORIAN_ATTRIBUTES = registerAttributes("praetorian");

    public static final ConfigAlienSpawningContainer PRAETORIAN_SPAWNING = registerAlienSpawning("praetorian");

    public static final ConfigMobAttributesContainer QUEEN_ATTRIBUTES = registerAttributes("queen");

    public static final ConfigAlienSpawningContainer QUEEN_SPAWNING = registerAlienSpawning("queen");

    public static final ConfigPropertyKey<Boolean> REMOVE_VANILLA_SPAWNS = registerBoolean("spawning.remove_vanilla_spawns");

    public static final ConfigMobAttributesContainer WARRIOR_ATTRIBUTES = registerAttributes("warrior");

    public static final ConfigAlienSpawningContainer WARRIOR_SPAWNING = registerAlienSpawning("warrior");

    public static final ConfigMobAttributesContainer YAUTJA_ATTRIBUTES = registerAttributes("yautja");

    public static final ConfigMobSpawningContainer YAUTJA_SPAWNING = registerMobSpawning("yautja");

    public static final ConfigPropertyKey<Boolean> YOUNG_SPAWNING_ENABLED = registerBoolean("spawning.young.enabled");

    private static ConfigMobAttributesContainer registerAttributes(String entityName) {
        var subpath = entityName + ".stats.";

        return new ConfigMobAttributesContainer(
            registerDouble(subpath + "armor"),
            registerDouble(subpath + "armor_toughness"),
            registerDouble(subpath + "attack_damage"),
            registerDouble(subpath + "follow_range"),
            registerDouble(subpath + "health"),
            registerFloat(subpath + "health_regen_per_second"),
            registerDouble(subpath + "knockback_resistance"),
            registerDouble(subpath + "move_speed")
        );
    }

    private static ConfigMobSpawningContainer registerMobSpawning(String entityName) {
        var subpath = entityName + ".spawn.";

        return new ConfigMobSpawningContainer(
            registerBoolean(subpath + "enabled"),
            registerInt(subpath + "max_group_size"),
            registerInt(subpath + "max_y"),
            registerInt(subpath + "min_group_size"),
            registerInt(subpath + "min_y"),
            registerInt(subpath + "weight")
        );
    }

    private static ConfigAlienSpawningContainer registerAlienSpawning(String entityName) {
        var subpath = entityName + ".spawn.";

        return new ConfigAlienSpawningContainer(
            registerMobSpawning(entityName),
            registerBoolean(subpath + "requires_resin")
        );
    }

    private static ConfigPropertyKey<Boolean> registerBoolean(String id) {
        return register(id, ConfigPropertyDeserializers.BOOLEAN, ConfigPropertySerializers.BOOLEAN);
    }

    private static ConfigPropertyKey<Double> registerDouble(String id) {
        return register(id, ConfigPropertyDeserializers.DOUBLE, ConfigPropertySerializers.DOUBLE);
    }

    private static ConfigPropertyKey<Float> registerFloat(String id) {
        return register(id, ConfigPropertyDeserializers.FLOAT, ConfigPropertySerializers.FLOAT);
    }

    private static ConfigPropertyKey<Integer> registerInt(String id) {
        return register(id, ConfigPropertyDeserializers.INT, ConfigPropertySerializers.INT);
    }

    private static <T> ConfigPropertyKey<T> register(
        String id,
        ConfigPropertyDeserializer<T> deserializer,
        ConfigPropertySerializer<T> serializer
    ) {
        var key = new ConfigPropertyKey<T>(id);

        CONFIG_PROPERTY_KEYS_BY_NAME.put(id, key);

        return ConfigPropertyRegistry.instance().register(key, deserializer, serializer);
    }

    public static void initialize() {}
}
