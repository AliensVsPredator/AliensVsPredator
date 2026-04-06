package com.blib.mod.common.network.packet;

import com.just.codec.stream.StreamCodec;
import com.just.codec.stream.StreamDecoder;
import com.just.codec.stream.StreamEncoder;
import com.just.codec.stream.impl.StreamCodecs;
import com.just.codec.stream.schema.StreamCodecSchema;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

import java.util.List;

import com.blib.api.common.pathfinding.v1.debug.DebugNodeEntry;
import com.blib.mod.BLib;

/**
 * Server-to-client payload carrying a rolling window of path nodes around the navigator's current position for the
 * pathfinding nav debug HUD.
 */
public record S2CPathfindingNavDebugPayload(
    int entityId,
    String entityName,
    double entityX,
    double entityY,
    double entityZ,
    double deltaX,
    double deltaY,
    double deltaZ,
    double wantedX,
    double wantedY,
    double wantedZ,
    boolean onGround,
    boolean inWater,
    int currentNodeIndex,
    int totalNodes,
    boolean reached,
    boolean navigating,
    boolean waitingForBlockBreak,
    List<DebugNodeEntry> windowNodes,
    int windowStartIndex,
    boolean climbingActive,
    int climbingSurfaceDirection,
    float climbingYaw,
    float entityYRot,
    float entityYBodyRot,
    long climbingWaypointPacked,
    long climbingTargetPacked,
    int ticksOnCurrentNode,
    int pathAgeTicks,
    float distanceToCurrentNode,
    float distanceToTarget,
    String moveOperation,
    float resolvedSpeed,
    int surfaceSolidBitmap,
    boolean climbingWasClimbing,
    boolean climbingNearEdgeTransition,
    int climbingTicksSinceSurfaceChange
) implements CustomPacketPayload {

    public static final ResourceLocation PAYLOAD_ID = BLib.MOD.resources().createLocation("pathfinding_nav_debug");

    public static final Type<S2CPathfindingNavDebugPayload> TYPE = new Type<>(PAYLOAD_ID);

    private static final StreamCodec<List<DebugNodeEntry>> NODE_LIST_CODEC = DebugNodeEntry.CODEC.asList();

    public static final StreamCodec<S2CPathfindingNavDebugPayload> CODEC = StreamCodec.of(
        new StreamDecoder<>() {

            @Override
            public <T> @NotNull S2CPathfindingNavDebugPayload decode(
                @NotNull StreamCodecSchema<T> schema,
                @NotNull T buf
            ) {
                return new S2CPathfindingNavDebugPayload(
                    StreamCodecs.INT.decode(schema, buf),
                    StreamCodecs.STRING_UTF8.decode(schema, buf),
                    StreamCodecs.DOUBLE.decode(schema, buf),
                    StreamCodecs.DOUBLE.decode(schema, buf),
                    StreamCodecs.DOUBLE.decode(schema, buf),
                    StreamCodecs.DOUBLE.decode(schema, buf),
                    StreamCodecs.DOUBLE.decode(schema, buf),
                    StreamCodecs.DOUBLE.decode(schema, buf),
                    StreamCodecs.DOUBLE.decode(schema, buf),
                    StreamCodecs.DOUBLE.decode(schema, buf),
                    StreamCodecs.DOUBLE.decode(schema, buf),
                    StreamCodecs.BOOLEAN.decode(schema, buf),
                    StreamCodecs.BOOLEAN.decode(schema, buf),
                    StreamCodecs.INT.decode(schema, buf),
                    StreamCodecs.INT.decode(schema, buf),
                    StreamCodecs.BOOLEAN.decode(schema, buf),
                    StreamCodecs.BOOLEAN.decode(schema, buf),
                    StreamCodecs.BOOLEAN.decode(schema, buf),
                    NODE_LIST_CODEC.decode(schema, buf),
                    StreamCodecs.INT.decode(schema, buf),
                    StreamCodecs.BOOLEAN.decode(schema, buf),
                    StreamCodecs.INT.decode(schema, buf),
                    StreamCodecs.FLOAT.decode(schema, buf),
                    StreamCodecs.FLOAT.decode(schema, buf),
                    StreamCodecs.FLOAT.decode(schema, buf),
                    StreamCodecs.LONG.decode(schema, buf),
                    StreamCodecs.LONG.decode(schema, buf),
                    StreamCodecs.INT.decode(schema, buf),
                    StreamCodecs.INT.decode(schema, buf),
                    StreamCodecs.FLOAT.decode(schema, buf),
                    StreamCodecs.FLOAT.decode(schema, buf),
                    StreamCodecs.STRING_UTF8.decode(schema, buf),
                    StreamCodecs.FLOAT.decode(schema, buf),
                    StreamCodecs.INT.decode(schema, buf),
                    StreamCodecs.BOOLEAN.decode(schema, buf),
                    StreamCodecs.BOOLEAN.decode(schema, buf),
                    StreamCodecs.INT.decode(schema, buf)
                );
            }
        },
        new StreamEncoder<>() {

            @Override
            public <T> void encode(
                @NotNull StreamCodecSchema<T> schema,
                @NotNull T buf,
                @NotNull S2CPathfindingNavDebugPayload value
            ) {
                StreamCodecs.INT.encode(schema, buf, value.entityId);
                StreamCodecs.STRING_UTF8.encode(schema, buf, value.entityName);
                StreamCodecs.DOUBLE.encode(schema, buf, value.entityX);
                StreamCodecs.DOUBLE.encode(schema, buf, value.entityY);
                StreamCodecs.DOUBLE.encode(schema, buf, value.entityZ);
                StreamCodecs.DOUBLE.encode(schema, buf, value.deltaX);
                StreamCodecs.DOUBLE.encode(schema, buf, value.deltaY);
                StreamCodecs.DOUBLE.encode(schema, buf, value.deltaZ);
                StreamCodecs.DOUBLE.encode(schema, buf, value.wantedX);
                StreamCodecs.DOUBLE.encode(schema, buf, value.wantedY);
                StreamCodecs.DOUBLE.encode(schema, buf, value.wantedZ);
                StreamCodecs.BOOLEAN.encode(schema, buf, value.onGround);
                StreamCodecs.BOOLEAN.encode(schema, buf, value.inWater);
                StreamCodecs.INT.encode(schema, buf, value.currentNodeIndex);
                StreamCodecs.INT.encode(schema, buf, value.totalNodes);
                StreamCodecs.BOOLEAN.encode(schema, buf, value.reached);
                StreamCodecs.BOOLEAN.encode(schema, buf, value.navigating);
                StreamCodecs.BOOLEAN.encode(schema, buf, value.waitingForBlockBreak);
                NODE_LIST_CODEC.encode(schema, buf, value.windowNodes);
                StreamCodecs.INT.encode(schema, buf, value.windowStartIndex);
                StreamCodecs.BOOLEAN.encode(schema, buf, value.climbingActive);
                StreamCodecs.INT.encode(schema, buf, value.climbingSurfaceDirection);
                StreamCodecs.FLOAT.encode(schema, buf, value.climbingYaw);
                StreamCodecs.FLOAT.encode(schema, buf, value.entityYRot);
                StreamCodecs.FLOAT.encode(schema, buf, value.entityYBodyRot);
                StreamCodecs.LONG.encode(schema, buf, value.climbingWaypointPacked);
                StreamCodecs.LONG.encode(schema, buf, value.climbingTargetPacked);
                StreamCodecs.INT.encode(schema, buf, value.ticksOnCurrentNode);
                StreamCodecs.INT.encode(schema, buf, value.pathAgeTicks);
                StreamCodecs.FLOAT.encode(schema, buf, value.distanceToCurrentNode);
                StreamCodecs.FLOAT.encode(schema, buf, value.distanceToTarget);
                StreamCodecs.STRING_UTF8.encode(schema, buf, value.moveOperation);
                StreamCodecs.FLOAT.encode(schema, buf, value.resolvedSpeed);
                StreamCodecs.INT.encode(schema, buf, value.surfaceSolidBitmap);
                StreamCodecs.BOOLEAN.encode(schema, buf, value.climbingWasClimbing);
                StreamCodecs.BOOLEAN.encode(schema, buf, value.climbingNearEdgeTransition);
                StreamCodecs.INT.encode(schema, buf, value.climbingTicksSinceSurfaceChange);
            }
        }
    );

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
