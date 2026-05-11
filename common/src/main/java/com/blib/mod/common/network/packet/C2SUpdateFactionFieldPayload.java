package com.blib.mod.common.network.packet;

import com.just.codec.stream.RecordStreamCodec;
import com.just.codec.stream.StreamCodec;
import com.just.codec.stream.impl.StreamCodecs;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

import com.blib.api.common.codec.v1.BLibCodecs;
import com.blib.mod.BLib;

/**
 * Client → server: update one editable field on a faction. The {@code fieldOrdinal} is the ordinal of {@link Field};
 * {@code value} is a string parsed server-side based on the discriminator (parsed as int / hex color / boolean / enum
 * name as appropriate). Mirrors {@code C2SUpdateJigsawBlockPayload}'s discriminator pattern but more compact since
 * faction fields are scalar.
 * <p>
 * On success the inspection push (and the directory push, if name/color changed) fire automatically.
 */
public record C2SUpdateFactionFieldPayload(
    ResourceLocation factionId,
    int fieldOrdinal,
    String value
) implements CustomPacketPayload {

    /** Discriminator for which scalar to update on the faction. Wire-encoded as ordinal int. */
    public enum Field {
        NAME,
        COLOR,
        CLAIM_VISIBILITY,
        BLOCK_BREAK_PROTECTION,
        BLOCK_INTERACT_PROTECTION,
        ENTITY_INTERACT_PROTECTION,
        NONLIVING_ENTITY_ATTACK_PROTECTION,
        ALLOW_PVP,
        ALLOW_EXPLOSIONS,
        ALLOW_MOB_GRIEFING
    }

    public static final ResourceLocation PAYLOAD_ID = BLib.MOD.resources().createLocation("update_faction_field");

    public static final Type<C2SUpdateFactionFieldPayload> TYPE = new Type<>(PAYLOAD_ID);

    public static final StreamCodec<C2SUpdateFactionFieldPayload> CODEC = RecordStreamCodec.of(
        BLibCodecs.Stream.RESOURCE_LOCATION,
        C2SUpdateFactionFieldPayload::factionId,
        StreamCodecs.INT,
        C2SUpdateFactionFieldPayload::fieldOrdinal,
        StreamCodecs.STRING_UTF8,
        C2SUpdateFactionFieldPayload::value,
        C2SUpdateFactionFieldPayload::new
    );

    public static C2SUpdateFactionFieldPayload of(ResourceLocation factionId, Field field, String value) {
        return new C2SUpdateFactionFieldPayload(factionId, field.ordinal(), value);
    }

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
