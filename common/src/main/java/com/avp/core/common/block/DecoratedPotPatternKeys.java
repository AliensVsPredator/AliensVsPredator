package com.avp.core.common.block;

import com.avp.core.common.util.LazySupplier;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.entity.DecoratedPotPattern;

import java.util.Map;

import com.avp.core.AVPResources;
import com.avp.core.common.item.AVPItems;

public class DecoratedPotPatternKeys {

    public static final ResourceKey<DecoratedPotPattern> OVOID = create("ovoid_pottery_pattern");

    public static final ResourceKey<DecoratedPotPattern> PARASITE = create("parasite_pottery_pattern");

    public static final ResourceKey<DecoratedPotPattern> ROYALTY = create("royalty_pottery_pattern");

    public static final ResourceKey<DecoratedPotPattern> VECTOR = create("vector_pottery_pattern");

    public static final LazySupplier<Map<Item, ResourceKey<DecoratedPotPattern>>> ITEM_TO_POT_TEXTURE = new LazySupplier<>(() -> Map.ofEntries(
        Map.entry(AVPItems.OVOID_POTTERY_SHERD.get(), OVOID),
        Map.entry(AVPItems.PARASITE_POTTERY_SHERD.get(), PARASITE),
        Map.entry(AVPItems.ROYALTY_POTTERY_SHERD.get(), ROYALTY),
        Map.entry(AVPItems.VECTOR_POTTERY_SHERD.get(), VECTOR)
    ));

    private static ResourceKey<DecoratedPotPattern> create(String name) {
        var resourceLocation = AVPResources.location(name);
        return ResourceKey.create(Registries.DECORATED_POT_PATTERN, resourceLocation);
    }
}
