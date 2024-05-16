package org.avp.common.tag;

import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;

import org.avp.common.AVPResources;

public class AVPEntityTags {

    public static final TagKey<EntityType<?>> ACID_BLEEDERS = create("acid_bleeders");

    public static final TagKey<EntityType<?>> ACID_IMMUNE = create("acid_immune");

    public static final TagKey<EntityType<?>> ALIENS = create("aliens");

    public static final TagKey<EntityType<?>> EGGS = create("eggs");

    public static final TagKey<EntityType<?>> ENGINEERS = create("engineers");

    public static final TagKey<EntityType<?>> MONSTERS = create("monsters");

    public static final TagKey<EntityType<?>> NON_HOSTS = create("non_hosts");

    public static final TagKey<EntityType<?>> NOT_WORTH_KILLING = create("not_worth_killing");

    public static final TagKey<EntityType<?>> PARASITES = create("parasites");

    public static final TagKey<EntityType<?>> PREDATORS = create("predators");

    public static final TagKey<EntityType<?>> ROYAL_ALIENS = create("royal_aliens");

    private static TagKey<EntityType<?>> create(String registryName) {
        return TagKey.create(Registries.ENTITY_TYPE, AVPResources.location(registryName));
    }

    private AVPEntityTags() {
        throw new UnsupportedOperationException();
    }
}
