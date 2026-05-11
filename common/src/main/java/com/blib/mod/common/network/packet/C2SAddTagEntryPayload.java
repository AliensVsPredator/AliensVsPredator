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
 * Client → server: append a new entry to a tag's project override JSON. {@code isTagRef=true} writes a
 * {@code #}-prefixed tag reference; {@code false} writes a direct registry-element id. Disk-only — the live registry
 * doesn't reflect the change until Reload Project. Server echoes an {@link S2CTagDraftPayload} after the disk write.
 */
public record C2SAddTagEntryPayload(
    String projectName,
    ResourceLocation registryKey,
    ResourceLocation tagId,
    boolean isTagRef,
    ResourceLocation entryId,
    boolean required
) implements CustomPacketPayload {

    public static final ResourceLocation PAYLOAD_ID = BLib.MOD.resources().createLocation("add_tag_entry");

    public static final Type<C2SAddTagEntryPayload> TYPE = new Type<>(PAYLOAD_ID);

    public static final StreamCodec<C2SAddTagEntryPayload> CODEC = RecordStreamCodec.of(
        StreamCodecs.STRING_UTF8,
        C2SAddTagEntryPayload::projectName,
        BLibCodecs.Stream.RESOURCE_LOCATION,
        C2SAddTagEntryPayload::registryKey,
        BLibCodecs.Stream.RESOURCE_LOCATION,
        C2SAddTagEntryPayload::tagId,
        StreamCodecs.BOOLEAN,
        C2SAddTagEntryPayload::isTagRef,
        BLibCodecs.Stream.RESOURCE_LOCATION,
        C2SAddTagEntryPayload::entryId,
        StreamCodecs.BOOLEAN,
        C2SAddTagEntryPayload::required,
        C2SAddTagEntryPayload::new
    );

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
