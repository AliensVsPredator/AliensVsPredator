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

/**
 * Server → client: the authoritative project state for one pool, expressed as a list of {@link DraftPoolElement} rows.
 * Sent after every successful pool edit (update / add / remove) and in response to {@link C2SRequestPoolDraftPayload}
 * when the editor opens a pool. The client's {@code ProjectDraftCache} stores this and the Pool Editor renders from the
 * cache instead of the live registry — registry no longer reflects edits until the user runs Reload Project.
 */
public record S2CPoolDraftPayload(
    String projectName,
    ResourceLocation poolId,
    List<DraftPoolElement> elements
) implements CustomPacketPayload {

    public static final ResourceLocation PAYLOAD_ID = BLib.MOD.resources().createLocation("pool_draft");

    public static final Type<S2CPoolDraftPayload> TYPE = new Type<>(PAYLOAD_ID);

    public static final StreamCodec<S2CPoolDraftPayload> CODEC = RecordStreamCodec.of(
        StreamCodecs.STRING_UTF8,
        S2CPoolDraftPayload::projectName,
        BLibCodecs.Stream.RESOURCE_LOCATION,
        S2CPoolDraftPayload::poolId,
        DraftPoolElement.CODEC.asList(),
        S2CPoolDraftPayload::elements,
        S2CPoolDraftPayload::new
    );

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
