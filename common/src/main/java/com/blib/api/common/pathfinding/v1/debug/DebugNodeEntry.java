package com.blib.api.common.pathfinding.v1.debug;

import com.just.codec.stream.RecordStreamCodec;
import com.just.codec.stream.StreamCodec;
import com.just.codec.stream.impl.StreamCodecs;

/**
 * Serializable snapshot of a single A* node for debug visualization.
 *
 * @param x           block x position
 * @param y           block y position
 * @param z           block z position
 * @param terrainType ordinal of {@link com.blib.api.common.pathfinding.v1.terrain.TerrainType}
 * @param onPath      true if this node is part of the final reconstructed path
 */
public record DebugNodeEntry(
    int x,
    int y,
    int z,
    int terrainType,
    boolean onPath
) {

    public static final StreamCodec<DebugNodeEntry> CODEC = RecordStreamCodec.of(
        StreamCodecs.INT,
        DebugNodeEntry::x,
        StreamCodecs.INT,
        DebugNodeEntry::y,
        StreamCodecs.INT,
        DebugNodeEntry::z,
        StreamCodecs.INT,
        DebugNodeEntry::terrainType,
        StreamCodecs.BOOLEAN,
        DebugNodeEntry::onPath,
        DebugNodeEntry::new
    );
}
