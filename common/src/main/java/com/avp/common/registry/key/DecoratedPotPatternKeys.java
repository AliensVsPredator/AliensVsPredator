package com.avp.common.registry.key;

import com.alien.common.registry.init.AlienItems;
import com.just.core.functional.function.Lazy;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.entity.DecoratedPotPattern;

import java.util.Map;

import com.avp.AVPResources;

public class DecoratedPotPatternKeys {

    public static final ResourceKey<DecoratedPotPattern> OVOID = create("ovoid_pottery_pattern");

    public static final ResourceKey<DecoratedPotPattern> PARASITE = create("parasite_pottery_pattern");

    public static final ResourceKey<DecoratedPotPattern> ROYALTY = create("royalty_pottery_pattern");

    public static final ResourceKey<DecoratedPotPattern> VECTOR = create("vector_pottery_pattern");

    public static final Lazy<Map<Item, ResourceKey<DecoratedPotPattern>>> ITEM_TO_POT_TEXTURE = Lazy.of(
        () -> Map.ofEntries(
            Map.entry(AlienItems.OVOID_POTTERY_SHERD.get(), OVOID),
            Map.entry(AlienItems.PARASITE_POTTERY_SHERD.get(), PARASITE),
            Map.entry(AlienItems.ROYALTY_POTTERY_SHERD.get(), ROYALTY),
            Map.entry(AlienItems.VECTOR_POTTERY_SHERD.get(), VECTOR)
        )
    );

    private static ResourceKey<DecoratedPotPattern> create(String id) {
        return ResourceKey.create(Registries.DECORATED_POT_PATTERN, AVPResources.location(id));
    }
}
