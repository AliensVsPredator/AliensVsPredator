package com.blib.engine.core;

import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import com.blib.engine.domain.selection.entity.EntityGizmoMode;
import com.blib.engine.domain.selection.entity.EntityScaleGizmo;
import com.blib.engine.domain.selection.entity.EntityTranslateGizmo;
import com.blib.engine.domain.selection.picking.SelectionManager;
import com.blib.engine.domain.selection.volume.BlockSelection;
import com.blib.engine.domain.selection.volume.BlockSelectionScaleGizmo;
import com.blib.engine.domain.selection.volume.BlockSelectionTranslateGizmo;
import com.blib.engine.domain.selection.volume.MoveBlocksGizmo;
import com.blib.engine.gizmo.BLibGizmoState;
import com.blib.engine.jigsaw.JigsawPieceSelection;
import com.blib.engine.modeler.gizmo.ModelerGizmoState;
import com.blib.engine.session.EngineMode;
import com.blib.engine.spawn.EntitySpawnSelection;

/**
 * Aggregate read-only view + workspace-close hook for the engine's distributed mutable singletons. Replaces the
 * scattered "every subsystem reaches into the gizmo / selection / jigsaw singleton directly" pattern with a single
 * canonical accessor — code that needs the full picture (workspace teardown, debug HUD, tests) can ask for it here
 * instead of importing every singleton individually.
 * <p>
 * This is a transitional facade. Full dependency-injection of the underlying state is out of scope for this pass; the
 * methods below intentionally delegate to existing singletons so existing call sites continue working. The value is:
 * <ol>
 * <li>One import to remember the engine's writable surface area.</li>
 * <li>A single {@link #clearTransient} method that the workspace's {@code removed()} hook can call instead of repeating
 * fifteen {@code Foo.clear()} lines that are prone to drifting out of sync.</li>
 * <li>A place to attach validation / tracing / mocking in future without touching every call site.</li>
 * </ol>
 */
@ApiStatus.Internal
public final class EngineState {

    private EngineState() {}

    /** True while the engine workspace is the active overlay (i.e. an {@link EngineMode#session} is live). */
    public static boolean isActive() {
        return EngineMode.get().isActive();
    }

    public static @Nullable com.blib.engine.session.EngineSession session() {
        return EngineMode.get().session();
    }

    /**
     * Clear every transient piece of mutable state owned by the gizmo, selection, and spawn singletons. Idempotent. The
     * workspace screen's {@code removed()} called these individually; routing through here means new singletons added
     * to the engine just need one entry in this method instead of an edit at the workspace screen.
     */
    public static void clearTransient() {
        SelectionManager.clear();
        BlockSelection.clear();
        BlockSelectionScaleGizmo.clear();
        BlockSelectionTranslateGizmo.clear();
        MoveBlocksGizmo.clear();
        EntityTranslateGizmo.clear();
        EntityScaleGizmo.clear();
        EntityGizmoMode.set(EntityGizmoMode.TRANSLATE);
        JigsawPieceSelection.clear();
        EntitySpawnSelection.clear();
        // Tunable-item + modeler gizmo states retain their preview/drag fields; null them out so the next workspace
        // session starts clean. These singletons don't expose a top-level clear() so we null each piece individually.
        BLibGizmoState.setDrag(null);
        BLibGizmoState.setLastRender(null);
        BLibGizmoState.setPreviewRender(false);
        ModelerGizmoState.setDrag(null);
        ModelerGizmoState.setLastRender(null);
    }
}
