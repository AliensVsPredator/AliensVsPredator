package com.blib.api.common.pathfinding.v1.debug;

import java.util.List;

/**
 * Immutable snapshot of an A* search for debug visualization. Captured after each pathfinding search completes.
 *
 * @param nodes          all visited (closed) nodes with their properties
 * @param corridorKeys   packed section keys forming the hierarchical corridor, empty if no corridor was used
 * @param visitedCount   number of nodes the search visited before completing or exhausting budget
 * @param maxSearchNodes the node budget for this search
 * @param diagnostics    structured search summary and rejection diagnostics
 */
public record PathSearchSnapshot(
    List<DebugNodeEntry> nodes,
    List<Long> corridorKeys,
    int visitedCount,
    int maxSearchNodes,
    PathSearchDebugData diagnostics
) {

    public PathSearchSnapshot {
        nodes = List.copyOf(nodes);
        corridorKeys = List.copyOf(corridorKeys);
    }
}
