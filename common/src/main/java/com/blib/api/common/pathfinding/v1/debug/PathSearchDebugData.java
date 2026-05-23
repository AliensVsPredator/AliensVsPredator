package com.blib.api.common.pathfinding.v1.debug;

import com.just.codec.stream.StreamCodec;
import com.just.codec.stream.StreamDecoder;
import com.just.codec.stream.StreamEncoder;
import com.just.codec.stream.impl.StreamCodecs;
import com.just.codec.stream.schema.StreamCodecSchema;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Structured summary of a completed block-level path search.
 */
public record PathSearchDebugData(
    PathSearchMode mode,
    PathSearchOutcome outcome,
    PathSearchTermination termination,
    PathDebugBlockPos start,
    PathDebugBlockPos requestedTarget,
    PathDebugBlockPos resolvedGoal,
    PathDebugBlockPos bestNode,
    int visitedCount,
    int maxSearchNodes,
    int pathLength,
    boolean reached,
    boolean corridorUsed,
    int corridorSectionCount,
    List<PathRejectionDebugData> rejections,
    List<PathSearchTimingEntry> timings
) {

    public static final StreamCodec<PathSearchDebugData> CODEC = StreamCodec.of(
        new StreamDecoder<>() {

            @Override
            public <T> @NotNull PathSearchDebugData decode(@NotNull StreamCodecSchema<T> schema, @NotNull T input) {
                return new PathSearchDebugData(
                    PathSearchMode.CODEC.decode(schema, input),
                    PathSearchOutcome.CODEC.decode(schema, input),
                    PathSearchTermination.CODEC.decode(schema, input),
                    PathDebugBlockPos.CODEC.decode(schema, input),
                    PathDebugBlockPos.CODEC.decode(schema, input),
                    PathDebugBlockPos.CODEC.decode(schema, input),
                    PathDebugBlockPos.CODEC.decode(schema, input),
                    StreamCodecs.INT.decode(schema, input),
                    StreamCodecs.INT.decode(schema, input),
                    StreamCodecs.INT.decode(schema, input),
                    StreamCodecs.BOOLEAN.decode(schema, input),
                    StreamCodecs.BOOLEAN.decode(schema, input),
                    StreamCodecs.INT.decode(schema, input),
                    PathRejectionDebugData.CODEC.asList().decode(schema, input),
                    PathSearchTimingEntry.CODEC.asList().decode(schema, input)
                );
            }
        },
        new StreamEncoder<>() {

            @Override
            public <T> void encode(@NotNull StreamCodecSchema<T> schema, @NotNull T output, @NotNull PathSearchDebugData value) {
                PathSearchMode.CODEC.encode(schema, output, value.mode);
                PathSearchOutcome.CODEC.encode(schema, output, value.outcome);
                PathSearchTermination.CODEC.encode(schema, output, value.termination);
                PathDebugBlockPos.CODEC.encode(schema, output, value.start);
                PathDebugBlockPos.CODEC.encode(schema, output, value.requestedTarget);
                PathDebugBlockPos.CODEC.encode(schema, output, value.resolvedGoal);
                PathDebugBlockPos.CODEC.encode(schema, output, value.bestNode);
                StreamCodecs.INT.encode(schema, output, value.visitedCount);
                StreamCodecs.INT.encode(schema, output, value.maxSearchNodes);
                StreamCodecs.INT.encode(schema, output, value.pathLength);
                StreamCodecs.BOOLEAN.encode(schema, output, value.reached);
                StreamCodecs.BOOLEAN.encode(schema, output, value.corridorUsed);
                StreamCodecs.INT.encode(schema, output, value.corridorSectionCount);
                PathRejectionDebugData.CODEC.asList().encode(schema, output, value.rejections);
                PathSearchTimingEntry.CODEC.asList().encode(schema, output, value.timings);
            }
        }
    );

    public PathSearchDebugData(
        PathSearchMode mode,
        PathSearchOutcome outcome,
        PathSearchTermination termination,
        PathDebugBlockPos start,
        PathDebugBlockPos requestedTarget,
        PathDebugBlockPos resolvedGoal,
        PathDebugBlockPos bestNode,
        int visitedCount,
        int maxSearchNodes,
        int pathLength,
        boolean reached,
        boolean corridorUsed,
        int corridorSectionCount,
        List<PathRejectionDebugData> rejections
    ) {
        this(
            mode,
            outcome,
            termination,
            start,
            requestedTarget,
            resolvedGoal,
            bestNode,
            visitedCount,
            maxSearchNodes,
            pathLength,
            reached,
            corridorUsed,
            corridorSectionCount,
            rejections,
            List.of()
        );
    }

    public PathSearchDebugData {
        rejections = List.copyOf(rejections);
        timings = List.copyOf(timings);
    }
}
