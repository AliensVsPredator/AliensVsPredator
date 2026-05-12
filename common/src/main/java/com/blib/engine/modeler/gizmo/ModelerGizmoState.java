package com.blib.engine.modeler.gizmo;

import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import com.blib.engine.gizmo.GizmoGeometry;
import com.blib.engine.modeler.ModelerBone;
import com.blib.engine.modeler.ModelerCube;

/**
 * Process-wide state for the modeler's translate / rotate / resize gizmo. Mirrors {@code BLibGizmoState}'s shape but
 * carries cube-edit context (selected cube + its owning bone) instead of item-tuner context.
 * <p>
 * Writers (renderer thread) set {@link #setLastRender}; readers (input thread, both client thread in MC) call
 * {@link #lastRender}. Single-selection only — unlike the item tuner there's no multi-target picking, so we store one
 * snapshot rather than a recent-renders list.
 */
@ApiStatus.Internal
public final class ModelerGizmoState {

    private static volatile ModelerGizmoMode mode = ModelerGizmoMode.OFF;

    private static volatile @Nullable RenderSnapshot lastRender = null;

    private static volatile @Nullable DragState drag = null;

    private ModelerGizmoState() {}

    public static ModelerGizmoMode mode() {
        return mode;
    }

    public static void setMode(ModelerGizmoMode newMode) {
        mode = newMode;
        // Cancel any in-flight drag when the user toggles modes — the drag state is mode-specific (axis
        // direction / face handle interpretation differs), and continuing it across a mode change would apply
        // the wrong delta.
        if (newMode == ModelerGizmoMode.OFF) {
            drag = null;
        }
    }

    public static @Nullable RenderSnapshot lastRender() {
        return lastRender;
    }

    public static void setLastRender(@Nullable RenderSnapshot snapshot) {
        lastRender = snapshot;
    }

    public static @Nullable DragState drag() {
        return drag;
    }

    public static void setDrag(@Nullable DragState newDrag) {
        drag = newDrag;
    }

    public static boolean isDragging() {
        return drag != null;
    }

    /**
     * Per-frame snapshot of where the gizmo was drawn — composes a {@link GizmoGeometry} with the cube context. The
     * cube is identified by reference; if it's deleted between render and click, the input handler bails before
     * mutating.
     *
     * @param geometry View-space pivot, axes, scale, projection (captured at the cube's pivot in cube-local frame).
     * @param owner    Bone that owns the selected cube — needed to rebuild the bone transform chain when applying drag
     *                 deltas in cube-local space.
     * @param cube     The selected cube — drag math reads/writes its origin/rotation/size.
     */
    public record RenderSnapshot(
        GizmoGeometry geometry,
        ModelerBone owner,
        ModelerCube cube
    ) {}

    /**
     * Captured at drag-start. {@code startCube} is a baseline of the cube's mutable fields so per-frame drag math
     * applies an absolute delta from drag-start rather than accumulating per-frame drift.
     * <p>
     * {@code previousCursorAngleRad} and {@code accumulatedRotationDegrees} are written each frame for ROTATE drags to
     * track the cumulative cursor sweep around the gizmo origin — necessary because a stateless atan2 delta from
     * drag-start would wrap at ±π and prevent rotations beyond 180°. For TRANSLATE / RESIZE these fields stay at their
     * drag-start values (0).
     *
     * @param mode                       Mode at drag-start (TRANSLATE / ROTATE / RESIZE — never OFF).
     * @param axis                       Which axis (0=X, 1=Y, 2=Z) the drag is along.
     * @param sign                       For RESIZE: +1 for MAX-face handles, -1 for MIN-face handles. Always +1 for
     *                                   TRANSLATE / ROTATE (which use positive axis only).
     * @param startCube                  Cube field baseline at drag-start (origin/size/rotation copied as Vec3s).
     * @param startCursorX               Cursor X (panel-relative pixels) at drag-start.
     * @param startCursorY               Cursor Y (panel-relative pixels) at drag-start.
     * @param startSnapshot              Render snapshot at drag-start — the drag math projects against this fixed
     *                                   geometry rather than the live one so a per-frame change to where the gizmo
     *                                   would render doesn't perturb the in-flight drag.
     * @param previousCursorAngleRad     Angle from the gizmo origin to the cursor on the previous update (radians,
     *                                   {@code atan2(y, x)}). Seeded from the click position at drag-start; each frame
     *                                   computes a wrapped delta against it for the accumulator.
     * @param accumulatedRotationDegrees Total signed cursor sweep around the gizmo since drag-start (degrees). The live
     *                                   cube rotation is {@code startCube.rotation + accumulated} along the dragged
     *                                   axis, so sweeps beyond 360° rotate the cube past a full revolution rather than
     *                                   wrapping back to start.
     */
    public record DragState(
        ModelerGizmoMode mode,
        int axis,
        int sign,
        CubeBaseline startCube,
        double startCursorX,
        double startCursorY,
        RenderSnapshot startSnapshot,
        double previousCursorAngleRad,
        float accumulatedRotationDegrees
    ) {}

    /** Snapshot of a cube's mutable fields at drag-start. */
    public record CubeBaseline(
        Vec3 origin,
        Vec3 size,
        Vec3 rotation
    ) {

        public static CubeBaseline of(ModelerCube cube) {
            return new CubeBaseline(cube.origin, cube.size, cube.rotation);
        }
    }
}
