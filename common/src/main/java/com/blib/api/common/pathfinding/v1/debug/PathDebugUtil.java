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
import java.util.List;

import com.blib.api.common.pathfinding.v1.navigator.PathNavigator;
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
     * Sends the last A* search snapshot to players currently tracking this GOAP entity in the engine.
     */
    public static void sendDebugSearchSnapshot(Mob mob, PathNavigator navigator) {
        var players = debugWatchers(mob);
        if (players.isEmpty()) {
            return;
        }

        var snapshot = navigator.getLastSearchSnapshot();

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

    private static final int NAV_WINDOW_RADIUS = 2;

    /**
     * Sends a rolling window of path nodes around the navigator's current position for the engine pathfinding panel.
     */
    public static void sendDebugNavState(Mob mob, PathNavigator navigator) {
        var players = debugWatchers(mob);
        if (players.isEmpty()) {
            return;
        }

        var pathSnapshot = collectPathSnapshot(mob, navigator);
        var move = collectMoveSnapshot(mob);
        var surfaceBitmap = computeSurfaceBitmap(mob);
        var pathAge = navigator.getTickCount() - navigator.getLastPathComputeTick();
        var ticksOnNode = navigator.getTickCount() - navigator.getLastProgressTick();
        var delta = mob.getDeltaMovement();
        var payload = buildPayload(
            mob,
            delta.x,
            delta.y,
            delta.z,
            pathSnapshot,
            move,
            surfaceBitmap,
            pathAge,
            ticksOnNode,
            navigator.getLastPathComputeNanos(),
            navigator.getLastPathComputeTick(),
            navigator
        );

        for (var player : players) {
            BLib.MOD.networking().sendToClient(player, payload);
        }
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
        PathNavigator navigator
    ) {
        var currentTerrain = navigator.getCurrentTerrain();
        var targetPos = navigator.getTargetPos();
        var blockToBreak = navigator.getBlockToBreak();
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
            path.waitingForBlockBreak(),
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
            navigator.isPathPending(),
            currentTerrain != null ? currentTerrain.ordinal() : -1,
            targetPos != null,
            targetPos != null ? targetPos.getX() : 0,
            targetPos != null ? targetPos.getY() : 0,
            targetPos != null ? targetPos.getZ() : 0,
            blockToBreak != null,
            blockToBreak != null ? blockToBreak.getX() : 0,
            blockToBreak != null ? blockToBreak.getY() : 0,
            blockToBreak != null ? blockToBreak.getZ() : 0,
            navigator.getConsecutiveFailures(),
            navigator.getFailureCooldownRemainingTicks(),
            navigator.getConfig().getStuckTimeoutInTicks(),
            navigator.getConfig().getPathRecalculateIntervalInTicks()
        );
    }

    private static PathSnapshot collectPathSnapshot(Mob mob, PathNavigator navigator) {
        var path = navigator.getCurrentPath();
        var windowNodes = new ArrayList<DebugNodeEntry>();
        var navigating = navigator.isNavigating();
        var waitingForBlockBreak = navigator.isWaitingForBlockBreak();

        if (path == null) {
            return new PathSnapshot(windowNodes, 0, 0, 0, false, navigating, waitingForBlockBreak, 0.0f, 0.0f);
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
            waitingForBlockBreak,
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
        boolean waitingForBlockBreak,
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
            parent
        );
    }

    private PathDebugUtil() {
        throw new UnsupportedOperationException();
    }
}
