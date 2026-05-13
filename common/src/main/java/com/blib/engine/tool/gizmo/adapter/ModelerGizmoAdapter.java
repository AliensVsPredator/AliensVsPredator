package com.blib.engine.tool.gizmo.adapter;

import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import com.blib.engine.modeler.gizmo.ModelerGizmoState;
import com.blib.engine.session.EngineSession;
import com.blib.engine.tool.gizmo.Gizmo;
import com.blib.engine.tool.gizmo.GizmoHit;

/**
 * Stub adapter exposing the modeler gizmo through the shared {@link Gizmo} interface. The adapter is intentionally
 * <em>not yet wired</em> to {@code ModelerGizmoInput} — that subsystem picks against 2D panel-relative pixels rather
 * than a 3D cursor ray, so the existing {@link Gizmo#hitTest(EngineSession)} contract (which uses the engine's world-
 * cursor session) doesn't fit. The migration path proposed in the architecture review is:
 * <ol>
 * <li>Introduce a {@code PickContext} sealed type — world-ray vs. panel-relative — so a single {@link Gizmo} can accept
 * either flavour of cursor without forcing one paradigm.</li>
 * <li>Re-implement modeler picking using {@code GizmoMath}'s panel-projection helpers (which already exist) and expose
 * a {@code hitTest(PickContext.PanelRelative)} entry point.</li>
 * <li>Wire this adapter into {@link com.blib.engine.tool.gizmo.GizmoRegistry} so the unified hover / dispatch pass
 * covers modeler handles alongside world gizmos.</li>
 * <li>Adapt the modeler's {@code ModelerGizmoInput} drag math to consume the unified {@code Gizmo.beginDrag} hit rather
 * than its current bespoke axis+sign signature. The cube field deltas would route through
 * {@link com.blib.engine.tool.gizmo.GizmoTarget} implementations (one per cube field — origin, size, rotation, pivot)
 * reusing the same generic {@code TranslateGizmo}/{@code ScaleGizmo}/{@code RotateGizmo} the world gizmos use.</li>
 * </ol>
 * Until that landing, this class is a marker so the unified-gizmo refactor has an obvious landing spot, and so the
 * registry can be polled for "is the modeler gizmo dragging?" via {@link #isDragging} without cross-package leaks.
 */
@ApiStatus.Internal
public final class ModelerGizmoAdapter implements Gizmo<ModelerGizmoAdapter.PanelHit> {

    public static final ModelerGizmoAdapter INSTANCE = new ModelerGizmoAdapter();

    private ModelerGizmoAdapter() {}

    @Override
    public String id() {
        return "modeler";
    }

    @Override
    public boolean isDragging() {
        return ModelerGizmoState.isDragging();
    }

    @Override
    public @Nullable PanelHit hitTest(EngineSession session) {
        // World cursor ray does not apply to the modeler — picking lives in panel-relative pixels driven by
        // ModelerGizmoInput. Returning null here is correct; once the PickContext seal exists, this method will be
        // replaced by hitTest(PickContext.PanelRelative).
        return null;
    }

    @Override
    public void beginDrag(PanelHit hit, EngineSession session) {
        // Stub — see class javadoc; the wiring lands once ModelerGizmoInput is refactored against the unified Gizmo.
    }

    @Override
    public void updateDrag(EngineSession session, Vec3 cursorRayDir) {
        // Stub — see class javadoc.
    }

    @Override
    public void endDrag() {
        ModelerGizmoState.setDrag(null);
    }

    @Override
    public void clear() {
        ModelerGizmoState.setDrag(null);
        ModelerGizmoState.setHover(null);
    }

    @Override
    public void setHovered(@Nullable PanelHit hit) {
        // Modeler hover is driven by ModelerGizmoInput.updateHover; nothing to mirror until that's unified.
    }

    /**
     * Hit handle for a future panel-relative pick. Carries axis + sign matching {@code ModelerGizmoState.HoverState}.
     */
    public record PanelHit(
        int axis,
        int sign,
        double t
    ) implements GizmoHit {}
}
