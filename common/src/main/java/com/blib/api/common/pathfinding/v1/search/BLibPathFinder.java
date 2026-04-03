package com.blib.api.common.pathfinding.v1.search;

import com.blib.api.common.pathfinding.v1.evaluator.TerrainEvaluator;
import com.blib.api.common.pathfinding.v1.node.PathNode;
import com.blib.api.common.pathfinding.v1.path.BLibPath;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelReader;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;

/**
 * A* pathfinding implementation using {@link TerrainEvaluator} for neighbor generation.
 */
public final class BLibPathFinder {

    private static final int MAX_NEIGHBORS = 16;

    private final TerrainEvaluator evaluator;

    private final SearchConfig config;

    public BLibPathFinder(TerrainEvaluator evaluator, SearchConfig config) {
        this.evaluator = evaluator;
        this.config = config;
    }

    public BLibPathFinder(TerrainEvaluator evaluator) {
        this(evaluator, SearchConfig.DEFAULT);
    }

    public @Nullable BLibPath findPath(LevelReader level, BlockPos startPos, BlockPos targetPos) {
        evaluator.prepare(level);

        try {
            return search(startPos, targetPos);
        } finally {
            evaluator.cleanup();
        }
    }

    private @Nullable BLibPath search(BlockPos startPos, BlockPos targetPos) {
        var startNode = evaluator.getStartNode(startPos);
        var goalNode = evaluator.getGoalNode(targetPos);

        startNode.setGCost(0);
        startNode.setHCost(heuristic(startNode, goalNode));

        var openSet = new PriorityQueue<PathNode>(Comparator.comparingDouble(PathNode::totalCost));
        openSet.add(startNode);

        var neighbors = new PathNode[MAX_NEIGHBORS];
        var visitedCount = 0;
        PathNode bestNode = startNode;

        while (!openSet.isEmpty() && visitedCount < config.maxSearchNodes()) {
            var current = openSet.poll();

            if (current.isClosed()) {
                continue;
            }

            current.setClosed(true);
            visitedCount++;

            if (current.equals(goalNode)) {
                return reconstructPath(current, true);
            }

            if (current.distanceSquaredTo(goalNode) < bestNode.distanceSquaredTo(goalNode)) {
                bestNode = current;
            }

            var neighborCount = evaluator.getNeighbors(current, neighbors);

            for (int i = 0; i < neighborCount; i++) {
                var neighbor = neighbors[i];

                if (neighbor.isClosed()) {
                    continue;
                }

                var edgeCost = current.distanceTo(neighbor) * evaluator.getTerrainCost(neighbor.getTerrainType());
                var tentativeG = current.getGCost() + edgeCost;

                if (tentativeG >= neighbor.getGCost() && neighbor.getGCost() > 0) {
                    continue;
                }

                neighbor.setParent(current);
                neighbor.setGCost(tentativeG);
                neighbor.setHCost(heuristic(neighbor, goalNode));
                openSet.add(neighbor);
            }
        }

        if (bestNode != startNode) {
            return reconstructPath(bestNode, false);
        }

        return null;
    }

    private float heuristic(PathNode from, PathNode to) {
        return from.distanceTo(to) * config.heuristicWeight();
    }

    private BLibPath reconstructPath(PathNode endNode, boolean reached) {
        var nodes = new ArrayList<PathNode>();
        var current = endNode;

        while (current != null && nodes.size() < config.maxPathLength()) {
            nodes.add(current);
            current = current.getParent();
        }

        Collections.reverse(nodes);

        return new BLibPath(nodes, reached);
    }
}
