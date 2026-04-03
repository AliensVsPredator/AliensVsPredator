package com.blib.api.common.pathfinding.v1.path;

import com.blib.api.common.pathfinding.v1.node.PathNode;
import com.blib.api.common.pathfinding.v1.transition.TerrainTransition;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * The result of a pathfinding search. Contains an ordered list of nodes from start to goal,
 * with computed terrain segments and transition points.
 */
public final class BLibPath {

    private final List<PathNode> nodes;

    private final List<TerrainSegment> segments;

    private final List<TerrainTransition> transitions;

    private final boolean reached;

    private int currentNodeIndex;

    public BLibPath(List<PathNode> nodes, boolean reached) {
        this.nodes = List.copyOf(nodes);
        this.segments = computeSegments(nodes);
        this.transitions = computeTransitions(nodes);
        this.reached = reached;
    }

    public void advance() {
        currentNodeIndex++;
    }

    public boolean isDone() {
        return currentNodeIndex >= nodes.size();
    }

    public PathNode getCurrentNode() {
        if (isDone()) {
            return nodes.getLast();
        }

        return nodes.get(currentNodeIndex);
    }

    public PathNode getNode(int index) {
        return nodes.get(index);
    }

    public int getNodeCount() {
        return nodes.size();
    }

    public int getCurrentNodeIndex() {
        return currentNodeIndex;
    }

    public List<PathNode> getNodes() {
        return nodes;
    }

    public List<TerrainSegment> getSegments() {
        return segments;
    }

    public List<TerrainTransition> getTransitions() {
        return transitions;
    }

    public boolean isReached() {
        return reached;
    }

    private static List<TerrainSegment> computeSegments(List<PathNode> nodes) {
        if (nodes.isEmpty()) {
            return List.of();
        }

        var segments = new ArrayList<TerrainSegment>();
        var segmentStart = 0;
        var currentTerrain = nodes.getFirst().getTerrainType();

        for (int i = 1; i < nodes.size(); i++) {
            var nodeTerrain = nodes.get(i).getTerrainType();

            if (nodeTerrain != currentTerrain) {
                segments.add(new TerrainSegment(currentTerrain, segmentStart, i));
                segmentStart = i;
                currentTerrain = nodeTerrain;
            }
        }

        segments.add(new TerrainSegment(currentTerrain, segmentStart, nodes.size()));

        return Collections.unmodifiableList(segments);
    }

    private static List<TerrainTransition> computeTransitions(List<PathNode> nodes) {
        if (nodes.size() < 2) {
            return List.of();
        }

        var transitions = new ArrayList<TerrainTransition>();

        for (int i = 1; i < nodes.size(); i++) {
            var previousTerrain = nodes.get(i - 1).getTerrainType();
            var currentTerrain = nodes.get(i).getTerrainType();

            if (currentTerrain != previousTerrain) {
                transitions.add(new TerrainTransition(previousTerrain, currentTerrain, i));
            }
        }

        return Collections.unmodifiableList(transitions);
    }
}
