package com.blib.engine.net;

import net.minecraft.client.Minecraft;
import org.jetbrains.annotations.ApiStatus;

import java.util.ArrayList;
import java.util.List;

import com.blib.engine.domain.selection.picking.EntitySelectable;
import com.blib.engine.domain.selection.picking.Selection;
import com.blib.engine.domain.selection.picking.SelectionManager;
import com.blib.engine.domain.selection.picking.event.SelectionChangedEvent;
import com.blib.engine.runtime.EventBus;
import com.blib.mod.BLib;
import com.blib.mod.client.render.debug.PathfindingDebugState;
import com.blib.mod.client.render.debug.PathfindingSearchDebugRenderer;
import com.blib.mod.client.render.goap.GOAPDebugState;
import com.blib.mod.common.network.packet.C2SGOAPTrackPayload;

/**
 * Mirrors the engine selection into the server-side GOAP debug tracker. The GOAP and pathfinding panels are selection-
 * driven, so selecting entities is enough to start the debug stream; selecting anything else clears the stream.
 */
@ApiStatus.Internal
public final class GOAPSelectionNetAdapter {

    private static List<Integer> lastEntityIds = List.of();

    private GOAPSelectionNetAdapter() {}

    public static void install() {
        EventBus.get().subscribe(SelectionChangedEvent.class, event -> sync(event.current()));
        sync(SelectionManager.current());
    }

    public static void clear() {
        var shouldUntrack = !lastEntityIds.isEmpty();
        lastEntityIds = List.of();
        clearClientDebugState();
        if (shouldUntrack) {
            sendTrack(List.of());
        }
    }

    private static void sync(Selection selection) {
        var entityIds = selectedEntityIds(selection);
        if (entityIds.equals(lastEntityIds)) {
            return;
        }

        lastEntityIds = List.copyOf(entityIds);
        clearClientDebugState();
        sendTrack(entityIds);
    }

    private static List<Integer> selectedEntityIds(Selection selection) {
        if (selection.isEmpty()) {
            return List.of();
        }

        var ids = new ArrayList<Integer>();
        for (var item : selection.items()) {
            if (!(item instanceof EntitySelectable entitySelectable)) {
                continue;
            }
            var entity = entitySelectable.entity();
            if (entity != null) {
                ids.add(entity.getId());
            }
        }
        return ids;
    }

    private static void clearClientDebugState() {
        GOAPDebugState.INSTANCE.clear();
        PathfindingDebugState.INSTANCE.clear();
        PathfindingSearchDebugRenderer.INSTANCE.clearAll();
    }

    private static void sendTrack(List<Integer> entityIds) {
        var mc = Minecraft.getInstance();
        if (mc.player == null || mc.getConnection() == null) {
            return;
        }
        BLib.MOD.networking().sendToServer(new C2SGOAPTrackPayload(entityIds));
    }
}
