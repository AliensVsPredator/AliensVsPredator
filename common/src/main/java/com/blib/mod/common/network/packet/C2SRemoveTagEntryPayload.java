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
 * Client → server: remove the entry at {@code rawIndex} from a tag's project override JSON. Out-of-range indices are
 * silently ignored — likely a stale client-side reference. Disk-only.
 */
public record C2SRemoveTagEntryPayload(
    String projectName,
    ResourceLocation registryKey,
    ResourceLocation tagId,
    int rawIndex
) implements CustomPacketPayload {

    public static final ResourceLocation PAYLOAD_ID = BLib.MOD.resources().createLocation("remove_tag_entry");

    public static final Type<C2SRemoveTagEntryPayload> TYPE = new Type<>(PAYLOAD_ID);

    public static final StreamCodec<C2SRemoveTagEntryPayload> CODEC = RecordStreamCodec.of(
        StreamCodecs.STRING_UTF8,
        C2SRemoveTagEntryPayload::projectName,
        BLibCodecs.Stream.RESOURCE_LOCATION,
        C2SRemoveTagEntryPayload::registryKey,
        BLibCodecs.Stream.RESOURCE_LOCATION,
        C2SRemoveTagEntryPayload::tagId,
        StreamCodecs.INT,
        C2SRemoveTagEntryPayload::rawIndex,
        C2SRemoveTagEntryPayload::new
    );

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
