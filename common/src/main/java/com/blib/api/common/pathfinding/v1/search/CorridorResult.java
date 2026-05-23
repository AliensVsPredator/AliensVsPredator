package com.blib.api.common.pathfinding.v1.search;

import net.minecraft.core.BlockPos;

import java.util.List;
import java.util.Set;

/**
 * Result of a section-level corridor search. Contains both the buffered corridor set (for constraining block-level A*)
 * and the ordered section waypoints (for segmented long-distance pathfinding).
 *
 * @param corridor         set of section keys forming the buffered corridor
 * @param sectionWaypoints ordered block positions (section centers) along the path from start to goal
 */
public record CorridorResult(
    Set<Long> corridor,
    List<BlockPos> sectionWaypoints
) {}
