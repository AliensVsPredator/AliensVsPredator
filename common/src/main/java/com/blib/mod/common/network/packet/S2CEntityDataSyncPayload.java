package com.blib.mod.common.network.packet;

import com.just.codec.stream.RecordStreamCodec;
import com.just.codec.stream.StreamCodec;
import com.just.codec.stream.impl.StreamCodecs;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

import com.blib.api.common.data_sync.v1.model.RawDataSyncMap;
import com.blib.mod.BLib;

public record S2CEntityDataSyncPayload(
    int entityId,
    RawDataSyncMap rawDataSyncMap
) implements CustomPacketPayload {

    public static final ResourceLocation PAYLOAD_ID = BLib.MOD.resources().createLocation("entity_data_sync");

    public static final Type<S2CEntityDataSyncPayload> TYPE = new Type<>(PAYLOAD_ID);

    public static final StreamCodec<S2CEntityDataSyncPayload> CODEC = RecordStreamCodec.of(
        StreamCodecs.INT,
        S2CEntityDataSyncPayload::entityId,
        RawDataSyncMap.STREAM_CODEC,
        S2CEntityDataSyncPayload::rawDataSyncMap,
        S2CEntityDataSyncPayload::new
    );

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
