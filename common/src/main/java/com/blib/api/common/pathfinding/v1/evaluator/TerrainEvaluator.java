package com.blib.api.common.pathfinding.v1.evaluator;

import com.blib.api.common.pathfinding.v1.node.PathNode;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelReader;

/**
 * Evaluates terrain for pathfinding. Determines the start and goal nodes,
 * and generates valid neighbors for a given node.
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
     * Populates the neighbors array with valid neighbors of the given node.
     * Returns the number of neighbors added.
     */
    int getNeighbors(PathNode node, PathNode[] neighbors);

    /**
     * Cleans up after a pathfinding search.
     */
    void cleanup();
}
