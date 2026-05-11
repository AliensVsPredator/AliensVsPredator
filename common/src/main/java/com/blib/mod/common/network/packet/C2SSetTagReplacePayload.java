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
 * Client → server: set a tag's {@code replace} flag. {@code true} causes the project's override to wholesale-replace
 * any vanilla / upstream entries for the same tag id post-reload; {@code false} (default) merges with them. Triggered
 * by the Tag Editor's header Replace toggle.
 */
public record C2SSetTagReplacePayload(
    String projectName,
    ResourceLocation registryKey,
    ResourceLocation tagId,
    boolean replace
) implements CustomPacketPayload {

    public static final ResourceLocation PAYLOAD_ID = BLib.MOD.resources().createLocation("set_tag_replace");

    public static final Type<C2SSetTagReplacePayload> TYPE = new Type<>(PAYLOAD_ID);

    public static final StreamCodec<C2SSetTagReplacePayload> CODEC = RecordStreamCodec.of(
        StreamCodecs.STRING_UTF8,
        C2SSetTagReplacePayload::projectName,
        BLibCodecs.Stream.RESOURCE_LOCATION,
        C2SSetTagReplacePayload::registryKey,
        BLibCodecs.Stream.RESOURCE_LOCATION,
        C2SSetTagReplacePayload::tagId,
        StreamCodecs.BOOLEAN,
        C2SSetTagReplacePayload::replace,
        C2SSetTagReplacePayload::new
    );

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
