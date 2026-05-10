package com.blib.mod.common.network.packet;

import com.just.codec.stream.RecordStreamCodec;
import com.just.codec.stream.StreamCodec;
import com.just.codec.stream.impl.StreamCodecs;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

import com.blib.api.common.codec.v1.BLibCodecs;
import com.blib.mod.BLib;

/**
 * Client → server: request the project's authoritative state for one pool. The server reads the pool JSON from the
 * project's datapack (or seeds it from the live registry encoding if the project hasn't yet authored an override) and
 * replies with {@link S2CPoolDraftPayload}. The Pool Editor fires this when the user picks a pool from the header
 * dropdown so it can render the project's state instead of the live registry's.
 */
public record C2SRequestPoolDraftPayload(
    String projectName,
    ResourceLocation poolId
) implements CustomPacketPayload {

    public static final ResourceLocation PAYLOAD_ID = BLib.MOD.resources().createLocation("request_pool_draft");

    public static final Type<C2SRequestPoolDraftPayload> TYPE = new Type<>(PAYLOAD_ID);

    public static final StreamCodec<C2SRequestPoolDraftPayload> CODEC = RecordStreamCodec.of(
        StreamCodecs.STRING_UTF8,
        C2SRequestPoolDraftPayload::projectName,
        BLibCodecs.Stream.RESOURCE_LOCATION,
        C2SRequestPoolDraftPayload::poolId,
        C2SRequestPoolDraftPayload::new
    );

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
