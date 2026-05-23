package com.blib.api.common.pathfinding.v1.debug;

import com.just.codec.stream.RecordStreamCodec;
import com.just.codec.stream.StreamCodec;
import com.just.codec.stream.impl.StreamCodecs;

/**
 * Serializable support block selected by pathfinding as stable ground for a path node.
 *
 * @param x         support block x position
 * @param y         support block y position
 * @param z         support block z position
 * @param pathIndex 0-based path node index that first depends on this support block
 */
public record StableGroundDebugEntry(
    int x,
    int y,
    int z,
    int pathIndex
) {

    public static final StreamCodec<StableGroundDebugEntry> CODEC = RecordStreamCodec.of(
        StreamCodecs.INT,
        StableGroundDebugEntry::x,
        StreamCodecs.INT,
        StableGroundDebugEntry::y,
        StreamCodecs.INT,
        StableGroundDebugEntry::z,
        StreamCodecs.INT,
        StableGroundDebugEntry::pathIndex,
        StableGroundDebugEntry::new
    );
}
