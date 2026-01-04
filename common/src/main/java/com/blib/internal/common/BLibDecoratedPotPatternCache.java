package com.blib.internal.common;

import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.entity.DecoratedPotPattern;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

@ApiStatus.Internal
public class BLibDecoratedPotPatternCache {

    private static final Map<Item, ResourceKey<DecoratedPotPattern>> CACHE = new HashMap<>();

    public static @Nullable ResourceKey<DecoratedPotPattern> get(Item item) {
        return CACHE.get(item);
    }

    public static void put(Item item, ResourceKey<DecoratedPotPattern> resourceKey) {
        CACHE.put(item, resourceKey);
    }

    private BLibDecoratedPotPatternCache() {
        throw new UnsupportedOperationException();
    }
}
