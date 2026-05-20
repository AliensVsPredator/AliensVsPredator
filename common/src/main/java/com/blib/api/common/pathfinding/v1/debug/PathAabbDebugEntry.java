package com.blib.api.common.pathfinding.v1.debug;

import com.just.codec.stream.StreamCodec;
import com.just.codec.stream.StreamDecoder;
import com.just.codec.stream.StreamEncoder;
import com.just.codec.stream.impl.StreamCodecs;
import com.just.codec.stream.schema.StreamCodecSchema;
import org.jetbrains.annotations.NotNull;

/**
 * Entity-volume probe used while validating a movement candidate.
 */
public record PathAabbDebugEntry(
    PathDebugBlockPos node,
    double minX,
    double minY,
    double minZ,
    double maxX,
    double maxY,
    double maxZ,
    PathEdgeDebugType type,
    int rejectionReason
) {

    public static final StreamCodec<PathAabbDebugEntry> CODEC = StreamCodec.of(
        new StreamDecoder<>() {

            @Override
            public <T> @NotNull PathAabbDebugEntry decode(@NotNull StreamCodecSchema<T> schema, @NotNull T input) {
                return new PathAabbDebugEntry(
                    PathDebugBlockPos.CODEC.decode(schema, input),
                    StreamCodecs.DOUBLE.decode(schema, input),
                    StreamCodecs.DOUBLE.decode(schema, input),
                    StreamCodecs.DOUBLE.decode(schema, input),
                    StreamCodecs.DOUBLE.decode(schema, input),
                    StreamCodecs.DOUBLE.decode(schema, input),
                    StreamCodecs.DOUBLE.decode(schema, input),
                    PathEdgeDebugType.CODEC.decode(schema, input),
                    StreamCodecs.INT.decode(schema, input)
                );
            }
        },
        new StreamEncoder<>() {

            @Override
            public <T> void encode(@NotNull StreamCodecSchema<T> schema, @NotNull T output, @NotNull PathAabbDebugEntry value) {
                PathDebugBlockPos.CODEC.encode(schema, output, value.node);
                StreamCodecs.DOUBLE.encode(schema, output, value.minX);
                StreamCodecs.DOUBLE.encode(schema, output, value.minY);
                StreamCodecs.DOUBLE.encode(schema, output, value.minZ);
                StreamCodecs.DOUBLE.encode(schema, output, value.maxX);
                StreamCodecs.DOUBLE.encode(schema, output, value.maxY);
                StreamCodecs.DOUBLE.encode(schema, output, value.maxZ);
                PathEdgeDebugType.CODEC.encode(schema, output, value.type);
                StreamCodecs.INT.encode(schema, output, value.rejectionReason);
            }
        }
    );
}
