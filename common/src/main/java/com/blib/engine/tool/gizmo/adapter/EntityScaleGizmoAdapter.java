package com.blib.engine.tool.gizmo.adapter;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import com.blib.engine.domain.selection.entity.EntityScaleGizmo;
import com.blib.engine.domain.selection.picking.EntitySelectable;
import com.blib.engine.domain.selection.picking.SelectionManager;
import com.blib.engine.session.EngineSession;
import com.blib.engine.tool.gizmo.Gizmo;

/**
 * {@link Gizmo} facade over the existing {@link EntityScaleGizmo} static API. Resolves the target entity from the
 * {@link SelectionManager} on every call, so the facade itself is stateless — only the underlying static class holds
 * the hover/drag fields.
 * <p>
 * This adapter is the demonstration referenced in the architecture proposal: the dispatch path can now ask the
 * {@link com.blib.engine.tool.gizmo.GizmoRegistry registry} for the closest hit across <em>all</em> registered gizmos
 * instead of {@code instanceof}-checking each selection type. The other four domain gizmos can be wrapped with the same
 * pattern (see {@link BlockSelectionScaleGizmoAdapter}); their target lookup is the only thing that differs.
 */
@ApiStatus.Internal
public final class EntityScaleGizmoAdapter implements Gizmo<EntityScaleGizmo.HandleHit> {

    public static final EntityScaleGizmoAdapter INSTANCE = new EntityScaleGizmoAdapter();

    private EntityScaleGizmoAdapter() {}

    private @Nullable LivingEntity target() {
        var sel = SelectionManager.current().single();
        if (sel instanceof EntitySelectable es) {
            return es.entity();
        }
        return null;
    }

    @Override
    public String id() {
        return "entity_scale";
    }

    @Override
    public boolean isDragging() {
        return EntityScaleGizmo.isDragging();
    }

    @Override
    public @Nullable EntityScaleGizmo.HandleHit hitTest(EngineSession session) {
        var entity = target();
        return entity == null ? null : EntityScaleGizmo.pickUnderCursorWithDistance(session, entity);
    }

    @Override
    public void beginDrag(EntityScaleGizmo.HandleHit hit, EngineSession session) {
        var entity = target();
        if (entity != null) {
            EntityScaleGizmo.beginDrag(entity, session);
        }
    }

    @Override
    public void updateDrag(EngineSession session, Vec3 cursorRayDir) {
        EntityScaleGizmo.updateDrag(session, cursorRayDir);
    }

    @Override
    public void endDrag() {
        EntityScaleGizmo.endDrag();
    }

    @Override
    public void clear() {
        EntityScaleGizmo.clear();
    }

    @Override
    public void setHovered(@Nullable EntityScaleGizmo.HandleHit hit) {
        EntityScaleGizmo.setHovered(hit != null);
    }
}
