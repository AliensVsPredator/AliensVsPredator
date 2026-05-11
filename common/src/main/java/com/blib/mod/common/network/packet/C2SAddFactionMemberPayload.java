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
 * Client → server: add an entity (by UUID) to a faction's membership. Triggered by the Members panel's "Add Member"
 * dialog. Op-gated; on success the members + directory pushes fire (member count changes).
 */
public record C2SAddFactionMemberPayload(
    ResourceLocation factionId,
    UUID memberUuid
) implements CustomPacketPayload {

    public static final ResourceLocation PAYLOAD_ID = BLib.MOD.resources().createLocation("add_faction_member");

    public static final Type<C2SAddFactionMemberPayload> TYPE = new Type<>(PAYLOAD_ID);

    public static final StreamCodec<C2SAddFactionMemberPayload> CODEC = RecordStreamCodec.of(
        BLibCodecs.Stream.RESOURCE_LOCATION,
        C2SAddFactionMemberPayload::factionId,
        StreamCodecs.UUID,
        C2SAddFactionMemberPayload::memberUuid,
        C2SAddFactionMemberPayload::new
    );

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
