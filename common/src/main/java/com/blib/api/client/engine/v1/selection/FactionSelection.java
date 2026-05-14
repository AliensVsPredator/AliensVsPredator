package com.blib.api.client.engine.v1.selection;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

/**
 * Public view over the engine workspace's "a faction is currently picked" selection state. Downstream mods receive an
 * instance of this in {@link com.blib.api.client.engine.v1.inspector.InspectorSection#render} when they declare
 * {@code selectableType() == FactionSelection.class}.
 * <p>
 * The faction may be of any registered faction type — downstream sections typically filter by reading
 * {@link com.blib.api.client.engine.v1.selection.FactionInspectionView#typeId} from the live inspection cache so they
 * only render for their own faction kinds.
 */
public interface FactionSelection {

    /** Identifier of the currently-selected faction. Stable across the lifetime of the selection. */
    ResourceLocation factionId();

    /**
     * Human-readable label suitable for inspector headers. Backed by the engine's directory cache; falls back to the
     * raw id while the directory entry is in flight.
     */
    Component displayName();
}
