package com.blib.internal.common.util;

import org.jetbrains.annotations.ApiStatus;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

import com.blib.api.common.util.v1.Dirty;

@ApiStatus.Internal
public final class ShardUtil {

    private ShardUtil() {
        throw new UnsupportedOperationException();
    }

    public static int shardIndex(int itemIndex, int shardSize) {
        return itemIndex / shardSize;
    }

    public static int shardStart(int shardIndex, int shardSize) {
        return shardIndex * shardSize;
    }

    public static int shardEnd(int shardIndex, int shardSize, int totalSize) {
        return Math.min(shardStart(shardIndex, shardSize) + shardSize, totalSize);
    }

    @SafeVarargs
    public static <K> Set<Integer> findDirtyShards(List<K> keys, int shardSize, Function<K, ? extends Dirty>... lookups) {
        var dirtyShards = new HashSet<Integer>();

        for (int i = 0; i < keys.size(); i++) {
            var key = keys.get(i);

            for (var lookup : lookups) {
                var dirty = lookup.apply(key);

                if (dirty != null && dirty.isDirty()) {
                    dirtyShards.add(shardIndex(i, shardSize));
                    break;
                }
            }
        }

        return dirtyShards;
    }

    public static void clearAllDirty(Map<?, ? extends Dirty> map) {
        for (var entry : map.values()) {
            entry.clearDirty();
        }
    }
}
