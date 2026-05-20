package com.blib.api.common.pathfinding.v1.debug;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.protocol.game.DebugPackets;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.level.pathfinder.Node;
import net.minecraft.world.level.pathfinder.Path;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import com.blib.api.common.pathfinding.v1.navigator.PathNavigatorApi;
import com.blib.api.common.pathfinding.v1.navigator.PathNavigationState;
import com.blib.api.common.pathfinding.v1.node.PathNode;
import com.blib.api.common.pathfinding.v1.path.BLibPath;
import com.blib.mod.BLib;
import com.blib.mod.common.gameplay.goap.GOAPDebugTracker;
import com.blib.mod.common.network.packet.S2CPathfindingNavDebugPayload;
import com.blib.mod.common.network.packet.S2CPathfindingSearchDebugPayload;

/**
 * Debug utilities for BLib pathfinding. Streams path state to clients that are inspecting a GOAP entity in the engine.
 */
public final class PathDebugUtil {

    private static final float DEFAULT_MAX_DISTANCE_TO_WAYPOINT = 1.0f;

    /**
     * Sends a BLibPath as a vanilla debug packet if a player is tracking this GOAP entity in the engine.
     */
    public static void sendDebugPath(Mob mob, @Nullable BLibPath path) {
        if (path == null || !hasDebugWatchers(mob)) {
            return;
        }

        var vanillaPath = toVanillaPath(path);

        DebugPackets.sendPathFindingPacket(mob.level(), mob, vanillaPath, DEFAULT_MAX_DISTANCE_TO_WAYPOINT);
    }

    private static Path toVanillaPath(BLibPath blibPath) {
        var nodes = new ArrayList<Node>();

        for (int i = 0; i < blibPath.getNodeCount(); i++) {
            var pathNode = blibPath.getNode(i);
            var vanillaNode = new Node(pathNode.getX(), pathNode.getY(), pathNode.getZ());

            vanillaNode.g = pathNode.getGCost();
            vanillaNode.h = pathNode.getHCost();
            vanillaNode.f = pathNode.getGCost() + pathNode.getHCost();

            nodes.add(vanillaNode);
        }

        var lastNode = blibPath.getNode(blibPath.getNodeCount() - 1);
        var targetPos = new BlockPos(lastNode.getX(), lastNode.getY(), lastNode.getZ());
        var path = new Path(nodes, targetPos, blibPath.isReached());

        // Advance the vanilla path to match the current node index so the renderer shows progress.
        for (int i = 0; i < blibPath.getCurrentNodeIndex(); i++) {
            path.advance();
        }

        return path;
    }

    /**
     * Sends the last A* search snapshot, or a current-path snapshot fallback, to players tracking this GOAP entity.
     */
    public static void sendDebugSearchSnapshot(Mob mob, PathNavigatorApi navigator) {
        var players = debugWatchers(mob);
        if (players.isEmpty()) {
            return;
        }

        var snapshot = currentSearchSnapshot(navigator);

        if (snapshot == null) {
            return;
        }

        var payload = new S2CPathfindingSearchDebugPayload(
            mob.getId(),
            snapshot.nodes(),
            snapshot.stableGround(),
            snapshot.corridorKeys(),
            snapshot.visitedCount(),
            snapshot.maxSearchNodes(),
            snapshot.diagnostics()
        );

        for (var player : players) {
            BLib.MOD.networking().sendToClient(player, payload);
        }
    }

    /**
     * Sends the current pathfinding debug state to one player. Used when a player starts tracking an entity that may
     * already be mid-action, before the next GOAP movement tick has a chance to stream fresh data.
     */
    public static void sendDebugState(ServerPlayer player, Mob mob, PathNavigatorApi navigator) {
        sendDebugSearchSnapshot(player, mob, navigator);
        sendDebugNavState(player, mob, navigator);
    }

    private static void sendDebugSearchSnapshot(ServerPlayer player, Mob mob, PathNavigatorApi navigator) {
        var snapshot = currentSearchSnapshot(navigator);

        if (snapshot == null) {
            return;
        }

        var payload = new S2CPathfindingSearchDebugPayload(
            mob.getId(),
            snapshot.nodes(),
            snapshot.stableGround(),
            snapshot.corridorKeys(),
            snapshot.visitedCount(),
            snapshot.maxSearchNodes(),
            snapshot.diagnostics()
        );

        BLib.MOD.networking().sendToClient(player, payload);
    }

    private static @Nullable PathSearchSnapshot currentSearchSnapshot(PathNavigatorApi navigator) {
        var state = navigator.getState();
        var snapshot = state.getLastSearchSnapshot();
        var currentPath = state.getCurrentPath();

        if (currentPath != null && currentPath.getNodeCount() > 0) {
            if (snapshot == null || !snapshotMatchesCurrentPath(snapshot, currentPath)) {
                return currentPathSnapshot(navigator);
            }
        }

        if (snapshot != null) {
            return snapshot;
        }

        return currentPathSnapshot(navigator);
    }

    private static boolean snapshotMatchesCurrentPath(PathSearchSnapshot snapshot, BLibPath path) {
        if (snapshot.diagnostics().pathLength() != path.getNodeCount()) {
            return false;
        }

        var snapshotPathNodes = new DebugNodeEntry[path.getNodeCount()];

        for (var node : snapshot.nodes()) {
            if (node.pathIndex() >= 0 && node.pathIndex() < snapshotPathNodes.length) {
                snapshotPathNodes[node.pathIndex()] = node;
            }
        }

        for (int i = 0; i < path.getNodeCount(); i++) {
            var pathNode = path.getNode(i);
            var snapshotNode = snapshotPathNodes[i];

            if (snapshotNode == null) {
                return false;
            }

            if (
                snapshotNode.x() != pathNode.getX()
                    || snapshotNode.y() != pathNode.getY()
                    || snapshotNode.z() != pathNode.getZ()
                    || snapshotNode.terrainType() != pathNode.getTerrainType().ordinal()
            ) {
                return false;
            }
        }

        return true;
    }

    private static @Nullable PathSearchSnapshot currentPathSnapshot(PathNavigatorApi navigator) {
        var state = navigator.getState();
        var path = state.getCurrentPath();

        if (path == null || path.getNodeCount() == 0) {
            return null;
        }

        var nodes = new ArrayList<DebugNodeEntry>(path.getNodeCount());
        for (int i = 0; i < path.getNodeCount(); i++) {
            nodes.add(toDebugEntry(path.getNode(i), i));
        }

        var firstNode = path.getNode(0);
        var lastNode = path.getNode(path.getNodeCount() - 1);
        var start = new PathDebugBlockPos(firstNode.getX(), firstNode.getY(), firstNode.getZ());
        var targetPos = state.getTargetPos();
        var requestedTarget = targetPos != null ? PathDebugBlockPos.of(targetPos) : new PathDebugBlockPos(
            lastNode.getX(),
            lastNode.getY(),
            lastNode.getZ()
        );
        var resolvedGoal = new PathDebugBlockPos(lastNode.getX(), lastNode.getY(), lastNode.getZ());
        var reached = path.isReached();
        var diagnostics = new PathSearchDebugData(
            PathSearchMode.DIRECT,
            reached ? PathSearchOutcome.COMPLETE : PathSearchOutcome.PARTIAL,
            reached ? PathSearchTermination.GOAL_REACHED : PathSearchTermination.OPEN_SET_EXHAUSTED,
            start,
            requestedTarget,
            resolvedGoal,
            resolvedGoal,
            nodes.size(),
            navigator.getRuntimeConfig().getSearchConfig().maxSearchNodes(),
            path.getNodeCount(),
            reached,
            false,
            0,
            List.of()
        );

        return new PathSearchSnapshot(
            nodes,
            collectStableGroundEntries(path),
            List.of(),
            nodes.size(),
            navigator.getRuntimeConfig().getSearchConfig().maxSearchNodes(),
            diagnostics
        );
    }

    private static List<StableGroundDebugEntry> collectStableGroundEntries(BLibPath path) {
        var stableGround = new LinkedHashMap<PathDebugBlockPos, StableGroundDebugEntry>();

        for (int i = 0; i < path.getNodeCount(); i++) {
            var node = path.getNode(i);
            if (!node.hasStableGround()) {
                continue;
            }

            for (int dx = 0; dx < node.getStableGroundXSize(); dx++) {
                for (int dz = 0; dz < node.getStableGroundZSize(); dz++) {
                    var pos = new PathDebugBlockPos(
                        node.getStableGroundX() + dx,
                        node.getStableGroundY(),
                        node.getStableGroundZ() + dz
                    );
                    stableGround.putIfAbsent(pos, new StableGroundDebugEntry(pos.x(), pos.y(), pos.z(), i));
                }
            }
        }

        return List.copyOf(stableGround.values());
    }

    private static final int NAV_WINDOW_RADIUS = 2;

    /**
     * Sends a rolling window of path nodes around the navigator's current position for the engine pathfinding panel.
     */
    public static void sendDebugNavState(Mob mob, PathNavigatorApi navigator) {
        var players = debugWatchers(mob);
        if (players.isEmpty()) {
            return;
        }

        var payload = buildNavPayload(mob, navigator);

        for (var player : players) {
            BLib.MOD.networking().sendToClient(player, payload);
        }
    }

    private static void sendDebugNavState(ServerPlayer player, Mob mob, PathNavigatorApi navigator) {
        BLib.MOD.networking().sendToClient(player, buildNavPayload(mob, navigator));
    }

    public static boolean hasDebugWatchers(Mob mob) {
        return !debugWatchers(mob).isEmpty();
    }

    private static List<ServerPlayer> debugWatchers(Mob mob) {
        var server = mob.getServer();
        if (server == null) {
            return List.of();
        }
        return GOAPDebugTracker.INSTANCE.playersTracking(server, mob.getUUID());
    }

    private static S2CPathfindingNavDebugPayload buildPayload(
        Mob mob,
        double deltaX,
        double deltaY,
        double deltaZ,
        PathSnapshot path,
        MoveSnapshot move,
        int surfaceBitmap,
        int pathAgeTicks,
        int ticksOnCurrentNode,
        long lastPathComputeNanos,
        int lastPathComputeTick,
        PathNavigatorApi navigator
    ) {
        var state = navigator.getState();
        var currentTerrain = state.getCurrentTerrain();
        var targetPos = state.getTargetPos();
        var featureControl = navigator.getFeatureControl();
        var runtimeConfig = navigator.getRuntimeConfig();
        var pathfindingProfile = featureControl.getPathfindingProfile();
        return new S2CPathfindingNavDebugPayload(
            mob.getId(),
            mob.getName().getString(),
            mob.getX(),
            mob.getY(),
            mob.getZ(),
            deltaX,
            deltaY,
            deltaZ,
            move.wantedX(),
            move.wantedY(),
            move.wantedZ(),
            mob.onGround(),
            mob.isInWater(),
            path.currentIndex(),
            path.totalNodes(),
            path.reached(),
            path.navigating(),
            path.windowNodes(),
            path.windowStart(),
            mob.getYRot(),
            mob.getVisualRotationYInDegrees(),
            ticksOnCurrentNode,
            pathAgeTicks,
            path.distanceToCurrentNode(),
            path.distanceToTarget(),
            move.operation(),
            move.resolvedSpeed(),
            surfaceBitmap,
            lastPathComputeNanos,
            lastPathComputeTick,
            state.isPathPending(),
            currentTerrain != null ? currentTerrain.ordinal() : -1,
            targetPos != null,
            targetPos != null ? targetPos.getX() : 0,
            targetPos != null ? targetPos.getY() : 0,
            targetPos != null ? targetPos.getZ() : 0,
            state.getConsecutiveFailures(),
            state.getFailureCooldownRemainingTicks(),
            runtimeConfig.getStuckTimeoutInTicks(),
            runtimeConfig.getPathRecalculateIntervalInTicks(),
            featureControl.getPathfindingFeatures().toMask(),
            featureControl.consumePathfindingFeatureUsageMask(),
            pathfindingProfile != null ? pathfindingProfile.ordinal() : -1,
            featureControl.getPathfindingFeaturesRevision(),
            runtimeConfig.getSearchConfig().maxSearchNodes(),
            runtimeConfig.getSearchConfig().heuristicWeight(),
            runtimeConfig.getSearchConfig().maxPathLength(),
            runtimeConfig.getSearchConfig().elevationWeight(),
            runtimeConfig.getPathfindingTuning().corridorDistanceThreshold(),
            runtimeConfig.getPathfindingTuning().sectionSearchNodeBudget(),
            runtimeConfig.getPathfindingTuning().corridorBufferRadius(),
            runtimeConfig.getPathfindingTuning().asyncChunkMargin(),
            runtimeConfig.getPathfindingTuning().minImprovement()
        );
    }

    private static S2CPathfindingNavDebugPayload buildNavPayload(Mob mob, PathNavigatorApi navigator) {
        var state = navigator.getState();
        var pathSnapshot = collectPathSnapshot(mob, state);
        var move = collectMoveSnapshot(mob);
        var surfaceBitmap = computeSurfaceBitmap(mob);
        var pathAge = state.getTickCount() - state.getLastPathComputeTick();
        var ticksOnNode = state.getTickCount() - state.getLastProgressTick();
        var delta = mob.getDeltaMovement();
        return buildPayload(
            mob,
            delta.x,
            delta.y,
            delta.z,
            pathSnapshot,
            move,
            surfaceBitmap,
            pathAge,
            ticksOnNode,
            state.getLastPathComputeNanos(),
            state.getLastPathComputeTick(),
            navigator
        );
    }

    private static PathSnapshot collectPathSnapshot(Mob mob, PathNavigationState state) {
        var path = state.getCurrentPath();
        var windowNodes = new ArrayList<DebugNodeEntry>();
        var navigating = state.isNavigating();

        if (path == null) {
            return new PathSnapshot(windowNodes, 0, 0, 0, false, navigating, 0.0f, 0.0f);
        }

        var currentIndex = path.getCurrentNodeIndex();
        var totalNodes = path.getNodeCount();
        var reached = path.isReached();
        var windowStart = Math.max(0, currentIndex - NAV_WINDOW_RADIUS);
        var windowEnd = Math.min(totalNodes, currentIndex + NAV_WINDOW_RADIUS + 1);

        for (int i = windowStart; i < windowEnd; i++) {
            windowNodes.add(toDebugEntry(path.getNode(i), i));
        }

        var distToCurrent = currentIndex < totalNodes ? distanceTo(mob, path.getNode(currentIndex)) : 0.0f;
        var distToTarget = totalNodes > 0 ? distanceTo(mob, path.getNode(totalNodes - 1)) : 0.0f;

        return new PathSnapshot(
            windowNodes,
            windowStart,
            currentIndex,
            totalNodes,
            reached,
            navigating,
            distToCurrent,
            distToTarget
        );
    }

    private static MoveSnapshot collectMoveSnapshot(Mob mob) {
        var moveControl = mob.getMoveControl();
        var operation = moveControl.getClass().getSimpleName();
        var speedModifier = moveControl.getSpeedModifier();
        var baseSpeed = mob.getAttributeValue(Attributes.MOVEMENT_SPEED);
        var resolvedSpeed = (float) (speedModifier * baseSpeed);

        return new MoveSnapshot(
            moveControl.getWantedX(),
            moveControl.getWantedY(),
            moveControl.getWantedZ(),
            operation,
            resolvedSpeed
        );
    }

    private static int computeSurfaceBitmap(Mob mob) {
        var entityPos = mob.blockPosition();
        var bitmap = 0;
        var directions = Direction.values();

        for (int i = 0; i < directions.length; i++) {
            if (mob.level().getBlockState(entityPos.relative(directions[i])).isSolid()) {
                bitmap |= 1 << i;
            }
        }

        return bitmap;
    }

    private static float distanceTo(Mob mob, PathNode node) {
        var dx = node.getX() + 0.5 - mob.getX();
        var dy = node.getY() - mob.getY();
        var dz = node.getZ() + 0.5 - mob.getZ();

        return (float) Math.sqrt(dx * dx + dy * dy + dz * dz);
    }

    private record PathSnapshot(
        List<DebugNodeEntry> windowNodes,
        int windowStart,
        int currentIndex,
        int totalNodes,
        boolean reached,
        boolean navigating,
        float distanceToCurrentNode,
        float distanceToTarget
    ) {}

    private record MoveSnapshot(
        double wantedX,
        double wantedY,
        double wantedZ,
        String operation,
        float resolvedSpeed
    ) {}

    private static DebugNodeEntry toDebugEntry(PathNode node, int pathIndex) {
        var parent = node.getParent() != null
            ? new PathDebugBlockPos(node.getParent().getX(), node.getParent().getY(), node.getParent().getZ())
            : PathDebugBlockPos.NONE;
        return new DebugNodeEntry(
            node.getX(),
            node.getY(),
            node.getZ(),
            node.getTerrainType().ordinal(),
            pathIndex,
            node.getGCost(),
            node.getHCost(),
            node.getCostMalus(),
            -1,
            parent,
            blockBreakPlan(node)
        );
    }

    private static List<PathDebugBlockPos> blockBreakPlan(PathNode node) {
        if (!node.requiresBlockBreaking()) {
            return List.of();
        }

        var blocks = new ArrayList<PathDebugBlockPos>(node.getBlockBreakPlan().size());

        for (var block : node.getBlockBreakPlan().blocks()) {
            blocks.add(new PathDebugBlockPos(block.getX(), block.getY(), block.getZ()));
        }

        return List.copyOf(blocks);
    }

    private PathDebugUtil() {
        throw new UnsupportedOperationException();
    }
}
