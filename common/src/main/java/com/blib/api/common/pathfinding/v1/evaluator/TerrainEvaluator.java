package com.blib.api.common.pathfinding.v1.evaluator;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelReader;

import com.blib.api.common.pathfinding.v1.node.PathNode;
import com.blib.api.common.pathfinding.v1.terrain.TerrainType;

/**
 * Evaluates terrain for pathfinding. Determines the start and goal nodes, and generates valid neighbors for a given
 * node.
 */
public interface TerrainEvaluator {

    /**
     * Prepares the evaluator for a new pathfinding search.
     */
    void prepare(LevelReader level);

    /**
     * Returns the start node for the entity's current position.
     */
    PathNode getStartNode(BlockPos entityPos);

    /**
     * Returns the goal node for the target position.
     */
    PathNode getGoalNode(BlockPos targetPos);

    /**
     * Returns the goal node for the target position, with access to the resolved search start. Implementations can use
     * the start position to avoid resolving upward requests to lower standable surfaces.
     */
    default PathNode getGoalNode(BlockPos startPos, BlockPos targetPos) {
        return getGoalNode(targetPos);
    }

    /**
     * Populates the neighbors array with valid neighbors of the given node. Returns the number of neighbors added.
     */
    int getNeighbors(PathNode node, PathNode[] neighbors);

    /**
     * Returns true when this evaluator can safely generate reverse edges for bidirectional search. Reverse edges must
     * preserve the same movement legality as {@link #getNeighbors(PathNode, PathNode[])}.
     */
    default boolean supportsBidirectionalSearch() {
        return false;
    }

    /**
     * Populates the predecessors array with nodes that can legally move forward into the given node. Implementations
     * that do not support bidirectional search may leave this empty.
     */
    default int getPredecessors(PathNode node, PathNode[] predecessors) {
        return 0;
    }

    /**
     * Returns the traversal cost multiplier for the given terrain type. Costs are snapshotted at {@link #prepare} time
     * for consistency within a search.
     */
    float getTerrainCost(TerrainType terrainType);

    /**
     * Cleans up after a pathfinding search.
     */
    void cleanup();
}
