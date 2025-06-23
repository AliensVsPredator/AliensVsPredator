package com.lib.common.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public record RawDataSyncMap(
    Map<Integer, byte[]> rawDataById
) {

    public static final StreamCodec<FriendlyByteBuf, RawDataSyncMap> STREAM_CODEC = new StreamCodec<>() {

        @Override
        public @NotNull RawDataSyncMap decode(@NotNull FriendlyByteBuf byteBuf) {
            var rawDataById = new HashMap<Integer, byte[]>();
            var length = byteBuf.readVarInt();

            for (var i = 0; i < length; i++) {
                var id = byteBuf.readByte();
                var dataLength = byteBuf.readByte();
                var data = new byte[dataLength];
                byteBuf.readBytes(data);

                rawDataById.put((int) id, data);
            }

            return new RawDataSyncMap(rawDataById);
        }

        @Override
        public void encode(@NotNull FriendlyByteBuf byteBuf, @NotNull RawDataSyncMap rawDataSyncMap) {
            byteBuf.writeVarInt(rawDataSyncMap.rawDataById.size());

            for (var entry : rawDataSyncMap.rawDataById.entrySet()) {
                // Write the id
                byteBuf.writeByte(entry.getKey());
                // Write the byte record's length.
                byteBuf.writeByte(entry.getValue().length);
                byteBuf.writeBytes(entry.getValue());
            }
        }
    };
}
