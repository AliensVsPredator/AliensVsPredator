package com.blib.internal.common;

import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.entity.DecoratedPotPattern;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

import com.blib.api.common.KeyedAccess;

@ApiStatus.Internal
public class BLibDecoratedPotPatternCache implements KeyedAccess<Item, ResourceKey<DecoratedPotPattern>> {

    public static final BLibDecoratedPotPatternCache INSTANCE = new BLibDecoratedPotPatternCache();

    private final Map<Item, ResourceKey<DecoratedPotPattern>> cache;

    private BLibDecoratedPotPatternCache() {
        this.cache = new HashMap<>();
    }

    @Override
    public @Nullable ResourceKey<DecoratedPotPattern> getOrNull(Item item) {
        return cache.get(item);
    }

    public void put(Item item, ResourceKey<DecoratedPotPattern> resourceKey) {
        cache.put(item, resourceKey);
    }
}
