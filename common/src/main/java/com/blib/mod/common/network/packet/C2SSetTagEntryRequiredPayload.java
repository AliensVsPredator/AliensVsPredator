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
 * Client → server: flip a tag entry's {@code required} flag at {@code rawIndex} in the project's tag JSON. Used by
 * the inspector's "req / opt" badge — clicking the badge toggles the bare-string / object form vanilla tag JSON uses
 * for the two states. Disk-only; the live registry doesn't reflect the change until Reload Project. Server echoes a
 * fresh {@link S2CTagDraftPayload} so the source view stays in sync.
 */
public record C2SSetTagEntryRequiredPayload(
    String projectName,
    ResourceLocation registryKey,
    ResourceLocation tagId,
    int rawIndex,
    boolean required
) implements CustomPacketPayload {

    public static final ResourceLocation PAYLOAD_ID = BLib.MOD.resources().createLocation("set_tag_entry_required");

    public static final Type<C2SSetTagEntryRequiredPayload> TYPE = new Type<>(PAYLOAD_ID);

    public static final StreamCodec<C2SSetTagEntryRequiredPayload> CODEC = RecordStreamCodec.of(
        StreamCodecs.STRING_UTF8,
        C2SSetTagEntryRequiredPayload::projectName,
        BLibCodecs.Stream.RESOURCE_LOCATION,
        C2SSetTagEntryRequiredPayload::registryKey,
        BLibCodecs.Stream.RESOURCE_LOCATION,
        C2SSetTagEntryRequiredPayload::tagId,
        StreamCodecs.INT,
        C2SSetTagEntryRequiredPayload::rawIndex,
        StreamCodecs.BOOLEAN,
        C2SSetTagEntryRequiredPayload::required,
        C2SSetTagEntryRequiredPayload::new
    );

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
