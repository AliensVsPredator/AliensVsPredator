package com.blib.engine.domain.selection.picking;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.AABB;
import org.jetbrains.annotations.ApiStatus;

import com.blib.api.client.engine.v1.selection.FactionSelection;
import com.blib.internal.client.faction.ClientFactionCache;
import com.blib.internal.client.faction.ClientFactionDirectoryCache;

/**
 * {@link Selectable} wrapping one engine-managed faction by id. Factions don't have a world position, so
 * {@link #worldBounds} is empty and the world-space selection-highlight renderer has nothing to draw — the inspector
 * still picks up the selection and shows the editable view via {@link com.blib.engine.ui.panel.details.DetailsPanel}'s
 * {@code case FACTION} branch.
 * <p>
 * Validity reads through to {@link ClientFactionDirectoryCache} (which is the workspace's authoritative client-side
 * mirror) so a faction deleted server-side gets pruned from the selection on the next read. Display name reads from the
 * directory cache when available, falling back to the global {@link ClientFactionCache} so a fresh selection prior to
 * the directory landing still shows a sensible label.
 */
@ApiStatus.Internal
public final class FactionSelectable implements Selectable, FactionSelection {

    private static final AABB EMPTY_BOUNDS = new AABB(0, 0, 0, 0, 0, 0);

    private final ResourceLocation factionId;

    public FactionSelectable(ResourceLocation factionId) {
        this.factionId = factionId;
    }

    @Override
    public ResourceLocation factionId() {
        return factionId;
    }

    @Override
    public SelectableType type() {
        return SelectableType.FACTION;
    }

    @Override
    public Component displayName() {
        var directoryEntry = ClientFactionDirectoryCache.get(factionId);
        if (directoryEntry != null) {
            return Component.literal(directoryEntry.name());
        }
        var globalEntry = ClientFactionCache.INSTANCE.get(factionId);
        if (globalEntry != null) {
            return Component.literal(globalEntry.name());
        }
        return Component.literal(factionId.toString());
    }

    @Override
    public AABB worldBounds() {
        return EMPTY_BOUNDS;
    }

    @Override
    public boolean isValid() {
        return ClientFactionDirectoryCache.get(factionId) != null
            || ClientFactionCache.INSTANCE.get(factionId) != null;
    }
}
