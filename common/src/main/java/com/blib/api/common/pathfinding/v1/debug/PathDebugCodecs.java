package com.blib.api.common.pathfinding.v1.debug;

import com.just.codec.stream.StreamCodec;
import com.just.codec.stream.StreamDecoder;
import com.just.codec.stream.StreamEncoder;
import com.just.codec.stream.impl.StreamCodecs;
import com.just.codec.stream.schema.StreamCodecSchema;
import org.jetbrains.annotations.NotNull;

final class PathDebugCodecs {

    static <E extends Enum<E>> StreamCodec<E> enumCodec(E[] values) {
        return StreamCodec.of(
            new StreamDecoder<>() {

                @Override
                public <T> @NotNull E decode(@NotNull StreamCodecSchema<T> schema, @NotNull T input) {
                    var ordinal = StreamCodecs.INT.decode(schema, input);
                    if (ordinal < 0 || ordinal >= values.length) {
                        ordinal = 0;
                    }
                    return values[ordinal];
                }
            },
            new StreamEncoder<>() {

                @Override
                public <T> void encode(@NotNull StreamCodecSchema<T> schema, @NotNull T output, @NotNull E value) {
                    StreamCodecs.INT.encode(schema, output, value.ordinal());
                }
            }
        );
    }

    private PathDebugCodecs() {}
}
