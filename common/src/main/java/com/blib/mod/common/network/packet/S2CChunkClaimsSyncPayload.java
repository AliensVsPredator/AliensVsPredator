package com.blib.mod.common.network.packet;

import com.just.codec.stream.RecordStreamCodec;
import com.just.codec.stream.StreamCodec;
import com.just.codec.stream.impl.StreamCodecs;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

import java.util.List;

import com.blib.api.common.codec.v1.BLibCodecs;
import com.blib.mod.BLib;

public record S2CChunkClaimsSyncPayload(
    ResourceLocation dimension,
    List<Entry> entries
) implements CustomPacketPayload {

    public record Entry(
        int chunkX,
        int chunkZ,
        List<ResourceLocation> factionIds
    ) {

        public static final StreamCodec<Entry> CODEC = RecordStreamCodec.of(
            StreamCodecs.INT,
            Entry::chunkX,
            StreamCodecs.INT,
            Entry::chunkZ,
            BLibCodecs.Stream.RESOURCE_LOCATION.asList(),
            Entry::factionIds,
            Entry::new
        );
    }

    public static final ResourceLocation PAYLOAD_ID = BLib.MOD.resources().createLocation("chunk_claims_sync");

    public static final Type<S2CChunkClaimsSyncPayload> TYPE = new Type<>(PAYLOAD_ID);

    public static final StreamCodec<S2CChunkClaimsSyncPayload> CODEC = RecordStreamCodec.of(
        BLibCodecs.Stream.RESOURCE_LOCATION,
        S2CChunkClaimsSyncPayload::dimension,
        Entry.CODEC.asList(),
        S2CChunkClaimsSyncPayload::entries,
        S2CChunkClaimsSyncPayload::new
    );

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
