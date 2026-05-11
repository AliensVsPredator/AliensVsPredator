package com.blib.internal.client.faction;

import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import java.util.List;

import com.blib.mod.common.network.packet.S2CFactionMembersPayload;

/**
 * Member roster for the workspace's currently-inspected faction. Populated by {@link S2CFactionMembersPayload} —
 * requested when the active selection becomes a {@link com.blib.engine.selection.FactionSelectable} (or its faction id
 * changes) and pushed proactively when the server adds / removes a member of that faction.
 * <p>
 * Single-faction at-a-time cache: switching the active faction in the Browser overwrites the prior roster. The Members
 * panel shows "(select a faction in the Browser)" when {@link #factionId} is null and the selection isn't a faction.
 */
@ApiStatus.Internal
public final class ClientFactionMembersCache {

    private static @Nullable ResourceLocation factionId;

    private static List<S2CFactionMembersPayload.MemberEntry> members = List.of();

    private ClientFactionMembersCache() {}

    public static @Nullable ResourceLocation factionId() {
        return factionId;
    }

    /**
     * Member roster of the cached faction. Each entry carries a UUID and a display name string — the string is empty
     * when the underlying entity isn't currently loaded server-side; the panel renders that as a UUID-only row.
     */
    public static List<S2CFactionMembersPayload.MemberEntry> members() {
        return members;
    }

    public static void apply(S2CFactionMembersPayload payload) {
        factionId = payload.factionId();
        members = List.copyOf(payload.members());
    }

    public static void clear() {
        factionId = null;
        members = List.of();
    }
}
