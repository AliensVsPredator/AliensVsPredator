package com.blib.mod.common.network.packet;

import com.just.codec.stream.RecordStreamCodec;
import com.just.codec.stream.StreamCodec;
import com.just.codec.stream.impl.StreamCodecs;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.UUID;

import com.blib.api.common.codec.v1.BLibCodecs;
import com.blib.mod.BLib;

/**
 * Server → client: every faction id whose membership contains {@link #memberUuid}. Sent in response to
 * {@link C2SRequestEntityFactionsPayload} and pushed proactively to the calling player after an add / remove member
 * mutation so the engine's Inspector + manage popup re-render without polling.
 */
public record S2CEntityFactionsPayload(
    UUID memberUuid,
    List<ResourceLocation> factionIds
) implements CustomPacketPayload {

    public static final ResourceLocation PAYLOAD_ID = BLib.MOD.resources().createLocation("entity_factions");

    public static final Type<S2CEntityFactionsPayload> TYPE = new Type<>(PAYLOAD_ID);

    public static final StreamCodec<S2CEntityFactionsPayload> CODEC = RecordStreamCodec.of(
        StreamCodecs.UUID,
        S2CEntityFactionsPayload::memberUuid,
        BLibCodecs.Stream.RESOURCE_LOCATION.asList(),
        S2CEntityFactionsPayload::factionIds,
        S2CEntityFactionsPayload::new
    );

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
