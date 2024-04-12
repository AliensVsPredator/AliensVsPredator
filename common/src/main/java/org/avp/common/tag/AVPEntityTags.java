package org.avp.common.tag;

import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;

import org.avp.common.AVPResources;

public class AVPEntityTags {

    public static final TagKey<EntityType<?>> ACID_BLEEDERS;

    public static final TagKey<EntityType<?>> ALIENS;

    public static final TagKey<EntityType<?>> EGGS;

    public static final TagKey<EntityType<?>> ENGINEERS;

    public static final TagKey<EntityType<?>> NON_HOSTS;

    public static final TagKey<EntityType<?>> NOT_WORTH_KILLING;

    public static final TagKey<EntityType<?>> PARASITES;

    public static final TagKey<EntityType<?>> PREDATORS;

    public static final TagKey<EntityType<?>> ROYAL_ALIENS;

    public static void forceInitialization() {
        // This method doesn't need to do anything
    }

    private static TagKey<EntityType<?>> create(String registryName) {
        return TagKey.create(Registries.ENTITY_TYPE, AVPResources.location(registryName));
    }

    static {
        ACID_BLEEDERS = create("acid_bleeders");
        ALIENS = create("aliens");
        EGGS = create("eggs");
        ENGINEERS = create("engineers");
        NOT_WORTH_KILLING = create("not_worth_killing");
        NON_HOSTS = create("non_hosts");
        PARASITES = create("parasites");
        PREDATORS = create("predators");
        ROYAL_ALIENS = create("royal_aliens");
    }

    private AVPEntityTags() {
        throw new UnsupportedOperationException();
    }
}
