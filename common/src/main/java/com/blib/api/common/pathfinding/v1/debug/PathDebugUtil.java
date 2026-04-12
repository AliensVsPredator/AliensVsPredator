package com.blib.api.common.pathfinding.v1.debug;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.protocol.game.DebugPackets;
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
import com.blib.mod.common.network.packet.S2CPathfindingNavDebugPayload;
import com.blib.mod.common.network.packet.S2CPathfindingSearchDebugPayload;
import com.blib.mod.common.property.BLibModProperties;
import com.blib.mod.common.property.BLibModPropertyAccess;

/**
 * Debug utilities for BLib pathfinding. Converts BLibPath to vanilla Path and sends debug packets to clients for
 * rendering via Minecraft's built-in pathfinding debug renderer.
 */
public final class PathDebugUtil {

    private static final float DEFAULT_MAX_DISTANCE_TO_WAYPOINT = 1.0f;

    /**
     * Sends a BLibPath as a debug packet if debug rendering is enabled.
     */
    public static void sendDebugPath(Mob mob, @Nullable BLibPath path) {
        if (!BLibModPropertyAccess.INSTANCE.get(BLibModProperties.Debug.Render.ENABLED)) {
            return;
        }

        if (path == null) {
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
     * Sends the last A* search snapshot as a debug packet if search debug rendering is enabled.
     */
    public static void sendDebugSearchSnapshot(Mob mob, PathNavigator navigator) {
        if (!BLibModPropertyAccess.INSTANCE.get(BLibModProperties.Debug.Render.ENABLED)) {
            return;
        }

        if (!BLibModPropertyAccess.INSTANCE.get(BLibModProperties.Debug.Render.PathSearch.ENABLED)) {
            return;
        }

        var snapshot = navigator.getLastSearchSnapshot();

        if (snapshot == null) {
            return;
        }

        var payload = new S2CPathfindingSearchDebugPayload(
            mob.getId(),
            snapshot.nodes(),
            snapshot.corridorKeys(),
            snapshot.visitedCount(),
            snapshot.maxSearchNodes()
        );

        BLib.MOD.networking().sendToAllClientsTrackingEntity(mob, payload);
    }

    private static final int NAV_WINDOW_RADIUS = 2;

    /**
     * Sends a rolling window of path nodes around the navigator's current position for the nav debug HUD.
     */
    public static void sendDebugNavState(Mob mob, PathNavigator navigator) {
        if (!BLibModPropertyAccess.INSTANCE.get(BLibModProperties.Debug.Render.ENABLED)) {
            return;
        }

        if (!BLibModPropertyAccess.INSTANCE.get(BLibModProperties.Debug.Render.PathSearch.ENABLED)) {
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
            navigator.getLastPathComputeTick()
        );

        BLib.MOD.networking().sendToAllClientsTrackingEntity(mob, payload);
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
        int lastPathComputeTick
    ) {
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
            lastPathComputeTick
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
        return new DebugNodeEntry(
            node.getX(),
            node.getY(),
            node.getZ(),
            node.getTerrainType().ordinal(),
            pathIndex
        );
    }

    private PathDebugUtil() {
        throw new UnsupportedOperationException();
    }
}
