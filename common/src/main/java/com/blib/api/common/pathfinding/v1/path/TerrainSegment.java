package com.blib.api.common.pathfinding.v1.path;

import com.blib.api.common.pathfinding.v1.terrain.TerrainType;

/**
 * A contiguous run of the same terrain type within a path.
 *
 * @param terrainType the terrain type for this segment
 * @param startIndex  inclusive start index into the path's node list
 * @param endIndex    exclusive end index into the path's node list
 */
public record TerrainSegment(TerrainType terrainType, int startIndex, int endIndex) {

    public int length() {
        return endIndex - startIndex;
    }
}
