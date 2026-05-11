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
 * Client → server: delete a tag's JSON file under the project's datapack. The live registry still holds the tag (with
 * the pre-delete merged contents) until the user runs Reload Project — same caveat as deleting a pool or structure.
 * Server replies with a refreshed {@link S2CTagCatalogPayload} so the browser drops the entry.
 */
public record C2SDeleteTagPayload(
    String projectName,
    ResourceLocation registryKey,
    ResourceLocation tagId
) implements CustomPacketPayload {

    public static final ResourceLocation PAYLOAD_ID = BLib.MOD.resources().createLocation("delete_tag");

    public static final Type<C2SDeleteTagPayload> TYPE = new Type<>(PAYLOAD_ID);

    public static final StreamCodec<C2SDeleteTagPayload> CODEC = RecordStreamCodec.of(
        StreamCodecs.STRING_UTF8,
        C2SDeleteTagPayload::projectName,
        BLibCodecs.Stream.RESOURCE_LOCATION,
        C2SDeleteTagPayload::registryKey,
        BLibCodecs.Stream.RESOURCE_LOCATION,
        C2SDeleteTagPayload::tagId,
        C2SDeleteTagPayload::new
    );

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
