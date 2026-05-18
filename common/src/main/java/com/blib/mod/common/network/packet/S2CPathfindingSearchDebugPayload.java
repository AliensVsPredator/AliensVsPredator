package com.blib.mod.common.network.packet;

import com.just.codec.stream.RecordStreamCodec;
import com.just.codec.stream.StreamCodec;
import com.just.codec.stream.impl.StreamCodecs;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

import java.util.List;

import com.blib.api.common.pathfinding.v1.debug.DebugNodeEntry;
import com.blib.api.common.pathfinding.v1.debug.PathSearchDebugData;
import com.blib.mod.BLib;

/**
 * Server-to-client payload carrying a snapshot of an A* pathfinding search for debug visualization.
 */
public record S2CPathfindingSearchDebugPayload(
    int entityId,
    List<DebugNodeEntry> nodes,
    List<Long> corridorKeys,
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
        StreamCodecs.LONG.asList(),
        S2CPathfindingSearchDebugPayload::corridorKeys,
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
