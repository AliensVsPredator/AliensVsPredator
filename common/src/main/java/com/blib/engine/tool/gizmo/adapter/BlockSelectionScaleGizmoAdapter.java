package com.blib.engine.tool.gizmo.adapter;

import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import com.blib.engine.domain.selection.volume.BlockSelection;
import com.blib.engine.domain.selection.volume.BlockSelectionScaleGizmo;
import com.blib.engine.session.EngineSession;
import com.blib.engine.tool.gizmo.Gizmo;

/**
 * {@link Gizmo} facade over the existing {@link BlockSelectionScaleGizmo} static API. The target is the
 * {@link BlockSelection} singleton, so no per-call lookup is needed; the facade only forwards.
 */
@ApiStatus.Internal
public final class BlockSelectionScaleGizmoAdapter implements Gizmo<BlockSelectionScaleGizmo.FaceHit> {

    public static final BlockSelectionScaleGizmoAdapter INSTANCE = new BlockSelectionScaleGizmoAdapter();

    private BlockSelectionScaleGizmoAdapter() {}

    @Override
    public String id() {
        return "block_volume_scale";
    }

    @Override
    public boolean isDragging() {
        return BlockSelectionScaleGizmo.isDragging();
    }

    @Override
    public @Nullable BlockSelectionScaleGizmo.FaceHit hitTest(EngineSession session) {
        if (BlockSelection.aabb().isEmpty()) {
            return null;
        }
        return BlockSelectionScaleGizmo.pickUnderCursorWithDistance(session);
    }

    @Override
    public void beginDrag(BlockSelectionScaleGizmo.FaceHit hit, EngineSession session) {
        BlockSelectionScaleGizmo.beginDrag(hit.face(), session);
    }

    @Override
    public void updateDrag(EngineSession session, Vec3 cursorRayDir) {
        BlockSelectionScaleGizmo.updateDrag(session, cursorRayDir);
    }

    @Override
    public void endDrag() {
        BlockSelectionScaleGizmo.endDrag();
    }

    @Override
    public void clear() {
        BlockSelectionScaleGizmo.clear();
    }

    @Override
    public void setHovered(@Nullable BlockSelectionScaleGizmo.FaceHit hit) {
        BlockSelectionScaleGizmo.setHoveredFace(hit == null ? null : hit.face());
    }
}
