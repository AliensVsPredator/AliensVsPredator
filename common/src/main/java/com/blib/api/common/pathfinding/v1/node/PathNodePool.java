package com.blib.api.common.pathfinding.v1.node;

import java.util.HashMap;
import java.util.Map;

import com.blib.api.common.pathfinding.v1.terrain.TerrainType;

/**
 * Object pool for {@link PathNode} instances. Ensures that each (x, y, z, posture, terrainType) combination maps to
 * exactly one node during a pathfinding search, avoiding duplicate allocations. Call {@link #reset()} between
 * pathfinding calls to clear the pool.
 */
public final class PathNodePool {

    private final Map<Long, PathNode> nodes;

    public PathNodePool() {
        this.nodes = new HashMap<>();
    }

    public PathNode getOrCreate(int x, int y, int z, TerrainType terrainType, int postureIndex) {
        var key = packPosition(x, y, z, postureIndex, terrainType.ordinal());
        var existing = nodes.get(key);

        if (existing != null) {
            return existing;
        }

        var node = new PathNode(x, y, z, terrainType, postureIndex);
        nodes.put(key, node);

        return node;
    }

    public void reset() {
        nodes.clear();
    }

    public int size() {
        return nodes.size();
    }

    private static long packPosition(int x, int y, int z, int postureIndex, int terrainTypeOrdinal) {
        return ((long) postureIndex & 0x3L) << 62 | ((long) terrainTypeOrdinal & 0x7L) << 59 | ((long) x & 0x7FFFFL) << 40 | ((long) y & 0xFFFL) << 28 | ((long) z & 0xFFFFFFFL);
    }
}
