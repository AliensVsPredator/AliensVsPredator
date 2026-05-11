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
 * Client → server: request the project's authoritative state for one tag. Server lazy-seeds an empty {@code values}
 * array if the project has no override yet (additive merge — vanilla / upstream entries are preserved by the reload),
 * then replies with {@link S2CTagDraftPayload}. Fired by the Tag Editor when the user picks (or changes) the active tag
 * in the header.
 */
public record C2SRequestTagDraftPayload(
    String projectName,
    ResourceLocation registryKey,
    ResourceLocation tagId
) implements CustomPacketPayload {

    public static final ResourceLocation PAYLOAD_ID = BLib.MOD.resources().createLocation("request_tag_draft");

    public static final Type<C2SRequestTagDraftPayload> TYPE = new Type<>(PAYLOAD_ID);

    public static final StreamCodec<C2SRequestTagDraftPayload> CODEC = RecordStreamCodec.of(
        StreamCodecs.STRING_UTF8,
        C2SRequestTagDraftPayload::projectName,
        BLibCodecs.Stream.RESOURCE_LOCATION,
        C2SRequestTagDraftPayload::registryKey,
        BLibCodecs.Stream.RESOURCE_LOCATION,
        C2SRequestTagDraftPayload::tagId,
        C2SRequestTagDraftPayload::new
    );

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
