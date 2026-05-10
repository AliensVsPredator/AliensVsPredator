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
 * Client → server: set the entity's {@code Attributes.SCALE} attribute to {@code scale}. Triggered from the engine
 * workspace's entity scale gizmo on drag release. The server clamps to {@code [0.1, 4.0]} as a sanity bound; the client
 * gizmo enforces the same range during drag so the user's preview matches what they'll get.
 * <p>
 * Op-gated. Refuses player targets and silently no-ops on entities that don't have the SCALE attribute registered (some
 * mob types may not have it; vanilla added it in 1.20.5 but mods can register entities without it).
 */
public record C2SSetEntityScalePayload(
    int entityId,
    double scale,
    ResourceLocation dimensionId
) implements CustomPacketPayload {

    public static final ResourceLocation PAYLOAD_ID = BLib.MOD.resources().createLocation("set_entity_scale");

    public static final Type<C2SSetEntityScalePayload> TYPE = new Type<>(PAYLOAD_ID);

    public static final StreamCodec<C2SSetEntityScalePayload> CODEC = RecordStreamCodec.of(
        StreamCodecs.INT,
        C2SSetEntityScalePayload::entityId,
        StreamCodecs.DOUBLE,
        C2SSetEntityScalePayload::scale,
        BLibCodecs.Stream.RESOURCE_LOCATION,
        C2SSetEntityScalePayload::dimensionId,
        C2SSetEntityScalePayload::new
    );

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
