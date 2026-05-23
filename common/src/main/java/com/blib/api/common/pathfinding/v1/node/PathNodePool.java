package com.blib.api.common.pathfinding.v1.node;

import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;

import com.blib.api.common.pathfinding.v1.terrain.TerrainType;

/**
 * Object pool for {@link PathNode} instances. Ensures that each (x, y, z, terrainType, posture) combination maps to
 * exactly one node during a pathfinding search, avoiding duplicate allocations. Call {@link #reset()} between
 * pathfinding calls to clear the pool.
 */
public final class PathNodePool {

    private final Long2ObjectOpenHashMap<PathNode> nodes;

    public PathNodePool() {
        this.nodes = new Long2ObjectOpenHashMap<>();
    }

    public PathNode getOrCreate(int x, int y, int z, TerrainType terrainType) {
        return getOrCreate(x, y, z, terrainType, PathPosture.STANDING);
    }

    public PathNode getOrCreate(int x, int y, int z, TerrainType terrainType, PathPosture posture) {
        var key = packPosition(x, y, z, terrainType.ordinal(), posture.ordinal());
        var existing = nodes.get(key);

        if (existing != null) {
            return existing;
        }

        var node = new PathNode(x, y, z, terrainType, posture);
        nodes.put(key, node);

        return node;
    }

    public void reset() {
        nodes.clear();
    }

    public int size() {
        return nodes.size();
    }

    private static long packPosition(int x, int y, int z, int terrainTypeOrdinal, int postureOrdinal) {
        return ((long) postureOrdinal & 0x3L) << 61
            | ((long) terrainTypeOrdinal & 0x7L) << 58
            | ((long) x & 0x7FFFFL) << 39
            | ((long) y & 0xFFFL) << 27
            | ((long) z & 0x7FFFFFFL);
    }
}
