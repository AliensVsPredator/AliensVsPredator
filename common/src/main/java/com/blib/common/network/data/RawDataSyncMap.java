package com.blib.common.network.data;

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
                // Write the id
                streamCodecSchema.writeByte(input, entry.getKey().byteValue());
                // Write the byte record's length.
                streamCodecSchema.writeByte(input, (byte) entry.getValue().length);
                streamCodecSchema.writeBytes(input, entry.getValue());
            }
        }

        @Override
        public @NotNull <T> RawDataSyncMap decode(@NotNull StreamCodecSchema<T> streamCodecSchema, @NotNull T input) {
            var rawDataById = new HashMap<Integer, byte[]>();
            var length = streamCodecSchema.readVarInt(input);

            for (var i = 0; i < length; i++) {
                var id = streamCodecSchema.readByte(input);
                var dataLength = streamCodecSchema.readByte(input);
                var data = streamCodecSchema.readBytes(input, dataLength);

                rawDataById.put((int) id, data);
            }

            return new RawDataSyncMap(rawDataById);
        }
    };
}
