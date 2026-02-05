package com.blib.internal.common.util;

import org.jetbrains.annotations.ApiStatus;

import java.util.HashMap;
import java.util.Map;

@ApiStatus.Internal
public class ShardManager<K> {

    private final int shardSize;

    private final Map<K, Integer> keyToShardIndex;

    private final Map<Integer, Integer> shardCounts;

    private int highestShardIndex;

    public ShardManager(int shardSize) {
        this.shardSize = shardSize;
        this.keyToShardIndex = new HashMap<>();
        this.shardCounts = new HashMap<>();
        this.highestShardIndex = 0;
    }

    public int assignShardIndex(K key) {
        var existing = keyToShardIndex.get(key);

        if (existing != null) {
            return existing;
        }

        for (var i = 0; i <= highestShardIndex; i++) {
            int count = shardCounts.getOrDefault(i, 0);

            if (count < shardSize) {
                shardCounts.merge(i, 1, Integer::sum);
                keyToShardIndex.put(key, i);
                return i;
            }
        }

        highestShardIndex++;

        shardCounts.put(highestShardIndex, 1);
        keyToShardIndex.put(key, highestShardIndex);

        return highestShardIndex;
    }

    public int getShardIndex(K key) {
        return keyToShardIndex.getOrDefault(key, -1);
    }

    public void recordShardEntry(K key, int shardIndex) {
        keyToShardIndex.put(key, shardIndex);
        shardCounts.merge(shardIndex, 1, Integer::sum);
        this.highestShardIndex = Math.max(highestShardIndex, shardIndex);
    }

    public void remove(K key) {
        var shardIndex = keyToShardIndex.remove(key);

        if (shardIndex != null) {
            shardCounts.computeIfPresent(shardIndex, (k, v) -> v > 1 ? v - 1 : null);
        }
    }

    public void clear() {
        keyToShardIndex.clear();
        shardCounts.clear();
        this.highestShardIndex = 0;
    }
}
