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
 * Client → server: create a brand-new empty tag in the project's datapack. Server refuses if the file already exists
 * for the project; replies with a refreshed {@link S2CTagCatalogPayload} (so the browser shows the new tag) and an
 * {@link S2CTagDraftPayload} (so a client that just opened the editor finds it populated).
 */
public record C2SCreateTagPayload(
    String projectName,
    ResourceLocation registryKey,
    ResourceLocation tagId
) implements CustomPacketPayload {

    public static final ResourceLocation PAYLOAD_ID = BLib.MOD.resources().createLocation("create_tag");

    public static final Type<C2SCreateTagPayload> TYPE = new Type<>(PAYLOAD_ID);

    public static final StreamCodec<C2SCreateTagPayload> CODEC = RecordStreamCodec.of(
        StreamCodecs.STRING_UTF8,
        C2SCreateTagPayload::projectName,
        BLibCodecs.Stream.RESOURCE_LOCATION,
        C2SCreateTagPayload::registryKey,
        BLibCodecs.Stream.RESOURCE_LOCATION,
        C2SCreateTagPayload::tagId,
        C2SCreateTagPayload::new
    );

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
