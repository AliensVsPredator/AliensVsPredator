package com.blib.mod.common.network.packet;

import com.just.codec.stream.RecordStreamCodec;
import com.just.codec.stream.StreamCodec;
import com.just.codec.stream.impl.StreamCodecs;
import com.just.codec.stream.schema.StreamCodecSchema;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

import java.util.List;

import com.blib.api.common.codec.v1.BLibCodecs;
import com.blib.api.common.territory.v1.Claimant;
import com.blib.mod.BLib;

public record S2CChunkClaimsSyncPayload(
    int chunkX,
    int chunkZ,
    List<ClaimData> claims
) implements CustomPacketPayload {

    public static final ResourceLocation PAYLOAD_ID = BLib.MOD.resources().createLocation("chunk_claims_sync");

    public static final Type<S2CChunkClaimsSyncPayload> TYPE = new Type<>(PAYLOAD_ID);

    public static final StreamCodec<S2CChunkClaimsSyncPayload> CODEC = RecordStreamCodec.of(
        StreamCodecs.INT,
        S2CChunkClaimsSyncPayload::chunkX,
        StreamCodecs.INT,
        S2CChunkClaimsSyncPayload::chunkZ,
        ClaimData.CODEC.asList(),
        S2CChunkClaimsSyncPayload::claims,
        S2CChunkClaimsSyncPayload::new
    );

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public record ClaimData(
        Claimant claimant,
        ResourceLocation reason
    ) {

        private static final byte ENTITY_TYPE = 0;

        private static final byte FACTION_TYPE = 1;

        private static final StreamCodec<Claimant> CLAIMANT_CODEC = new StreamCodec<>() {

            @Override
            public @NotNull <T> Claimant decode(@NotNull StreamCodecSchema<T> schema, @NotNull T input) {
                var type = schema.readByte(input);

                return switch (type) {
                    case ENTITY_TYPE -> Claimant.entity(schema.read(input, StreamCodecs.UUID));
                    case FACTION_TYPE -> Claimant.faction(schema.read(input, BLibCodecs.Stream.RESOURCE_LOCATION));
                    default -> throw new IllegalArgumentException("Unknown claimant type: " + type);
                };
            }

            @Override
            public <T> void encode(@NotNull StreamCodecSchema<T> schema, @NotNull T input, @NotNull Claimant value) {
                switch (value) {
                    case Claimant.EntityClaimant entityClaimant -> {
                        schema.writeByte(input, ENTITY_TYPE);
                        schema.write(input, StreamCodecs.UUID, entityClaimant.entityId());
                    }
                    case Claimant.FactionClaimant factionClaimant -> {
                        schema.writeByte(input, FACTION_TYPE);
                        schema.write(input, BLibCodecs.Stream.RESOURCE_LOCATION, factionClaimant.factionId());
                    }
                }
            }
        };

        public static final StreamCodec<ClaimData> CODEC = RecordStreamCodec.of(
            CLAIMANT_CODEC,
            ClaimData::claimant,
            BLibCodecs.Stream.RESOURCE_LOCATION,
            ClaimData::reason,
            ClaimData::new
        );
    }
}
