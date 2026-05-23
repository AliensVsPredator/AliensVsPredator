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
    boolean replaceArea,
    int minChunkX,
    int minChunkZ,
    int maxChunkX,
    int maxChunkZ,
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

    public static S2CChunkClaimsSyncPayload incremental(ResourceLocation dimension, List<Entry> entries) {
        return new S2CChunkClaimsSyncPayload(dimension, false, 0, 0, -1, -1, entries);
    }

    public static S2CChunkClaimsSyncPayload replaceArea(
        ResourceLocation dimension,
        int minChunkX,
        int minChunkZ,
        int maxChunkX,
        int maxChunkZ,
        List<Entry> entries
    ) {
        return new S2CChunkClaimsSyncPayload(dimension, true, minChunkX, minChunkZ, maxChunkX, maxChunkZ, entries);
    }

    public static final StreamCodec<S2CChunkClaimsSyncPayload> CODEC = RecordStreamCodec.of(
        BLibCodecs.Stream.RESOURCE_LOCATION,
        S2CChunkClaimsSyncPayload::dimension,
        StreamCodecs.BOOLEAN,
        S2CChunkClaimsSyncPayload::replaceArea,
        StreamCodecs.INT,
        S2CChunkClaimsSyncPayload::minChunkX,
        StreamCodecs.INT,
        S2CChunkClaimsSyncPayload::minChunkZ,
        StreamCodecs.INT,
        S2CChunkClaimsSyncPayload::maxChunkX,
        StreamCodecs.INT,
        S2CChunkClaimsSyncPayload::maxChunkZ,
        Entry.CODEC.asList(),
        S2CChunkClaimsSyncPayload::entries,
        S2CChunkClaimsSyncPayload::new
    );

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
