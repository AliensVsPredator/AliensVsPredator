package com.blib.internal.client.faction;

import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import com.blib.api.common.faction.v1.ClaimVisibility;
import com.blib.api.common.faction.v1.ProtectionMode;
import com.blib.mod.common.network.packet.S2CFactionInspectionPayload;

/**
 * Single-faction inspection snapshot the universal Inspector reads when a
 * {@link com.blib.engine.selection.FactionSelectable} is the active selection. Populated by
 * {@link S2CFactionInspectionPayload}: requested on selection swap and pushed proactively when the inspected faction's
 * state changes server-side.
 * <p>
 * Holds at most one snapshot — selection swaps clear-and-replace. Stale-reply guard: if a reply arrives for a faction
 * that's no longer the active inspection, callers can compare {@link ClientFactionInspection#id} against the active
 * selection to decide whether to keep it; this cache itself is permissive and stores whatever the latest payload
 * carried.
 */
@ApiStatus.Internal
public final class ClientFactionInspectionCache {

    public record ClientFactionInspection(
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
    ) {}

    private static @Nullable ClientFactionInspection current;

    private ClientFactionInspectionCache() {}

    public static @Nullable ClientFactionInspection current() {
        return current;
    }

    public static void apply(S2CFactionInspectionPayload payload) {
        current = new ClientFactionInspection(
            payload.id(),
            payload.name(),
            payload.color(),
            payload.typeId(),
            payload.claimVisibility(),
            payload.blockBreakProtection(),
            payload.blockInteractProtection(),
            payload.entityInteractProtection(),
            payload.nonLivingEntityAttackProtection(),
            payload.allowPvp(),
            payload.allowExplosions(),
            payload.allowMobGriefing()
        );
    }

    public static void clear() {
        current = null;
    }
}
