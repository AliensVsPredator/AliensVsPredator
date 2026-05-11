package com.blib.mod.common.network.packet;

import com.just.codec.stream.RecordStreamCodec;
import com.just.codec.stream.StreamCodec;
import com.just.codec.stream.impl.StreamCodecs;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

import com.blib.api.common.codec.v1.BLibCodecs;
import com.blib.api.common.faction.v1.RelationshipState;
import com.blib.mod.BLib;

/**
 * Client → server: set the pairwise relationship state between two factions. Triggered by the Diplomacy Matrix when the
 * user clicks a cell. The server normalizes the pair (lex-min) and stores once; the directory push then re-syncs all
 * clients. State is wire-encoded as ordinal int.
 */
public record C2SSetFactionRelationshipPayload(
    ResourceLocation factionA,
    ResourceLocation factionB,
    int stateOrdinal
) implements CustomPacketPayload {

    public static final ResourceLocation PAYLOAD_ID = BLib.MOD.resources().createLocation("set_faction_relationship");

    public static final Type<C2SSetFactionRelationshipPayload> TYPE = new Type<>(PAYLOAD_ID);

    public static final StreamCodec<C2SSetFactionRelationshipPayload> CODEC = RecordStreamCodec.of(
        BLibCodecs.Stream.RESOURCE_LOCATION,
        C2SSetFactionRelationshipPayload::factionA,
        BLibCodecs.Stream.RESOURCE_LOCATION,
        C2SSetFactionRelationshipPayload::factionB,
        StreamCodecs.INT,
        C2SSetFactionRelationshipPayload::stateOrdinal,
        C2SSetFactionRelationshipPayload::new
    );

    public static C2SSetFactionRelationshipPayload of(ResourceLocation a, ResourceLocation b, RelationshipState state) {
        return new C2SSetFactionRelationshipPayload(a, b, state.ordinal());
    }

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
