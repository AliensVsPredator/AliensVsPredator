package com.blib.mod.common.network.packet;

import com.just.codec.stream.RecordStreamCodec;
import com.just.codec.stream.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

import com.blib.api.common.codec.v1.BLibCodecs;
import com.blib.mod.BLib;

/**
 * Client → server: ask for the member roster of one faction. Triggered by the Members panel when its active faction id
 * changes. Reply is {@link S2CFactionMembersPayload}.
 */
public record C2SRequestFactionMembersPayload(ResourceLocation factionId) implements CustomPacketPayload {

    public static final ResourceLocation PAYLOAD_ID = BLib.MOD.resources().createLocation("request_faction_members");

    public static final Type<C2SRequestFactionMembersPayload> TYPE = new Type<>(PAYLOAD_ID);

    public static final StreamCodec<C2SRequestFactionMembersPayload> CODEC = RecordStreamCodec.of(
        BLibCodecs.Stream.RESOURCE_LOCATION,
        C2SRequestFactionMembersPayload::factionId,
        C2SRequestFactionMembersPayload::new
    );

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
