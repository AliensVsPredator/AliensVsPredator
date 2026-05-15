package com.blib.api.client.engine.v1.selection;

import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

import com.blib.internal.client.faction.ClientFactionInspectionCache;

/**
 * Public read view over the engine's per-selection faction inspection snapshot. The engine requests fresh data from the
 * server whenever the selection swaps to a new faction; the resulting snapshot is cached client-side and surfaced here
 * so downstream inspector sections can read it without depending on BLib internals.
 * <p>
 * Returns {@code null} when no faction is selected or the snapshot hasn't landed yet. Callers should render a
 * "(loading…)" placeholder in that case rather than treating it as an error.
 */
public interface FactionInspectionView {

    /** Faction the snapshot describes. */
    ResourceLocation factionId();

    /** Registered faction-type id (e.g. {@code yourmod:my_faction_kind}). Use this to filter for your own factions. */
    ResourceLocation typeId();

    /** Display name as known to the server at snapshot time. */
    String name();

    /** ARGB color in the high byte; the engine renders swatches against this. */
    int color();

    /** Latest snapshot for the currently-selected faction, if any. */
    @Nullable
    static FactionInspectionView current() {
        var snapshot = ClientFactionInspectionCache.current();
        if (snapshot == null) {
            return null;
        }
        return new FactionInspectionView() {

            @Override
            public ResourceLocation factionId() {
                return snapshot.id();
            }

            @Override
            public ResourceLocation typeId() {
                return snapshot.typeId();
            }

            @Override
            public String name() {
                return snapshot.name();
            }

            @Override
            public int color() {
                return snapshot.color();
            }
        };
    }
}
