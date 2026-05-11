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
 * Server → client: member roster for a single faction. Sent in response to {@link C2SRequestFactionMembersPayload} and
 * pushed proactively after a successful add/remove member op for that faction. The Members panel renders this when its
 * faction id matches the active selection.
 * <p>
 * Each member carries a UUID and an optional display name (empty string when the entity isn't currently loaded —
 * resolution is best-effort server-side from the entity registry).
 */
public record S2CFactionMembersPayload(
    ResourceLocation factionId,
    List<MemberEntry> members
) implements CustomPacketPayload {

    public record MemberEntry(
        UUID uuid,
        String displayName
    ) {

        public static final StreamCodec<MemberEntry> CODEC = RecordStreamCodec.of(
            StreamCodecs.UUID,
            MemberEntry::uuid,
            StreamCodecs.STRING_UTF8,
            MemberEntry::displayName,
            MemberEntry::new
        );
    }

    public static final ResourceLocation PAYLOAD_ID = BLib.MOD.resources().createLocation("faction_members");

    public static final Type<S2CFactionMembersPayload> TYPE = new Type<>(PAYLOAD_ID);

    public static final StreamCodec<S2CFactionMembersPayload> CODEC = RecordStreamCodec.of(
        BLibCodecs.Stream.RESOURCE_LOCATION,
        S2CFactionMembersPayload::factionId,
        MemberEntry.CODEC.asList(),
        S2CFactionMembersPayload::members,
        S2CFactionMembersPayload::new
    );

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
