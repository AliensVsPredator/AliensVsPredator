package com.blib.api.common.pathfinding.v1.debug;

import com.just.codec.stream.RecordStreamCodec;
import com.just.codec.stream.StreamCodec;
import com.just.codec.stream.impl.StreamCodecs;

/**
 * Serializable snapshot of a single A* node for debug visualization.
 *
 * @param x                 block x position
 * @param y                 block y position
 * @param z                 block z position
 * @param terrainType       ordinal of {@link com.blib.api.common.pathfinding.v1.terrain.TerrainType}
 * @param postureIndex      posture index at this node
 * @param surfaceDirection  assigned surface direction ordinal (set by TPO post-processing)
 * @param availableSurfaces bitmask of available surface directions (CLIMBABLE only)
 * @param onPath            true if this node is part of the final reconstructed path
 */
public record DebugNodeEntry(
    int x,
    int y,
    int z,
    int terrainType,
    int postureIndex,
    int surfaceDirection,
    int availableSurfaces,
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
        StreamCodecs.INT,
        DebugNodeEntry::postureIndex,
        StreamCodecs.INT,
        DebugNodeEntry::surfaceDirection,
        StreamCodecs.INT,
        DebugNodeEntry::availableSurfaces,
        StreamCodecs.BOOLEAN,
        DebugNodeEntry::onPath,
        DebugNodeEntry::new
    );
}
