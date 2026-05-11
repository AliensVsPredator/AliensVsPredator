package com.blib.mod.common.network.packet;

import com.just.codec.stream.RecordStreamCodec;
import com.just.codec.stream.StreamCodec;
import com.just.codec.stream.impl.StreamCodecs;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

import com.blib.api.common.codec.v1.BLibCodecs;
import com.blib.api.common.faction.v1.ClaimVisibility;
import com.blib.api.common.faction.v1.ProtectionMode;
import com.blib.mod.BLib;

/**
 * Server → client: full editable state for one faction. Sent in response to {@link C2SRequestFactionInspectionPayload}
 * and pushed proactively when the inspected faction's state changes server-side. The universal Inspector renders this
 * via its {@code case FACTION} branch.
 * <p>
 * Enum fields are exposed as proper enum types in the record, but the wire format encodes each as an ordinal int. The
 * codec uses method-reference adapters ({@link #claimVisibilityOrdinal} etc.) so the compiler can resolve the
 * heavily-overloaded {@code RecordStreamCodec.of} cleanly without lambda type-inference grief.
 */
public record S2CFactionInspectionPayload(
    ResourceLocation id,
    String name,
    int color,
    ResourceLocation typeId,
    ClaimVisibility claimVisibility,
    ProtectionMode blockBreakProtection,
    ProtectionMode blockInteractProtection,
    ProtectionMode entityInteractProtection,
    ProtectionMode nonLivingEntityAttackProtection,
    boolean allowPvp,
    boolean allowExplosions,
    boolean allowMobGriefing
) implements CustomPacketPayload {

    public static final ResourceLocation PAYLOAD_ID = BLib.MOD.resources().createLocation("faction_inspection");

    public static final Type<S2CFactionInspectionPayload> TYPE = new Type<>(PAYLOAD_ID);

    public int claimVisibilityOrdinal() {
        return claimVisibility.ordinal();
    }

    public int blockBreakProtectionOrdinal() {
        return blockBreakProtection.ordinal();
    }

    public int blockInteractProtectionOrdinal() {
        return blockInteractProtection.ordinal();
    }

    public int entityInteractProtectionOrdinal() {
        return entityInteractProtection.ordinal();
    }

    public int nonLivingEntityAttackProtectionOrdinal() {
        return nonLivingEntityAttackProtection.ordinal();
    }

    public static final StreamCodec<S2CFactionInspectionPayload> CODEC = RecordStreamCodec.of(
        BLibCodecs.Stream.RESOURCE_LOCATION,
        S2CFactionInspectionPayload::id,
        StreamCodecs.STRING_UTF8,
        S2CFactionInspectionPayload::name,
        StreamCodecs.INT,
        S2CFactionInspectionPayload::color,
        BLibCodecs.Stream.RESOURCE_LOCATION,
        S2CFactionInspectionPayload::typeId,
        StreamCodecs.INT,
        S2CFactionInspectionPayload::claimVisibilityOrdinal,
        StreamCodecs.INT,
        S2CFactionInspectionPayload::blockBreakProtectionOrdinal,
        StreamCodecs.INT,
        S2CFactionInspectionPayload::blockInteractProtectionOrdinal,
        StreamCodecs.INT,
        S2CFactionInspectionPayload::entityInteractProtectionOrdinal,
        StreamCodecs.INT,
        S2CFactionInspectionPayload::nonLivingEntityAttackProtectionOrdinal,
        StreamCodecs.BOOLEAN,
        S2CFactionInspectionPayload::allowPvp,
        StreamCodecs.BOOLEAN,
        S2CFactionInspectionPayload::allowExplosions,
        StreamCodecs.BOOLEAN,
        S2CFactionInspectionPayload::allowMobGriefing,
        S2CFactionInspectionPayload::fromOrdinals
    );

    /**
     * Reconstruct from wire form. Bounds-checks each ordinal so a malformed packet can't AIOOB; out-of-range values
     * fall back to the most-restrictive enum entry.
     */
    public static S2CFactionInspectionPayload fromOrdinals(
        ResourceLocation id,
        String name,
        int color,
        ResourceLocation typeId,
        int claimVisibilityOrdinal,
        int blockBreakOrdinal,
        int blockInteractOrdinal,
        int entityInteractOrdinal,
        int nonLivingEntityAttackOrdinal,
        boolean allowPvp,
        boolean allowExplosions,
        boolean allowMobGriefing
    ) {
        return new S2CFactionInspectionPayload(
            id,
            name,
            color,
            typeId,
            decodeClaimVisibility(claimVisibilityOrdinal),
            decodeProtectionMode(blockBreakOrdinal),
            decodeProtectionMode(blockInteractOrdinal),
            decodeProtectionMode(entityInteractOrdinal),
            decodeProtectionMode(nonLivingEntityAttackOrdinal),
            allowPvp,
            allowExplosions,
            allowMobGriefing
        );
    }

    private static ClaimVisibility decodeClaimVisibility(int ordinal) {
        var values = ClaimVisibility.values();
        return ordinal >= 0 && ordinal < values.length ? values[ordinal] : ClaimVisibility.PRIVATE;
    }

    private static ProtectionMode decodeProtectionMode(int ordinal) {
        var values = ProtectionMode.values();
        return ordinal >= 0 && ordinal < values.length ? values[ordinal] : ProtectionMode.PRIVATE;
    }

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
