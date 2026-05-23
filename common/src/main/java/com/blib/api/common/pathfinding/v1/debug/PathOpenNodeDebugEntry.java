package com.blib.api.common.pathfinding.v1.debug;

import com.just.codec.stream.StreamCodec;
import com.just.codec.stream.StreamDecoder;
import com.just.codec.stream.StreamEncoder;
import com.just.codec.stream.impl.StreamCodecs;
import com.just.codec.stream.schema.StreamCodecSchema;
import org.jetbrains.annotations.NotNull;

/**
 * A generated search node that remained queued when the search completed or was superseded.
 */
public record PathOpenNodeDebugEntry(
    PathDebugBlockPos node,
    PathDebugBlockPos parent,
    int terrainType,
    float gCost,
    float hCost,
    boolean backward
) {

    public static final StreamCodec<PathOpenNodeDebugEntry> CODEC = StreamCodec.of(
        new StreamDecoder<>() {

            @Override
            public <T> @NotNull PathOpenNodeDebugEntry decode(@NotNull StreamCodecSchema<T> schema, @NotNull T input) {
                return new PathOpenNodeDebugEntry(
                    PathDebugBlockPos.CODEC.decode(schema, input),
                    PathDebugBlockPos.CODEC.decode(schema, input),
                    StreamCodecs.INT.decode(schema, input),
                    StreamCodecs.FLOAT.decode(schema, input),
                    StreamCodecs.FLOAT.decode(schema, input),
                    StreamCodecs.BOOLEAN.decode(schema, input)
                );
            }
        },
        new StreamEncoder<>() {

            @Override
            public <T> void encode(@NotNull StreamCodecSchema<T> schema, @NotNull T output, @NotNull PathOpenNodeDebugEntry value) {
                PathDebugBlockPos.CODEC.encode(schema, output, value.node);
                PathDebugBlockPos.CODEC.encode(schema, output, value.parent);
                StreamCodecs.INT.encode(schema, output, value.terrainType);
                StreamCodecs.FLOAT.encode(schema, output, value.gCost);
                StreamCodecs.FLOAT.encode(schema, output, value.hCost);
                StreamCodecs.BOOLEAN.encode(schema, output, value.backward);
            }
        }
    );
}
