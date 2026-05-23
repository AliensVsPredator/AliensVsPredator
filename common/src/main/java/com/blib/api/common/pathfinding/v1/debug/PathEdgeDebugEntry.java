package com.blib.api.common.pathfinding.v1.debug;

import com.just.codec.stream.StreamCodec;
import com.just.codec.stream.StreamDecoder;
import com.just.codec.stream.StreamEncoder;
import com.just.codec.stream.impl.StreamCodecs;
import com.just.codec.stream.schema.StreamCodecSchema;
import org.jetbrains.annotations.NotNull;

/**
 * A single attempted movement edge. Rejected entries retain the source node, candidate node, movement category, and
 * rejection reason so world debug rendering can point at the exact failed transition.
 */
public record PathEdgeDebugEntry(
    PathDebugBlockPos from,
    PathDebugBlockPos to,
    PathEdgeDebugType type,
    boolean accepted,
    int rejectionReason,
    boolean backward
) {

    public static final int NO_REJECTION = -1;

    public static final StreamCodec<PathEdgeDebugEntry> CODEC = StreamCodec.of(
        new StreamDecoder<>() {

            @Override
            public <T> @NotNull PathEdgeDebugEntry decode(@NotNull StreamCodecSchema<T> schema, @NotNull T input) {
                return new PathEdgeDebugEntry(
                    PathDebugBlockPos.CODEC.decode(schema, input),
                    PathDebugBlockPos.CODEC.decode(schema, input),
                    PathEdgeDebugType.CODEC.decode(schema, input),
                    StreamCodecs.BOOLEAN.decode(schema, input),
                    StreamCodecs.INT.decode(schema, input),
                    StreamCodecs.BOOLEAN.decode(schema, input)
                );
            }
        },
        new StreamEncoder<>() {

            @Override
            public <T> void encode(@NotNull StreamCodecSchema<T> schema, @NotNull T output, @NotNull PathEdgeDebugEntry value) {
                PathDebugBlockPos.CODEC.encode(schema, output, value.from);
                PathDebugBlockPos.CODEC.encode(schema, output, value.to);
                PathEdgeDebugType.CODEC.encode(schema, output, value.type);
                StreamCodecs.BOOLEAN.encode(schema, output, value.accepted);
                StreamCodecs.INT.encode(schema, output, value.rejectionReason);
                StreamCodecs.BOOLEAN.encode(schema, output, value.backward);
            }
        }
    );
}
