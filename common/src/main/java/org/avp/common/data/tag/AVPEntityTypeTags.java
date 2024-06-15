package org.avp.common.data.tag;

import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;

import org.avp.common.AVPResources;

public class AVPEntityTypeTags {

    public static final TagKey<EntityType<?>> ACID_BLEEDERS = create("acid_bleeders");

    public static final TagKey<EntityType<?>> ACID_IMMUNE = create("acid_immune");

    public static final TagKey<EntityType<?>> ALIENS = create("aliens");

    public static final TagKey<EntityType<?>> EGGS = create("eggs");

    public static final TagKey<EntityType<?>> ENGINEERS = create("engineers");

    public static final TagKey<EntityType<?>> HEAVY_GUNS_IMMUNE = create("heavy_guns_immune");

    public static final TagKey<EntityType<?>> HIVE_ALIENS = create("hive_aliens");

    public static final TagKey<EntityType<?>> MEDIUM_GUNS_IMMUNE = create("medium_guns_immune");

    public static final TagKey<EntityType<?>> MONSTERS = create("monsters");

    public static final TagKey<EntityType<?>> NON_HOSTS = create("non_hosts");

    public static final TagKey<EntityType<?>> NOT_WORTH_KILLING = create("not_worth_killing");

    public static final TagKey<EntityType<?>> PARASITES = create("parasites");

    public static final TagKey<EntityType<?>> PREDATORS = create("predators");

    public static final TagKey<EntityType<?>> PRODUCES_RESIN = create("produces_resin");

    public static final TagKey<EntityType<?>> ROYAL_ALIENS = create("royal_aliens");

    public static final TagKey<EntityType<?>> SMALL_GUNS_IMMUNE = create("small_guns_immune");

    public static final TagKey<EntityType<?>> UBER_GUNS_IMMUNE = create("uber_guns_immune");

    private static TagKey<EntityType<?>> create(String registryName) {
        return TagKey.create(Registries.ENTITY_TYPE, AVPResources.location(registryName));
    }

    private AVPEntityTypeTags() {
        throw new UnsupportedOperationException();
    }
}
