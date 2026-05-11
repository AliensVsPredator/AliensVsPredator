package com.blib.internal.common.util;

import org.jetbrains.annotations.ApiStatus;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Maps keys to fixed-size shard indices for sharded persistence (one file per shard). Also tracks which shards have
 * unwritten changes — callers route mutations through {@link #markDirty} (or rely on {@link #remove}'s auto-mark) so
 * the next save knows which shard files to rewrite.
 * <p>
 * Per-shard dirty tracking matches save granularity (per shard file). Object-level dirty bits on individual entries are
 * still useful for "needs re-serialization" decisions, but the save loop should always consult this set to decide which
 * files to touch: an object-only dirty bit goes away with a deleted object, leaving the shard silently unwritten and
 * the deleted entry resurrecting from disk on next load.
 */
@ApiStatus.Internal
public class ShardManager<K> {

    private final int shardSize;

    private final Map<K, Integer> keyToShardIndex;

    private final Map<Integer, Integer> shardCounts;

    /** Shards that need rewriting on the next save. Cleared by {@link #clearDirty} after the save completes. */
    private final Set<Integer> dirtyShards;

    private int highestShardIndex;

    public ShardManager(int shardSize) {
        this.shardSize = shardSize;
        this.keyToShardIndex = new HashMap<>();
        this.shardCounts = new HashMap<>();
        this.dirtyShards = new HashSet<>();
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
            // The shard now lacks an entry that's on disk — mark it dirty so the next save rewrites the file without
            // the deleted entry. This is the foolproof default: callers can't forget to mark dirty on deletion.
            dirtyShards.add(shardIndex);
            shardCounts.computeIfPresent(shardIndex, (k, v) -> v > 1 ? v - 1 : null);
        }
    }

    /** Marks the shard containing {@code key} as needing rewrite on the next save. No-op if the key is unknown. */
    public void markDirty(K key) {
        var shardIndex = keyToShardIndex.get(key);
        if (shardIndex != null) {
            dirtyShards.add(shardIndex);
        }
    }

    /** Direct-by-index variant of {@link #markDirty}. Used when the caller already has the shard index in hand. */
    public void markDirtyShard(int shardIndex) {
        dirtyShards.add(shardIndex);
    }

    /** Read-only view of the dirty shard set. Iterate this in the save loop to know which files to rewrite. */
    public Set<Integer> dirtyShards() {
        return Collections.unmodifiableSet(dirtyShards);
    }

    public boolean hasDirtyShards() {
        return !dirtyShards.isEmpty();
    }

    /** Clears the dirty set. Call after a successful save flushes the dirty shards to disk. */
    public void clearDirty() {
        dirtyShards.clear();
    }

    public void clear() {
        keyToShardIndex.clear();
        shardCounts.clear();
        dirtyShards.clear();
        this.highestShardIndex = 0;
    }
}
