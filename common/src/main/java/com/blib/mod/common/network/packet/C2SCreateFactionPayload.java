package com.blib.mod.common.network.packet;

import com.just.codec.stream.RecordStreamCodec;
import com.just.codec.stream.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

import com.blib.api.common.codec.v1.BLibCodecs;
import com.blib.mod.BLib;

/**
 * Client → server: create a new faction with id {@code factionId} and type {@code typeId}. Triggered by the Faction
 * Browser's "New Faction" dialog. Op-gated; server resolves {@code typeId} against the
 * {@code BLibBuiltInRegistries.FACTION_DATA_TYPES} registry and rejects unknown types. On success the directory push
 * fires automatically.
 */
public record C2SCreateFactionPayload(
    ResourceLocation factionId,
    ResourceLocation typeId
) implements CustomPacketPayload {

    public static final ResourceLocation PAYLOAD_ID = BLib.MOD.resources().createLocation("create_faction");

    public static final Type<C2SCreateFactionPayload> TYPE = new Type<>(PAYLOAD_ID);

    public static final StreamCodec<C2SCreateFactionPayload> CODEC = RecordStreamCodec.of(
        BLibCodecs.Stream.RESOURCE_LOCATION,
        C2SCreateFactionPayload::factionId,
        BLibCodecs.Stream.RESOURCE_LOCATION,
        C2SCreateFactionPayload::typeId,
        C2SCreateFactionPayload::new
    );

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
