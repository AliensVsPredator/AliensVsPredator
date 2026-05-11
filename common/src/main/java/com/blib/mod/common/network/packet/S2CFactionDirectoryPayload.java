package com.blib.mod.common.network.packet;

import com.just.codec.stream.RecordStreamCodec;
import com.just.codec.stream.StreamCodec;
import com.just.codec.stream.impl.StreamCodecs;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

import java.util.List;

import com.blib.api.common.codec.v1.BLibCodecs;
import com.blib.mod.BLib;

/**
 * Server → client: full faction directory snapshot. Carries every faction's id / name / color / memberCount / typeId,
 * plus the entire pairwise relationship table. Sent in response to {@link C2SRequestFactionDirectoryPayload} on
 * workspace open and pushed proactively to all clients after any faction add / remove / metadata-change /
 * relationship-change. The Faction Browser and Diplomacy Matrix read entirely from the cache populated by this payload.
 * <p>
 * Enum states are wire-encoded as ordinal ints; the client converts back to {@code RelationshipState} when applying.
 */
public record S2CFactionDirectoryPayload(
    List<FactionEntry> factions,
    List<RelationshipEntry> relationships
) implements CustomPacketPayload {

    public record FactionEntry(
        ResourceLocation id,
        String name,
        int color,
        int memberCount,
        ResourceLocation typeId
    ) {

        public static final StreamCodec<FactionEntry> CODEC = RecordStreamCodec.of(
            BLibCodecs.Stream.RESOURCE_LOCATION,
            FactionEntry::id,
            StreamCodecs.STRING_UTF8,
            FactionEntry::name,
            StreamCodecs.INT,
            FactionEntry::color,
            StreamCodecs.INT,
            FactionEntry::memberCount,
            BLibCodecs.Stream.RESOURCE_LOCATION,
            FactionEntry::typeId,
            FactionEntry::new
        );
    }

    public record RelationshipEntry(
        ResourceLocation factionA,
        ResourceLocation factionB,
        int stateOrdinal
    ) {

        public static final StreamCodec<RelationshipEntry> CODEC = RecordStreamCodec.of(
            BLibCodecs.Stream.RESOURCE_LOCATION,
            RelationshipEntry::factionA,
            BLibCodecs.Stream.RESOURCE_LOCATION,
            RelationshipEntry::factionB,
            StreamCodecs.INT,
            RelationshipEntry::stateOrdinal,
            RelationshipEntry::new
        );
    }

    public static final ResourceLocation PAYLOAD_ID = BLib.MOD.resources().createLocation("faction_directory");

    public static final Type<S2CFactionDirectoryPayload> TYPE = new Type<>(PAYLOAD_ID);

    public static final StreamCodec<S2CFactionDirectoryPayload> CODEC = RecordStreamCodec.of(
        FactionEntry.CODEC.asList(),
        S2CFactionDirectoryPayload::factions,
        RelationshipEntry.CODEC.asList(),
        S2CFactionDirectoryPayload::relationships,
        S2CFactionDirectoryPayload::new
    );

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
