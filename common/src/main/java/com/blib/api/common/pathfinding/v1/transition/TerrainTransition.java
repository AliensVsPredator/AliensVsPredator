package com.blib.api.common.pathfinding.v1.transition;

import com.blib.api.common.pathfinding.v1.terrain.TerrainType;

/**
 * Represents a terrain boundary crossing within a path.
 *
 * @param from      the terrain type being exited
 * @param to        the terrain type being entered
 * @param nodeIndex the index of the first node in the new terrain
 */
public record TerrainTransition(TerrainType from, TerrainType to, int nodeIndex) {
}
