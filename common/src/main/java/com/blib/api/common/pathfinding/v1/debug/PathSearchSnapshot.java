package com.blib.api.common.pathfinding.v1.debug;

import java.util.List;

/**
 * Immutable snapshot of an A* search for debug visualization. Captured after each pathfinding search completes.
 *
 * @param nodes          all visited (closed) nodes with their properties
 * @param stableGround   support blocks selected by pathfinding for path nodes
 * @param corridorKeys   packed section keys forming the hierarchical corridor, empty if no corridor was used
 * @param openNodes      generated nodes still queued when the search snapshot was built
 * @param edgeAttempts   movement edges attempted while generating neighbors
 * @param clearanceBoxes entity boxes probed while validating movement clearance
 * @param supportFootprint support-footprint cells checked for movement candidates
 * @param blockingBlocks blocks that caused candidate clearance or support rejection
 * @param visitedCount   number of nodes the search visited before completing or exhausting budget
 * @param maxSearchNodes the node budget for this search
 * @param diagnostics    structured search summary and rejection diagnostics
 */
public record PathSearchSnapshot(
    List<DebugNodeEntry> nodes,
    List<StableGroundDebugEntry> stableGround,
    List<Long> corridorKeys,
    List<PathOpenNodeDebugEntry> openNodes,
    List<PathEdgeDebugEntry> edgeAttempts,
    List<PathAabbDebugEntry> clearanceBoxes,
    List<PathSupportDebugEntry> supportFootprint,
    List<PathBlockDebugEntry> blockingBlocks,
    int visitedCount,
    int maxSearchNodes,
    PathSearchDebugData diagnostics
) {

    public PathSearchSnapshot {
        nodes = List.copyOf(nodes);
        stableGround = List.copyOf(stableGround);
        corridorKeys = List.copyOf(corridorKeys);
        openNodes = List.copyOf(openNodes);
        edgeAttempts = List.copyOf(edgeAttempts);
        clearanceBoxes = List.copyOf(clearanceBoxes);
        supportFootprint = List.copyOf(supportFootprint);
        blockingBlocks = List.copyOf(blockingBlocks);
    }
}
