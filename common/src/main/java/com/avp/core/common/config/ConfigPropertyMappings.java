package com.avp.core.common.config;

import com.avp.core.common.util.LazySupplier;
import net.minecraft.world.entity.EntityType;

import java.util.Map;

import com.avp.core.common.entity.type.AVPEntityTypes;

public class ConfigPropertyMappings {

    public static final LazySupplier<Map<EntityType<?>, ConfigMobAttributesContainer>> BY_ENTITY_TYPE = new LazySupplier<>(() -> Map.ofEntries(
        Map.entry(AVPEntityTypes.DRONE.get(), ConfigProperties.DRONE_ATTRIBUTES),
        Map.entry(AVPEntityTypes.OVAMORPH.get(), ConfigProperties.OVAMORPH_ATTRIBUTES),
        Map.entry(AVPEntityTypes.PRAETORIAN.get(), ConfigProperties.PRAETORIAN_ATTRIBUTES),
        Map.entry(AVPEntityTypes.QUEEN.get(), ConfigProperties.QUEEN_ATTRIBUTES),
        Map.entry(AVPEntityTypes.WARRIOR.get(), ConfigProperties.WARRIOR_ATTRIBUTES),
        Map.entry(AVPEntityTypes.YAUTJA.get(), ConfigProperties.YAUTJA_ATTRIBUTES)
    ));
}
