package com.avp.common.registry.tag;

import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;

import com.avp.AVPResources;

public class AVPEntityTypeTags {

    public static final TagKey<EntityType<?>> ABERRANT_ALIENS = create("aberrant_aliens");

    public static final TagKey<EntityType<?>> ACID_IMMUNE = create("acid_immune");

    public static final TagKey<EntityType<?>> ALIENS = create("aliens");

    public static final TagKey<EntityType<?>> ANIMALS = create("animals");

    public static final TagKey<EntityType<?>> CHESTBURSTERS = create("chestbursters");

    public static final TagKey<EntityType<?>> CRUSHERS = create("crushers");

    public static final TagKey<EntityType<?>> DRONES = create("drones");

    public static final TagKey<EntityType<?>> FACEHUGGERS = create("facehuggers");

    public static final TagKey<EntityType<?>> HATED_BY_XENOMORPHS = create("hated_by_xenomorphs");

    public static final TagKey<EntityType<?>> HIVE_ALIENS = create("hive_aliens");

    public static final TagKey<EntityType<?>> HOSTS = create("hosts");

    public static final TagKey<EntityType<?>> HUMANOIDS = create("humanoids");

    public static final TagKey<EntityType<?>> IRRADIATED_ALIENS = create("irradiated_aliens");

    public static final TagKey<EntityType<?>> NETHER_ALIENS = create("nether_aliens");

    public static final TagKey<EntityType<?>> NETHER_CREATURES = create("nether_creatures");

    public static final TagKey<EntityType<?>> NORMAL_ALIENS = create("normal_aliens");

    public static final TagKey<EntityType<?>> OVOMORPHS = create("ovomorphs");

    public static final TagKey<EntityType<?>> PARASITES = create("parasites");

    public static final TagKey<EntityType<?>> PRAETORIANS = create("praetorians");

    public static final TagKey<EntityType<?>> PREDATORS = create("predators");

    public static final TagKey<EntityType<?>> PROWLERS = create("prowlers");

    public static final TagKey<EntityType<?>> QUEENS = create("queens");

    public static final TagKey<EntityType<?>> RADIATION_RESISTANT = create("radiation_resistant");

    public static final TagKey<EntityType<?>> ROYAL_ALIENS = create("royal_aliens");

    public static final TagKey<EntityType<?>> ROYAL_XENOMORPHS = create("royal_xenomorphs");

    public static final TagKey<EntityType<?>> RUNNER_HOSTS = create("runner_hosts");

    public static final TagKey<EntityType<?>> RUNNERS = create("runners");

    public static final TagKey<EntityType<?>> SPAWNS_IN_HIVE_DRONE_LAYER = create("spawns_in_hive_drone_layer");

    public static final TagKey<EntityType<?>> SPAWNS_IN_HIVE_PRAETORIAN_LAYER = create("spawns_in_hive_praetorian_layer");

    public static final TagKey<EntityType<?>> SPAWNS_IN_HIVE_QUEEN_LAYER = create("spawns_in_hive_queen_layer");

    public static final TagKey<EntityType<?>> SPAWNS_IN_HIVE_WARRIOR_LAYER = create("spawns_in_hive_warrior_layer");

    public static final TagKey<EntityType<?>> WARRIORS = create("warriors");

    public static final TagKey<EntityType<?>> XENOMORPHS = create("xenomorphs");

    private static TagKey<EntityType<?>> create(String name) {
        return TagKey.create(Registries.ENTITY_TYPE, AVPResources.location(name));
    }
}
