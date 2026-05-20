package com.blib.mod.common.network.packet;

import com.just.codec.stream.RecordStreamCodec;
import com.just.codec.stream.StreamCodec;
import com.just.codec.stream.impl.StreamCodecs;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

import java.util.List;

import com.blib.api.common.pathfinding.v1.debug.PathAabbDebugEntry;
import com.blib.api.common.pathfinding.v1.debug.PathBlockDebugEntry;
import com.blib.api.common.pathfinding.v1.debug.DebugNodeEntry;
import com.blib.api.common.pathfinding.v1.debug.PathEdgeDebugEntry;
import com.blib.api.common.pathfinding.v1.debug.PathOpenNodeDebugEntry;
import com.blib.api.common.pathfinding.v1.debug.PathSearchDebugData;
import com.blib.api.common.pathfinding.v1.debug.PathSupportDebugEntry;
import com.blib.api.common.pathfinding.v1.debug.StableGroundDebugEntry;
import com.blib.mod.BLib;

/**
 * Server-to-client payload carrying a snapshot of an A* pathfinding search for debug visualization.
 */
public record S2CPathfindingSearchDebugPayload(
    int entityId,
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
) implements CustomPacketPayload {

    public static final ResourceLocation PAYLOAD_ID = BLib.MOD.resources().createLocation("pathfinding_search_debug");

    public static final Type<S2CPathfindingSearchDebugPayload> TYPE = new Type<>(PAYLOAD_ID);

    public static final StreamCodec<S2CPathfindingSearchDebugPayload> CODEC = RecordStreamCodec.of(
        StreamCodecs.INT,
        S2CPathfindingSearchDebugPayload::entityId,
        DebugNodeEntry.CODEC.asList(),
        S2CPathfindingSearchDebugPayload::nodes,
        StableGroundDebugEntry.CODEC.asList(),
        S2CPathfindingSearchDebugPayload::stableGround,
        StreamCodecs.LONG.asList(),
        S2CPathfindingSearchDebugPayload::corridorKeys,
        PathOpenNodeDebugEntry.CODEC.asList(),
        S2CPathfindingSearchDebugPayload::openNodes,
        PathEdgeDebugEntry.CODEC.asList(),
        S2CPathfindingSearchDebugPayload::edgeAttempts,
        PathAabbDebugEntry.CODEC.asList(),
        S2CPathfindingSearchDebugPayload::clearanceBoxes,
        PathSupportDebugEntry.CODEC.asList(),
        S2CPathfindingSearchDebugPayload::supportFootprint,
        PathBlockDebugEntry.CODEC.asList(),
        S2CPathfindingSearchDebugPayload::blockingBlocks,
        StreamCodecs.INT,
        S2CPathfindingSearchDebugPayload::visitedCount,
        StreamCodecs.INT,
        S2CPathfindingSearchDebugPayload::maxSearchNodes,
        PathSearchDebugData.CODEC,
        S2CPathfindingSearchDebugPayload::diagnostics,
        S2CPathfindingSearchDebugPayload::new
    );

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
