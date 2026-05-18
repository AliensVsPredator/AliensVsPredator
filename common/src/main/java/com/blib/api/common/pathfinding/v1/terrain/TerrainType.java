package com.blib.api.common.pathfinding.v1.terrain;

/**
 * Classifies a block position for pathfinding purposes. Entities configure which terrain types they support and the
 * traversal cost for each.
 */
public enum TerrainType {

    /**
     * A passable position where the entity can stand. Solid block below, non-solid blocks at feet and head level.
     * Standard walking movement with gravity.
     */
    GROUND,

    /**
     * A position submerged in fluid. The entity swims freely in 3D (no gravity constraint). Movement is typically
     * slower and uses water physics (drag, buoyancy).
     */
    WATER,

    /**
     * A passable position with no solid support below. The entity is falling or flying. Only traversable by entities
     * that support flight or as part of a fall (cost scales with fall distance).
     */
    AIR
}
