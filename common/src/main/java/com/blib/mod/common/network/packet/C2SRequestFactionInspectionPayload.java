package com.blib.mod.common.network.packet;

import com.just.codec.stream.RecordStreamCodec;
import com.just.codec.stream.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

import com.blib.api.common.codec.v1.BLibCodecs;
import com.blib.mod.BLib;

/**
 * Client → server: ask for one faction's full editable state. Triggered by the universal Inspector when a
 * {@link com.blib.engine.domain.selection.picking.FactionSelectable} becomes the active selection. Reply is
 * {@link S2CFactionInspectionPayload}.
 */
public record C2SRequestFactionInspectionPayload(ResourceLocation factionId) implements CustomPacketPayload {

    public static final ResourceLocation PAYLOAD_ID = BLib.MOD.resources().createLocation("request_faction_inspection");

    public static final Type<C2SRequestFactionInspectionPayload> TYPE = new Type<>(PAYLOAD_ID);

    public static final StreamCodec<C2SRequestFactionInspectionPayload> CODEC = RecordStreamCodec.of(
        BLibCodecs.Stream.RESOURCE_LOCATION,
        C2SRequestFactionInspectionPayload::factionId,
        C2SRequestFactionInspectionPayload::new
    );

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
