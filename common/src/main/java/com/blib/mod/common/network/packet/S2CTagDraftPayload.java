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
 * Server → client: the project's authoritative state for one tag, plus the live registry's expanded member set for the
 * read-only "Resolved" view. {@code entries} is the source-level list straight out of the project's JSON
 * ({@code #}-refs preserved); {@code resolvedMembers} is the post-merge member set the registry currently has, with all
 * references already expanded. Sent in response to {@link C2SRequestTagDraftPayload} and after every successful tag
 * mutation.
 * <p>
 * Resolved members reflect the LAST RELOADED state — pre-reload edits don't show up there until the user runs Reload
 * Project. The Source view is always live (driven by the disk write that happens on every edit packet).
 */
public record S2CTagDraftPayload(
    String projectName,
    ResourceLocation registryKey,
    ResourceLocation tagId,
    boolean replace,
    List<TagEntryDraft> entries,
    List<ResourceLocation> resolvedMembers
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
        BLibCodecs.Stream.RESOURCE_LOCATION.asList(),
        S2CTagDraftPayload::resolvedMembers,
        S2CTagDraftPayload::new
    );

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
