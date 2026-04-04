package com.blib.api.common.pathfinding.v1.debug;

import net.minecraft.core.BlockPos;
import net.minecraft.network.protocol.game.DebugPackets;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.pathfinder.Node;
import net.minecraft.world.level.pathfinder.Path;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;

import com.blib.api.common.pathfinding.v1.navigator.PathNavigator;
import com.blib.api.common.pathfinding.v1.path.BLibPath;
import com.blib.mod.BLib;
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

    private PathDebugUtil() {
        throw new UnsupportedOperationException();
    }
}
