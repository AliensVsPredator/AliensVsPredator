package com.blib.engine.modeler.gizmo;

import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix3f;

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

    private static volatile ModelerGizmoFrame frame = ModelerGizmoFrame.LOCAL;

    private static volatile @Nullable RenderSnapshot lastRender = null;

    private static volatile @Nullable DragState drag = null;

    private static volatile @Nullable HoverState hover = null;

    private ModelerGizmoState() {}

    public static ModelerGizmoMode mode() {
        return mode;
    }

    public static void setMode(ModelerGizmoMode newMode) {
        mode = newMode;
        // Cancel any in-flight drag when the user toggles modes — the drag state is mode-specific (axis
        // direction / face handle interpretation differs), and continuing it across a mode change would apply
        // the wrong delta. Hover is cleared too so a stale highlight from the previous mode doesn't ghost.
        if (newMode == ModelerGizmoMode.OFF) {
            drag = null;
            hover = null;
        }
    }

    public static ModelerGizmoFrame frame() {
        return frame;
    }

    /**
     * Switch the gizmo's reference frame (LOCAL vs GLOBAL). Cancels any in-flight drag for the same reason
     * {@link #setMode} does — the drag math branches on frame, so changing mid-drag would reinterpret the same cursor
     * motion against a different axis basis.
     */
    public static void setFrame(ModelerGizmoFrame newFrame) {
        frame = newFrame;
        drag = null;
        hover = null;
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

    public static @Nullable HoverState hover() {
        return hover;
    }

    public static void setHover(@Nullable HoverState newHover) {
        hover = newHover;
    }

    /**
     * Per-frame snapshot of where the gizmo was drawn — composes a {@link GizmoGeometry} with target context (either a
     * cube + its owning bone, or a bone). The target is identified by reference; if it's deleted between render and
     * click, the input handler bails before mutating.
     * <p>
     * <b>Invariant</b>: exactly one of {@code {cube, bone}} is non-null. {@link #isCube()} and {@link #isBone()}
     * reflect which target the snapshot describes; {@code owner} is meaningful only for cube targets.
     *
     * @param geometry          View-space pivot, axes, scale, projection. The axes match whatever frame the gizmo was
     *                          rendered in (target-local in LOCAL mode, world-axis-aligned in GLOBAL mode).
     * @param owner             Bone that owns the selected cube — needed to rebuild the bone transform chain when
     *                          applying drag deltas in cube-local space. Null when the target is a bone.
     * @param cube              The selected cube — drag math reads/writes its origin/rotation/size. Null for bone
     *                          targets.
     * @param bone              The selected bone — drag math reads/writes its position/rotation/pivot. Null for cube
     *                          targets.
     * @param boneChainRotation 3x3 rotation matrix accumulated by walking the parent chain. For cube targets: from
     *                          {@code scene.root} to {@code owner} (i.e. through every parent bone) BEFORE applying the
     *                          cube's own rotation. For bone targets: from {@code scene.root} to the bone's parent
     *                          (i.e. NOT through the bone's own rotation, since the bone's own rotation operates on its
     *                          children, not on its own {@code position}). Used by GLOBAL-frame drag math to
     *                          inverse-transform a world-space delta back to target-local pre-rotation coords.
     * @param frame             Reference frame at render time. Drag math reads this to pick the LOCAL vs GLOBAL path so
     *                          a drag-in-flight stays consistent even if the user toggles frame mid-drag (the toggle
     *                          itself cancels the drag, but reading from the snapshot is the safer source).
     */
    public record RenderSnapshot(
        GizmoGeometry geometry,
        @Nullable ModelerBone owner,
        @Nullable ModelerCube cube,
        @Nullable ModelerBone bone,
        Matrix3f boneChainRotation,
        ModelerGizmoFrame frame
    ) {

        public boolean isCube() {
            return cube != null;
        }

        public boolean isBone() {
            return bone != null;
        }
    }

    /**
     * Captured at drag-start. {@code startCube} / {@code startBone} is a baseline of the target's mutable fields so
     * per-frame drag math applies an absolute delta from drag-start rather than accumulating per-frame drift.
     * <p>
     * <b>Invariant</b>: exactly one of {@code {startCube, startBone}} is non-null, matching {@code startSnapshot}'s
     * cube/bone target.
     * <p>
     * {@code previousCursorAngleRad} and {@code accumulatedRotationDegrees} are written each frame for ROTATE drags to
     * track the cumulative cursor sweep around the gizmo origin — necessary because a stateless atan2 delta from
     * drag-start would wrap at ±π and prevent rotations beyond 180°. For TRANSLATE / RESIZE these fields stay at their
     * drag-start values (0).
     *
     * @param mode                       Mode at drag-start (TRANSLATE / ROTATE / RESIZE / PIVOT — never OFF).
     * @param axis                       Which axis (0=X, 1=Y, 2=Z) the drag is along.
     * @param sign                       For RESIZE: +1 for MAX-face handles, -1 for MIN-face handles. Always +1 for
     *                                   TRANSLATE / ROTATE / PIVOT (which use positive axis only).
     * @param startCube                  Cube field baseline at drag-start. Null when the drag targets a bone.
     * @param startBone                  Bone field baseline at drag-start. Null when the drag targets a cube.
     * @param startCursorX               Cursor X (panel-relative pixels) at drag-start.
     * @param startCursorY               Cursor Y (panel-relative pixels) at drag-start.
     * @param startSnapshot              Render snapshot at drag-start — the drag math projects against this fixed
     *                                   geometry rather than the live one so a per-frame change to where the gizmo
     *                                   would render doesn't perturb the in-flight drag.
     * @param previousCursorAngleRad     Angle from the gizmo origin to the cursor on the previous update (radians,
     *                                   {@code atan2(y, x)}). Seeded from the click position at drag-start; each frame
     *                                   computes a wrapped delta against it for the accumulator.
     * @param accumulatedRotationDegrees Total signed cursor sweep around the gizmo since drag-start (degrees). The live
     *                                   target rotation is {@code startRotation + accumulated} along the dragged axis,
     *                                   so sweeps beyond 360° rotate past a full revolution rather than wrapping back
     *                                   to start.
     */
    public record DragState(
        ModelerGizmoMode mode,
        int axis,
        int sign,
        @Nullable CubeBaseline startCube,
        @Nullable BoneBaseline startBone,
        double startCursorX,
        double startCursorY,
        RenderSnapshot startSnapshot,
        double previousCursorAngleRad,
        float accumulatedRotationDegrees
    ) {

        public boolean isBoneDrag() {
            return startBone != null;
        }
    }

    /**
     * Hovered gizmo handle. Refreshed each frame by {@code ModelerGizmoInput.updateHover} from the viewport panel's
     * render hook, then read by the renderer to brighten the matching handle's alpha. {@code axis} is 0/1/2; {@code
     * sign} is +1 / -1 (only meaningful for RESIZE — TRANSLATE / ROTATE always set +1, matching DragState's
     * convention).
     */
    public record HoverState(
        int axis,
        int sign
    ) {}

    /**
     * Snapshot of a cube's mutable fields at drag-start. Pivot's included so PIVOT-mode drags read a stable baseline;
     * inflate's included so the ghost outline renderer can draw the cube at its true drag-start size.
     */
    public record CubeBaseline(
        Vec3 origin,
        Vec3 size,
        Vec3 rotation,
        Vec3 pivot,
        double inflate
    ) {

        public static CubeBaseline of(ModelerCube cube) {
            return new CubeBaseline(cube.origin, cube.size, cube.rotation, cube.pivot, cube.inflate);
        }
    }

    /**
     * Snapshot of a bone's mutable fields at drag-start. Parallel to {@link CubeBaseline}; lets bone-target drag math
     * compute deltas against a stable baseline and lets the ghost-outline renderer place the bone at its drag-start
     * world transform.
     */
    public record BoneBaseline(
        Vec3 position,
        Vec3 rotation,
        Vec3 pivot,
        Vec3 scale
    ) {

        public static BoneBaseline of(ModelerBone bone) {
            return new BoneBaseline(bone.position, bone.rotation, bone.pivot, bone.scale);
        }
    }
}
