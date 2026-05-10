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
 * Client → server: teleport the entity identified by {@code entityId} to {@code (x, y, z)} in dimension
 * {@code dimensionId}. Triggered from the engine workspace's entity translate gizmo on drag release. Free continuous
 * world coordinates — the user may have used Shift to integer-snap on the client, but the wire format carries doubles
 * so the server doesn't need to know about that distinction.
 * <p>
 * Server-side validation: op-gated, refuses player targets, falls back silently if the entity has unloaded or the
 * dimension can't be resolved.
 */
public record C2STranslateEntityPayload(
    int entityId,
    double x,
    double y,
    double z,
    ResourceLocation dimensionId
) implements CustomPacketPayload {

    public static final ResourceLocation PAYLOAD_ID = BLib.MOD.resources().createLocation("translate_entity");

    public static final Type<C2STranslateEntityPayload> TYPE = new Type<>(PAYLOAD_ID);

    public static final StreamCodec<C2STranslateEntityPayload> CODEC = RecordStreamCodec.of(
        StreamCodecs.INT,
        C2STranslateEntityPayload::entityId,
        StreamCodecs.DOUBLE,
        C2STranslateEntityPayload::x,
        StreamCodecs.DOUBLE,
        C2STranslateEntityPayload::y,
        StreamCodecs.DOUBLE,
        C2STranslateEntityPayload::z,
        BLibCodecs.Stream.RESOURCE_LOCATION,
        C2STranslateEntityPayload::dimensionId,
        C2STranslateEntityPayload::new
    );

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
