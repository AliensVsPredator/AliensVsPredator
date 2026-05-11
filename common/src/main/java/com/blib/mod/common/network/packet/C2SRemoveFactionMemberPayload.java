package com.blib.mod.common.network.packet;

import com.just.codec.stream.RecordStreamCodec;
import com.just.codec.stream.StreamCodec;
import com.just.codec.stream.impl.StreamCodecs;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

import com.blib.api.common.codec.v1.BLibCodecs;
import com.blib.mod.BLib;

/**
 * Client → server: remove a member (by UUID) from a faction. Triggered by the Members panel's per-row Remove button.
 * Op-gated; on success the members + directory pushes fire.
 */
public record C2SRemoveFactionMemberPayload(
    ResourceLocation factionId,
    UUID memberUuid
) implements CustomPacketPayload {

    public static final ResourceLocation PAYLOAD_ID = BLib.MOD.resources().createLocation("remove_faction_member");

    public static final Type<C2SRemoveFactionMemberPayload> TYPE = new Type<>(PAYLOAD_ID);

    public static final StreamCodec<C2SRemoveFactionMemberPayload> CODEC = RecordStreamCodec.of(
        BLibCodecs.Stream.RESOURCE_LOCATION,
        C2SRemoveFactionMemberPayload::factionId,
        StreamCodecs.UUID,
        C2SRemoveFactionMemberPayload::memberUuid,
        C2SRemoveFactionMemberPayload::new
    );

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
