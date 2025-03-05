package com.avp.common.entity;

import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;

import com.avp.AVPResources;

public class AVPEntityTypeTags {

    public static final TagKey<EntityType<?>> ABERRANT_ALIENS = create("aberrant_aliens");

    public static final TagKey<EntityType<?>> ACID_IMMUNE = create("acid_immune");

    public static final TagKey<EntityType<?>> ALIENS = create("aliens");

    public static final TagKey<EntityType<?>> ANIMALS = create("animals");

    public static final TagKey<EntityType<?>> HIVE_ALIENS = create("hive_aliens");

    public static final TagKey<EntityType<?>> HOSTS = create("hosts");

    public static final TagKey<EntityType<?>> HUMANOIDS = create("humanoids");

    public static final TagKey<EntityType<?>> NETHER_ALIENS = create("nether_aliens");

    public static final TagKey<EntityType<?>> NETHER_CREATURES = create("nether_creatures");

    public static final TagKey<EntityType<?>> NORMAL_ALIENS = create("normal_aliens");

    public static final TagKey<EntityType<?>> PARASITES = create("parasites");

    public static final TagKey<EntityType<?>> REMOVE_VANILLA_SPAWNS = create("remove_vanilla_spawns");

    public static final TagKey<EntityType<?>> ROYAL_ALIENS = create("royal_aliens");

    public static final TagKey<EntityType<?>> XENOMORPHS = create("xenomorphs");

    private static TagKey<EntityType<?>> create(String name) {
        return TagKey.create(Registries.ENTITY_TYPE, AVPResources.location(name));
    }
}
