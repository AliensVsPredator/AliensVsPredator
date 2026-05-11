package com.blib.mod.common.network.packet;

import com.just.codec.stream.RecordStreamCodec;
import com.just.codec.stream.StreamCodec;
import com.just.codec.stream.impl.StreamCodecs;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

import com.blib.mod.BLib;

/**
 * Client → server: list every faction whose membership contains the given entity UUID. Powers the engine's entity
 * Inspector "Factions" section and the right-click "Manage Factions" popup, both of which need a reverse-lookup (entity
 * → factions) that the regular {@code S2CFactionMembersPayload} (faction → members) doesn't provide. Server replies
 * with {@link S2CEntityFactionsPayload}. Workspace is op-gated upstream; this read isn't separately gated.
 */
public record C2SRequestEntityFactionsPayload(
    UUID memberUuid
) implements CustomPacketPayload {

    public static final ResourceLocation PAYLOAD_ID = BLib.MOD.resources().createLocation("request_entity_factions");

    public static final Type<C2SRequestEntityFactionsPayload> TYPE = new Type<>(PAYLOAD_ID);

    public static final StreamCodec<C2SRequestEntityFactionsPayload> CODEC = RecordStreamCodec.of(
        StreamCodecs.UUID,
        C2SRequestEntityFactionsPayload::memberUuid,
        C2SRequestEntityFactionsPayload::new
    );

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
