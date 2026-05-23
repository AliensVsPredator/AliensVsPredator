package com.blib.api.common.pathfinding.v1.terrain;

/**
 * Classifies a block position for pathfinding purposes.
 */
public enum TerrainType {

    /**
     * A passable feet position where the entity can stand. The block below is solid and the feet block is open.
     */
    GROUND,

    /**
     * A water volume the entity can swim through. Water nodes do not require ground support.
     */
    WATER
}
