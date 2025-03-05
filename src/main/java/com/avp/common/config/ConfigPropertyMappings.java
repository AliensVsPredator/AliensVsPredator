package com.avp.common.config;

import net.minecraft.world.entity.EntityType;

import java.util.Map;

import com.avp.common.entity.type.AVPEntityTypes;

public class ConfigPropertyMappings {

    public static final Map<EntityType<?>, ConfigMobAttributesContainer> BY_ENTITY_TYPE = Map.ofEntries(
        Map.entry(AVPEntityTypes.DRONE, ConfigProperties.DRONE_ATTRIBUTES),
        Map.entry(AVPEntityTypes.OVAMORPH, ConfigProperties.OVAMORPH_ATTRIBUTES),
        Map.entry(AVPEntityTypes.PRAETORIAN, ConfigProperties.PRAETORIAN_ATTRIBUTES),
        Map.entry(AVPEntityTypes.QUEEN, ConfigProperties.QUEEN_ATTRIBUTES),
        Map.entry(AVPEntityTypes.WARRIOR, ConfigProperties.WARRIOR_ATTRIBUTES),
        Map.entry(AVPEntityTypes.YAUTJA, ConfigProperties.YAUTJA_ATTRIBUTES)
    );
}
