package com.blib.api.common.data_sync.v1.model;

import com.just.codec.stream.StreamCodec;
import com.just.codec.stream.schema.StreamCodecSchema;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public record RawDataSyncMap(
    Map<Integer, byte[]> rawDataById
) {

    public static final StreamCodec<RawDataSyncMap> STREAM_CODEC = new StreamCodec<>() {

        @Override
        public <T> void encode(@NotNull StreamCodecSchema<T> streamCodecSchema, @NotNull T input, @NotNull RawDataSyncMap value) {
            streamCodecSchema.writeVarInt(input, value.rawDataById.size());

            for (var entry : value.rawDataById.entrySet()) {
                streamCodecSchema.writeVarInt(input, entry.getKey());
                streamCodecSchema.writeVarInt(input, entry.getValue().length);
                streamCodecSchema.writeBytes(input, entry.getValue());
            }
        }

        @Override
        public @NotNull <T> RawDataSyncMap decode(@NotNull StreamCodecSchema<T> streamCodecSchema, @NotNull T input) {
            var rawDataById = new HashMap<Integer, byte[]>();
            var length = streamCodecSchema.readVarInt(input);

            for (var i = 0; i < length; i++) {
                var id = streamCodecSchema.readVarInt(input);
                var dataLength = streamCodecSchema.readVarInt(input);
                var data = streamCodecSchema.readBytes(input, dataLength);

                rawDataById.put(id, data);
            }

            return new RawDataSyncMap(rawDataById);
        }
    };
}
