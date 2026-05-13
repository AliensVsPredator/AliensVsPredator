package com.blib.engine.tool.gizmo;

import org.jetbrains.annotations.ApiStatus;

import com.blib.engine.domain.selection.entity.EntityGizmoMode;
import com.blib.engine.domain.selection.entity.EntityScaleGizmo;
import com.blib.engine.domain.selection.entity.EntityTranslateGizmo;
import com.blib.engine.domain.selection.picking.EntitySelectable;
import com.blib.engine.domain.selection.picking.SelectionManager;
import com.blib.engine.domain.selection.volume.BlockSelection;
import com.blib.engine.domain.selection.volume.BlockSelectionScaleGizmo;
import com.blib.engine.domain.selection.volume.BlockSelectionTranslateGizmo;
import com.blib.engine.domain.selection.volume.MoveBlocksGizmo;
import com.blib.engine.session.EngineMode;

/**
 * Per-frame gizmo hover update. Prior to this pass every {@code *GizmoRenderer} ran the cursor-vs-handle picker inside
 * its own {@code render()} method and wrote the result into the gizmo's static hover field. That made the gizmo's hover
 * state a <em>side-effect of rendering</em> — picking silently broke whenever a renderer was skipped (panel offscreen,
 * shader missing, etc.), and the hover-set writes prevented the picker from being tested headless. This class moves the
 * hover update out of rendering: the workspace screen calls {@link #tick} once per frame from its render loop, after
 * the camera frame is captured but before any gizmo draws.
 * <p>
 * The gating logic (engine active, selection type, AABB present, gizmo mode, corner-picking state, session non-null)
 * mirrors what each renderer used to do inline. Each gizmo's hover field is cleared when its gating fails; only the
 * winning gizmo for the current mode has its hover updated. Drag-locked gizmos keep their last hovered face/axis so the
 * rendered drag highlight follows the dragged handle.
 */
@ApiStatus.Internal
public final class GizmoHoverPass {

    private GizmoHoverPass() {}

    /** Run all gizmo hover updates for this frame. Safe to call when engine mode is inactive — clears all state. */
    public static void tick() {
        var mode = EngineMode.get();
        if (!mode.isActive()) {
            clearAll();
            return;
        }
        var session = mode.session();
        if (session == null) {
            clearAll();
            return;
        }

        var sel = SelectionManager.current().single();
        var isEntity = sel instanceof EntitySelectable;

        // Block-volume gizmos: only when no entity is selected, an AABB exists, and we're not mid-corner-pick.
        var volumeOk = !isEntity
            && !BlockSelection.aabb().isEmpty()
            && BlockSelection.picking() == BlockSelection.PickingState.NONE;
        if (volumeOk) {
            switch (BlockSelection.gizmoMode()) {
                case SCALE_VOLUME -> tickBlockScale(session);
                case TRANSLATE_VOLUME -> tickBlockTranslate(session);
                case MOVE_BLOCKS -> tickMoveBlocks(session);
            }
        } else {
            BlockSelectionScaleGizmo.setHoveredFace(null);
            BlockSelectionTranslateGizmo.setHoveredAxis(null);
            MoveBlocksGizmo.setHoveredAxis(null);
        }

        // Entity gizmos: only when an entity is selected (and alive).
        if (isEntity) {
            var entity = ((EntitySelectable) sel).entity();
            if (entity != null) {
                switch (EntityGizmoMode.get()) {
                    case TRANSLATE -> tickEntityTranslate(session, entity);
                    case SCALE -> tickEntityScale(session, entity);
                }
            } else {
                EntityTranslateGizmo.setHoveredAxis(null);
                EntityScaleGizmo.setHovered(false);
            }
        } else {
            EntityTranslateGizmo.setHoveredAxis(null);
            EntityScaleGizmo.setHovered(false);
        }
    }

    private static void clearAll() {
        // Registered adapters get cleared through the registry; legacy non-adapted gizmos still need direct calls.
        // Phase-2 of the gizmo unification moves these direct calls away as each gizmo gets a registered adapter.
        GizmoRegistry.clearAllHover();
        BlockSelectionTranslateGizmo.setHoveredAxis(null);
        MoveBlocksGizmo.setHoveredAxis(null);
        EntityTranslateGizmo.setHoveredAxis(null);
        // BlockSelectionScaleGizmo + EntityScaleGizmo are now adapter-registered; clearAllHover above covers them.
        BlockSelectionScaleGizmo.setHoveredFace(null);
        EntityScaleGizmo.setHovered(false);
    }

    private static void tickBlockScale(com.blib.engine.session.EngineSession session) {
        var dragging = BlockSelectionScaleGizmo.draggingFace();
        BlockSelectionScaleGizmo.setHoveredFace(
            dragging != null ? dragging : BlockSelectionScaleGizmo.pickUnderCursor(session)
        );
        BlockSelectionTranslateGizmo.setHoveredAxis(null);
        MoveBlocksGizmo.setHoveredAxis(null);
    }

    private static void tickBlockTranslate(com.blib.engine.session.EngineSession session) {
        var dragging = BlockSelectionTranslateGizmo.draggingAxis();
        BlockSelectionTranslateGizmo.setHoveredAxis(
            dragging != null ? dragging : BlockSelectionTranslateGizmo.pickUnderCursor(session)
        );
        BlockSelectionScaleGizmo.setHoveredFace(null);
        MoveBlocksGizmo.setHoveredAxis(null);
    }

    private static void tickMoveBlocks(com.blib.engine.session.EngineSession session) {
        var dragging = MoveBlocksGizmo.draggingAxis();
        MoveBlocksGizmo.setHoveredAxis(dragging != null ? dragging : MoveBlocksGizmo.pickUnderCursor(session));
        BlockSelectionScaleGizmo.setHoveredFace(null);
        BlockSelectionTranslateGizmo.setHoveredAxis(null);
    }

    private static void tickEntityTranslate(
        com.blib.engine.session.EngineSession session,
        net.minecraft.world.entity.LivingEntity entity
    ) {
        var dragging = EntityTranslateGizmo.draggingAxis();
        if (dragging != null) {
            EntityTranslateGizmo.setHoveredAxis(dragging);
        } else {
            var pick = EntityTranslateGizmo.pickUnderCursorWithDistance(session, entity);
            EntityTranslateGizmo.setHoveredAxis(pick == null ? null : pick.axis());
        }
        EntityScaleGizmo.setHovered(false);
    }

    private static void tickEntityScale(
        com.blib.engine.session.EngineSession session,
        net.minecraft.world.entity.LivingEntity entity
    ) {
        if (EntityScaleGizmo.isDragging()) {
            EntityScaleGizmo.setHovered(true);
        } else {
            EntityScaleGizmo.setHovered(EntityScaleGizmo.pickUnderCursorWithDistance(session, entity) != null);
        }
        EntityTranslateGizmo.setHoveredAxis(null);
    }
}
