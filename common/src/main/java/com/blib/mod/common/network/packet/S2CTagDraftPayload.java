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
 * Server → client: the project's authoritative state for one tag, expressed as the {@code replace} flag plus the list
 * of {@link TagEntryDraft} rows. Sent in response to {@link C2SRequestTagDraftPayload} and after every successful tag
 * mutation. The client's tag draft cache stores this and the Tag Editor renders from the cache instead of the live
 * registry — registry no longer reflects edits until the user runs Reload Project.
 */
public record S2CTagDraftPayload(
    String projectName,
    ResourceLocation registryKey,
    ResourceLocation tagId,
    boolean replace,
    List<TagEntryDraft> entries
) implements CustomPacketPayload {

    public static final ResourceLocation PAYLOAD_ID = BLib.MOD.resources().createLocation("tag_draft");

    public static final Type<S2CTagDraftPayload> TYPE = new Type<>(PAYLOAD_ID);

    public static final StreamCodec<S2CTagDraftPayload> CODEC = RecordStreamCodec.of(
        StreamCodecs.STRING_UTF8,
        S2CTagDraftPayload::projectName,
        BLibCodecs.Stream.RESOURCE_LOCATION,
        S2CTagDraftPayload::registryKey,
        BLibCodecs.Stream.RESOURCE_LOCATION,
        S2CTagDraftPayload::tagId,
        StreamCodecs.BOOLEAN,
        S2CTagDraftPayload::replace,
        TagEntryDraft.CODEC.asList(),
        S2CTagDraftPayload::entries,
        S2CTagDraftPayload::new
    );

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
