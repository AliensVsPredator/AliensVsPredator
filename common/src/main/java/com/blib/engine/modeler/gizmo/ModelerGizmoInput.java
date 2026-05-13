package com.blib.engine.modeler.gizmo;

import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.ApiStatus;
import org.joml.Matrix3f;
import org.joml.Vector3f;

import com.blib.engine.gizmo.GizmoMath;
import com.blib.engine.modeler.ModelerCube;
import com.blib.engine.render.modeler.ModelerGizmoRenderer;

/**
 * Picking + drag logic for the modeler gizmo. Stateless; reads/writes {@link ModelerGizmoState} and mutates the
 * selected cube's fields in place when a drag delta is applied. All math is delegated to {@link GizmoMath} so the
 * algorithms stay aligned with {@code BLibGizmoInput}.
 * <p>
 * Coordinates passed in are PANEL-relative (cursor and width/height) so the perspective-divide math in
 * {@link GizmoMath#projectToScreen} produces panel-pixel positions that match {@code mouseX / mouseY} as received by
 * {@code ModelerViewportPanel}.
 */
@ApiStatus.Internal
public final class ModelerGizmoInput {

    private static final float TRANSLATE_PICK_THRESHOLD_PX = 16f;

    private static final float ROTATE_PICK_THRESHOLD_PX = 24f;

    private static final float RESIZE_PICK_THRESHOLD_PX = 16f;

    private static final int RING_PICK_SEGMENTS = 96;

    private ModelerGizmoInput() {}

    /**
     * Hit-test the gizmo handles at the given cursor and start a drag if one is hit. Returns true if a handle was
     * grabbed (the caller should claim the click); false otherwise (cursor falls through to plain cube selection).
     */
    public static boolean tryStartDrag(double panelCursorX, double panelCursorY, int panelW, int panelH) {
        var mode = ModelerGizmoState.mode();
        if (mode == ModelerGizmoMode.OFF) {
            return false;
        }

        var snapshot = ModelerGizmoState.lastRender();
        if (snapshot == null) {
            return false;
        }

        var pick = pickHandle(snapshot, mode, panelCursorX, panelCursorY, panelW, panelH);
        if (pick == null) {
            return false;
        }

        // ROTATE drags accumulate angle delta frame-over-frame; seed the previous-angle field with the cursor's
        // initial angle around the projected gizmo origin so the first update produces a (near-)zero step. For other
        // modes this field stays 0 and is unread.
        double initialAngleRad = 0;
        if (mode == ModelerGizmoMode.ROTATE) {
            var origin = GizmoMath.projectToScreen(snapshot.geometry().viewPivot(), snapshot.geometry().projection(), panelW, panelH);
            if (origin != null) {
                initialAngleRad = Math.atan2(panelCursorY - origin.y, panelCursorX - origin.x);
            }
        }

        // Seed the drag with whichever baseline matches the snapshot's target. Exactly one of {cube, bone} is non-null
        // per the RenderSnapshot invariant; we use that to pick the baseline kind.
        var cubeBaseline = snapshot.isCube() ? ModelerGizmoState.CubeBaseline.of(snapshot.cube()) : null;
        var boneBaseline = snapshot.isBone() ? ModelerGizmoState.BoneBaseline.of(snapshot.bone()) : null;

        ModelerGizmoState.setDrag(
            new ModelerGizmoState.DragState(
                mode,
                pick.axis,
                pick.sign,
                cubeBaseline,
                boneBaseline,
                panelCursorX,
                panelCursorY,
                snapshot,
                initialAngleRad,
                0f
            )
        );
        return true;
    }

    /** Apply the cumulative drag delta from drag-start cursor to the current cursor. No-op when no drag is active. */
    public static void updateDrag(double panelCursorX, double panelCursorY, int panelW, int panelH) {
        var drag = ModelerGizmoState.drag();
        if (drag == null) {
            return;
        }

        boolean bone = drag.isBoneDrag();
        switch (drag.mode()) {
            case TRANSLATE -> {
                if (bone) {
                    applyBoneTranslate(drag, panelCursorX, panelCursorY, panelW, panelH);
                } else {
                    applyTranslate(drag, panelCursorX, panelCursorY, panelW, panelH);
                }
            }
            case ROTATE -> {
                if (bone) {
                    applyBoneRotate(drag, panelCursorX, panelCursorY, panelW, panelH);
                } else {
                    applyRotate(drag, panelCursorX, panelCursorY, panelW, panelH);
                }
            }
            case RESIZE -> {
                // RESIZE has no bone version — the renderer doesn't capture a bone snapshot in RESIZE mode, so a
                // bone-targeted drag in this branch shouldn't be reachable. Skip if it somehow is, to stay safe.
                if (!bone) {
                    applyResize(drag, panelCursorX, panelCursorY, panelW, panelH);
                }
            }
            case PIVOT -> {
                if (bone) {
                    applyBonePivotTranslate(drag, panelCursorX, panelCursorY, panelW, panelH);
                } else {
                    applyPivotTranslate(drag, panelCursorX, panelCursorY, panelW, panelH);
                }
            }
            default -> {
                /* OFF — no drag math to apply. */
            }
        }
    }

    public static void endDrag() {
        ModelerGizmoState.setDrag(null);
    }

    /**
     * Pick the handle under the cursor and write the result to {@link ModelerGizmoState#setHover}. Bails (clears hover)
     * when there's no gizmo to hit (mode OFF, no snapshot) or a drag is already in flight (the drag visual supersedes
     * hover feedback). Called once per frame from the viewport panel's render hook so the renderer can brighten the
     * hovered handle's alpha.
     */
    public static void updateHover(double panelCursorX, double panelCursorY, int panelW, int panelH) {
        var mode = ModelerGizmoState.mode();
        if (mode == ModelerGizmoMode.OFF || ModelerGizmoState.isDragging()) {
            ModelerGizmoState.setHover(null);
            return;
        }
        var snapshot = ModelerGizmoState.lastRender();
        if (snapshot == null) {
            ModelerGizmoState.setHover(null);
            return;
        }
        var pick = pickHandle(snapshot, mode, panelCursorX, panelCursorY, panelW, panelH);
        ModelerGizmoState.setHover(pick == null ? null : new ModelerGizmoState.HoverState(pick.axis(), pick.sign()));
    }

    /** Result of a handle hit-test — the axis index (0/1/2) and the sign (+1 for positive-axis / MAX, -1 for MIN). */
    private record HandleHit(
        int axis,
        int sign
    ) {}

    private static HandleHit pickHandle(
        ModelerGizmoState.RenderSnapshot snapshot,
        ModelerGizmoMode mode,
        double cx,
        double cy,
        int w,
        int h
    ) {
        return switch (mode) {
            // PIVOT reuses the same axis-arrow handles as TRANSLATE; only the drag math differs.
            case TRANSLATE, PIVOT -> pickAxisHandle(snapshot, cx, cy, w, h, TRANSLATE_PICK_THRESHOLD_PX);
            case ROTATE -> pickRingHandle(snapshot, cx, cy, w, h);
            case RESIZE -> pickFaceHandle(snapshot, cx, cy, w, h);
            default -> null;
        };
    }

    private static HandleHit pickAxisHandle(
        ModelerGizmoState.RenderSnapshot snapshot,
        double cx,
        double cy,
        int w,
        int h,
        float threshold
    ) {
        int best = -1;
        float bestDist = threshold;
        for (int axis = 0; axis < 3; axis++) {
            float d = GizmoMath.distanceToAxisHandle(snapshot.geometry(), axis, 1, cx, cy, w, h);
            if (d < bestDist) {
                bestDist = d;
                best = axis;
            }
        }
        return best < 0 ? null : new HandleHit(best, 1);
    }

    private static HandleHit pickRingHandle(ModelerGizmoState.RenderSnapshot snapshot, double cx, double cy, int w, int h) {
        int best = -1;
        float bestDist = ROTATE_PICK_THRESHOLD_PX;
        for (int axis = 0; axis < 3; axis++) {
            float d = GizmoMath.distanceToRotateHandle(snapshot.geometry(), axis, cx, cy, w, h, RING_PICK_SEGMENTS);
            if (d < bestDist) {
                bestDist = d;
                best = axis;
            }
        }
        return best < 0 ? null : new HandleHit(best, 1);
    }

    private static HandleHit pickFaceHandle(ModelerGizmoState.RenderSnapshot snapshot, double cx, double cy, int w, int h) {
        int bestAxis = -1;
        int bestSign = 1;
        float bestDist = RESIZE_PICK_THRESHOLD_PX;
        for (int axis = 0; axis < 3; axis++) {
            for (int sign : new int[] { -1, 1 }) {
                var face = ModelerGizmoRenderer.faceCenterLocal(snapshot.cube(), axis, sign);
                // Resize handles are scaled to 60% of the snapshot's gizmo scale (matches the renderer). Pass the
                // shorter length by scaling face-center offset and using the regular handle math — but since
                // GizmoMath doesn't take a length override, we compensate by reducing the threshold proportionally
                // ... actually, distanceToOffsetAxisHandle computes the tip at `base + scale * axis * sign`, where
                // scale is the snapshot's scale. The face handle's visible length is 0.6 × snapshot.scale, so we
                // accept this small mismatch — picking still works because we project the visible segment and the
                // cursor sits near it; the segment's screen length is correct for distance tests regardless of
                // the world-space length, as long as base + tip match the rendered handle.
                float d = distanceToFaceHandle(snapshot, (float) face[0], (float) face[1], (float) face[2], axis, sign, cx, cy, w, h);
                if (d < bestDist) {
                    bestDist = d;
                    bestAxis = axis;
                    bestSign = sign;
                }
            }
        }
        return bestAxis < 0 ? null : new HandleHit(bestAxis, bestSign);
    }

    /**
     * Face handle is rendered with a length of {@code 0.6 × snapshot.scale}; we project the actual visible segment so
     * picking matches what the user sees. {@code GizmoMath#distanceToOffsetAxisHandle} uses {@code snapshot.scale}
     * directly, so we project the segment manually here with the resize-scaled length.
     */
    private static float distanceToFaceHandle(
        ModelerGizmoState.RenderSnapshot snapshot,
        float fx,
        float fy,
        float fz,
        int axis,
        int sign,
        double cx,
        double cy,
        int w,
        int h
    ) {
        var geom = snapshot.geometry();
        var baseView = GizmoMath.localToView(geom, fx, fy, fz);
        var base = GizmoMath.projectToScreen(baseView, geom.projection(), w, h);
        if (base == null) {
            return Float.POSITIVE_INFINITY;
        }
        var axisView = geom.axis(axis);
        float resizeLen = geom.scale() * 0.6f * sign;
        var tipView = new Vector3f(baseView).fma(resizeLen, axisView);
        var tip = GizmoMath.projectToScreen(tipView, geom.projection(), w, h);
        if (tip == null) {
            return Float.POSITIVE_INFINITY;
        }
        return GizmoMath.distancePointToSegment((float) cx, (float) cy, base, tip);
    }

    private static void applyTranslate(ModelerGizmoState.DragState drag, double cx, double cy, int w, int h) {
        var s = drag.startSnapshot();
        float delta = GizmoMath.axisDelta(s.geometry(), drag.axis(), drag.sign(), drag.startCursorX(), drag.startCursorY(), cx, cy, w, h);
        if (delta == 0f) {
            return;
        }

        // LOCAL frame: delta is already along the cube's post-rotation X/Y/Z; adding it to origin.<axis> shifts the
        // cube body in cube-local pre-rotation, and the cube's own rotation matrix carries the body through into
        // the world-space motion the user expects. No inverse-transform needed.
        // GLOBAL frame: delta is along world X/Y/Z. The cube body's world position is bone_chain * R_cube * v, so to
        // produce a world-space shift along the world axis we have to inverse-transform: Δv_local = R_cube^T *
        // bone_chain^T * delta_world.
        var startOrigin = drag.startCube().origin();
        var deltaLocal = computeLocalDelta(drag, s, delta);
        var newOrigin = new Vec3(
            startOrigin.x + deltaLocal.x,
            startOrigin.y + deltaLocal.y,
            startOrigin.z + deltaLocal.z
        );
        s.cube().origin = newOrigin;
    }

    /**
     * Compute the cube-local delta vector for a translate-style drag, branching on the snapshot's frame. Shared helper
     * between {@link #applyTranslate} and {@link #applyPivotTranslate}'s body-compensation path.
     */
    private static Vector3f computeLocalDelta(ModelerGizmoState.DragState drag, ModelerGizmoState.RenderSnapshot s, float delta) {
        if (s.frame() == ModelerGizmoFrame.LOCAL) {
            // Axis-aligned in cube-local pre-rotation. The cube's rotation chain does the rest at render time.
            var v = new Vector3f(0, 0, 0);
            v.setComponent(drag.axis(), delta);
            return v;
        }
        // GLOBAL: delta is along world axis. Inverse cumulative rotation = R_cube^T * bone_chain^T.
        var startRot = drag.startCube().rotation();
        var rcube = new Matrix3f()
            .rotateZ((float) Math.toRadians(startRot.z))
            .rotateY((float) Math.toRadians(startRot.y))
            .rotateX((float) Math.toRadians(startRot.x));
        var inverseCumulative = new Matrix3f(s.boneChainRotation()).mul(rcube).transpose();
        var worldDelta = new Vector3f(0, 0, 0);
        worldDelta.setComponent(drag.axis(), delta);
        inverseCumulative.transform(worldDelta);
        return worldDelta;
    }

    /**
     * Pivot translation drag — moves the cube's rotation center along the gizmo's visible axis while keeping the
     * rendered cube body in place. Two subtleties on top of the {@link #applyTranslate} pattern:
     * <ol>
     * <li><b>Pivot shift in the visible direction.</b> The gizmo is rendered in the cube's post-rotation frame, so the
     * red/green/blue arrows point along {@code R * (1,0,0)} / {@code (0,1,0)} / {@code (0,0,1)} in world space (where
     * {@code R} is the cube's rotation matrix). The pivot is stored in cube-local pre-rotation coords; to move the
     * world-space pivot by {@code delta * visibleAxis}, the pre-rotation shift is {@code R * (delta along axis)} (the
     * pivot itself isn't transformed by R — it sits outside the rotation). The previous version added {@code delta}
     * directly to {@code pivot.x/y/z}, which only matched the visible direction when R was identity.</li>
     * <li><b>Origin compensation.</b> A naked pivot shift drags the rendered cube along because the body transform is
     * {@code T(pivot) * R * T(-pivot) * v} — changing pivot by {@code Δp} moves the rendered body by
     * {@code (I - R) * Δp}. To cancel that body motion, shift {@code cube.origin} by {@code Δo = (I - R^T) * Δp}; with
     * {@code Δp = R * δ_local}, that simplifies to {@code Δo = Δp - δ_local} (the difference between rotated and
     * pre-rotation axis units, scaled by the drag delta). When R is identity the difference vanishes and the cube
     * doesn't move, matching the simple case.</li>
     * </ol>
     */
    private static void applyPivotTranslate(ModelerGizmoState.DragState drag, double cx, double cy, int w, int h) {
        var s = drag.startSnapshot();
        float delta = GizmoMath.axisDelta(s.geometry(), drag.axis(), drag.sign(), drag.startCursorX(), drag.startCursorY(), cx, cy, w, h);
        if (delta == 0f) {
            return;
        }

        // Build the cube's start rotation matrix in Z-Y-X intrinsic Euler order (matches ModelerTransforms.applyCube
        // and applyRotate elsewhere in this file). Use the drag-start rotation, not live state — applyPivotTranslate
        // re-runs each frame with absolute deltas from drag-start, so the baseline must be drag-start to stay stable.
        var startRot = drag.startCube().rotation();
        var rcube = new Matrix3f()
            .rotateZ((float) Math.toRadians(startRot.z))
            .rotateY((float) Math.toRadians(startRot.y))
            .rotateX((float) Math.toRadians(startRot.x));

        // Δp_local — the change to cube.pivot — depends on frame.
        // LOCAL : user drags along visible (cube-post-rotation) axis. Pivot is OUTSIDE the cube rotation, so to
        // move the world pivot along the visible axis we shift by R_cube * (delta along local axis).
        // GLOBAL: user drags along world axis. Pivot's local-to-world transform is just bone_chain (no R_cube,
        // since the pivot is the rotation center, not inside the rotation). Δp_local = bone_chain^T * delta_world.
        var dpLocal = new Vector3f(0, 0, 0);
        if (s.frame() == ModelerGizmoFrame.LOCAL) {
            var deltaCubeLocal = new Vector3f(0, 0, 0);
            deltaCubeLocal.setComponent(drag.axis(), delta);
            dpLocal.set(deltaCubeLocal);
            rcube.transform(dpLocal);
        } else {
            // GLOBAL: inverse-transform world delta through bone_chain.
            var deltaWorld = new Vector3f(0, 0, 0);
            deltaWorld.setComponent(drag.axis(), delta);
            var boneInv = new Matrix3f(s.boneChainRotation()).transpose();
            boneInv.transform(deltaWorld);
            dpLocal.set(deltaWorld);
        }

        // Origin compensation so the cube body stays put under the pivot change. Derivation (see earlier comment in
        // the LOCAL-only version): Δo = (I - R_cube^T) * Δp_local. This is independent of bone_chain — the bone
        // chain transforms the entire (pivot + body) outcome uniformly, so it cancels in the "keep body in place"
        // equation.
        var rcubeT = new Matrix3f(rcube).transpose();
        var rcubeTimesDp = new Vector3f(dpLocal);
        rcubeT.transform(rcubeTimesDp);
        var dOrigin = new Vector3f(dpLocal).sub(rcubeTimesDp);

        var startPivot = drag.startCube().pivot();
        var startOrigin = drag.startCube().origin();

        s.cube().pivot = new Vec3(
            startPivot.x + dpLocal.x,
            startPivot.y + dpLocal.y,
            startPivot.z + dpLocal.z
        );
        s.cube().origin = new Vec3(
            startOrigin.x + dOrigin.x,
            startOrigin.y + dOrigin.y,
            startOrigin.z + dOrigin.z
        );
    }

    /**
     * Rotation drag — accumulate angle frame-over-frame using the cursor's atan2 angle around the projected gizmo
     * origin, then apply that rotation around the cube's VISIBLE local axis (not just the matching Euler component).
     * <p>
     * The naive approach of adding the accumulated angle to {@code cube.rotation.y} (etc.) only works when the cube has
     * no other rotation: with Z-Y-X intrinsic Euler order, {@code rotation.y} rotates around the post-Z Y axis, while
     * the green ring the user sees and grabs is the post-Z-Y-X Y axis. Once any X rotation has been authored, the two
     * diverge and the green ring rotates around what looks like a different axis — making the user think the blue/green
     * rings are swapped.
     * <p>
     * To make each ring rotate around its visible axis: build the cube's start rotation matrix, post-multiply by a
     * rotation around the cube-local axis_unit for the picked ring, and decompose the result back to Z-Y-X Euler.
     * Identity {@code R(θ, M*v) * M = M * R(θ, v)} means post-multiplying M_start by {@code R(θ, axis_unit)} is the
     * same as pre-multiplying by {@code R(θ, M_start * axis_unit)} — i.e. rotation around the visible axis.
     */
    private static void applyRotate(ModelerGizmoState.DragState drag, double cx, double cy, int w, int h) {
        var s = drag.startSnapshot();
        var origin = GizmoMath.projectToScreen(s.geometry().viewPivot(), s.geometry().projection(), w, h);
        if (origin == null) {
            return;
        }

        double currentAngle = Math.atan2(cy - origin.y, cx - origin.x);
        double frameDeltaRad = currentAngle - drag.previousCursorAngleRad();

        // Unwrap to [-π, π] so a sweep across the ±π discontinuity (e.g., cursor crossing from just-above the +X
        // axis to just-below it) doesn't register as a -360° spike.
        while (frameDeltaRad > Math.PI) {
            frameDeltaRad -= 2 * Math.PI;
        }
        while (frameDeltaRad < -Math.PI) {
            frameDeltaRad += 2 * Math.PI;
        }

        // Right-hand rule sign — axis pointing toward the viewer (axisView.z > 0) inverts so a screen-CW sweep
        // produces +ve rotation around the +axis (matches the right-hand rule for an axis pointing into the screen).
        var axisView = s.geometry().axis(drag.axis());
        float signFactor = axisView.z > 0 ? -1f : 1f;
        float frameDeltaDegrees = (float) Math.toDegrees(frameDeltaRad) * signFactor;
        float newAccumulated = drag.accumulatedRotationDegrees() + frameDeltaDegrees;

        // Visible-axis rotation: build the cube's rotation matrix at drag-start, post-multiply by R(accumulated)
        // around the cube-local axis_unit for the picked ring, then decompose the result back to Z-Y-X Euler so the
        // data stays in the Bedrock-friendly representation.
        var startRot = drag.startCube().rotation();
        var mStart = new Matrix3f()
            .rotateZ((float) Math.toRadians(startRot.z))
            .rotateY((float) Math.toRadians(startRot.y))
            .rotateX((float) Math.toRadians(startRot.x));

        float thetaRad = (float) Math.toRadians(newAccumulated);
        var mNew = new Matrix3f(mStart);
        switch (drag.axis()) {
            case 0 -> mNew.rotateX(thetaRad);
            case 1 -> mNew.rotateY(thetaRad);
            default -> mNew.rotateZ(thetaRad);
        }

        s.cube().rotation = decomposeZYX(mNew);

        // Store back: previous-angle for the next frame's delta, accumulator so the cube rotation reads from a
        // single source of truth (mStart * R(accumulated, axis_unit), applied every frame).
        ModelerGizmoState.setDrag(
            new ModelerGizmoState.DragState(
                drag.mode(),
                drag.axis(),
                drag.sign(),
                drag.startCube(),
                drag.startBone(),
                drag.startCursorX(),
                drag.startCursorY(),
                drag.startSnapshot(),
                currentAngle,
                newAccumulated
            )
        );
    }

    /**
     * Decompose a 3x3 rotation matrix into Z-Y-X intrinsic Euler angles {@code (x, y, z)} in degrees, such that
     * {@code Rz(z) * Ry(y) * Rx(x)} reproduces the matrix. Singular case (y = ±π/2, gimbal lock) collapses z to 0 and
     * recovers x from the remaining matrix entries — standard convention used by Blender / Bedrock decompositions.
     * <p>
     * JOML uses column-major naming ({@code mNM} = column N, row M), so e.g. {@code m02} is the standard-math element
     * at (row=2, col=0), which for our {@code Rz * Ry * Rx} matrix equals {@code -sin(y)}.
     */
    private static Vec3 decomposeZYX(Matrix3f m) {
        // Clamp against floating-point overshoot so asin doesn't return NaN at the boundary.
        float sinY = -Math.clamp(m.m02, -1f, 1f);
        float cosY = (float) Math.sqrt(1f - sinY * sinY);

        double xRad;
        double yRad = Math.asin(sinY);
        double zRad;
        if (cosY > 1.0e-6f) {
            xRad = Math.atan2(m.m12, m.m22);
            zRad = Math.atan2(m.m01, m.m00);
        } else {
            // Gimbal lock at y = ±90°: cos(y) ≈ 0 kills the standard formulas. Pin z = 0 and recover x from m21,
            // m11 which under z = 0 and |sin(y)| = 1 reduce to ∓sin(x), cos(x).
            xRad = Math.atan2(-Math.signum(sinY) * m.m21, m.m11);
            zRad = 0;
        }

        return new Vec3(Math.toDegrees(xRad), Math.toDegrees(yRad), Math.toDegrees(zRad));
    }

    /**
     * Resize delta application. The handle's visible length is 0.6 × snapshot.scale; we call {@code axisDelta} with the
     * snapshot's full scale and scale the returned delta down by 0.6 so a full drag of the visible arrow length grows /
     * shrinks the cube by exactly the arrow's world-length.
     */
    private static void applyResize(ModelerGizmoState.DragState drag, double cx, double cy, int w, int h) {
        var s = drag.startSnapshot();
        var face = ModelerGizmoRenderer.faceCenterLocal(s.cube(), drag.axis(), drag.sign());
        float rawDelta = GizmoMath.offsetAxisDelta(
            s.geometry(),
            (float) face[0],
            (float) face[1],
            (float) face[2],
            drag.axis(),
            drag.sign(),
            drag.startCursorX(),
            drag.startCursorY(),
            cx,
            cy,
            w,
            h
        );
        if (rawDelta == 0f) {
            return;
        }
        // Visible handle is 60% of the snapshot's scale; axisDelta is parameterized on the full scale so a 60%-as-
        // long handle yields a delta that's 1/0.6 × too big. Scale down to match what the user sees, then snap to
        // an integer — cube size is a whole-number authoring concept (Bedrock pixel units), and fractional values
        // would show up in the inspector / JSON export. Snap-to-int also gives the drag a tactile "click" feel.
        int delta = Math.round(rawDelta * 0.6f);
        if (delta == 0) {
            // Sub-unit motion hasn't crossed an integer threshold yet — leave the cube alone until the user has
            // moved far enough to register one whole unit.
            return;
        }

        var startOrigin = drag.startCube().origin();
        var startSize = drag.startCube().size();

        // Round the baseline to int as well so a fractional pre-existing size doesn't leak into the result. Origin
        // can stay where it is; only the affected-axis origin needs to track the delta for MIN faces.
        long startSizeX = Math.round(startSize.x);
        long startSizeY = Math.round(startSize.y);
        long startSizeZ = Math.round(startSize.z);

        double newOriginX = startOrigin.x, newOriginY = startOrigin.y, newOriginZ = startOrigin.z;
        long newSizeX = startSizeX, newSizeY = startSizeY, newSizeZ = startSizeZ;

        if (drag.sign() > 0) {
            // MAX face — grow size only; origin unchanged. Clamp at zero so dragging inward past zero just pins
            // the size at zero rather than producing a negative-volume cube.
            switch (drag.axis()) {
                case 0 -> newSizeX = Math.max(0, startSizeX + delta);
                case 1 -> newSizeY = Math.max(0, startSizeY + delta);
                default -> newSizeZ = Math.max(0, startSizeZ + delta);
            }
        } else {
            // MIN face — moving outward (delta > 0) grows the cube on the -axis side; origin shifts down and size
            // grows. Clamp at zero size by pinning origin to the opposite face's position when the user has dragged
            // past it. Origin shift uses the same integer delta so the cube stays grid-aligned across the drag.
            switch (drag.axis()) {
                case 0 -> {
                    long raw = startSizeX + delta;
                    if (raw <= 0) {
                        newOriginX = startOrigin.x + startSizeX;
                        newSizeX = 0;
                    } else {
                        newOriginX = startOrigin.x - delta;
                        newSizeX = raw;
                    }
                }
                case 1 -> {
                    long raw = startSizeY + delta;
                    if (raw <= 0) {
                        newOriginY = startOrigin.y + startSizeY;
                        newSizeY = 0;
                    } else {
                        newOriginY = startOrigin.y - delta;
                        newSizeY = raw;
                    }
                }
                default -> {
                    long raw = startSizeZ + delta;
                    if (raw <= 0) {
                        newOriginZ = startOrigin.z + startSizeZ;
                        newSizeZ = 0;
                    } else {
                        newOriginZ = startOrigin.z - delta;
                        newSizeZ = raw;
                    }
                }
            }
        }

        ModelerCube cube = s.cube();
        cube.origin = new Vec3(newOriginX, newOriginY, newOriginZ);
        cube.size = new Vec3(newSizeX, newSizeY, newSizeZ);
    }

    /**
     * Bone TRANSLATE — mutates {@code bone.position}. Unlike a cube's origin (which sits INSIDE the cube's rotation),
     * the bone's position is the FIRST transform in {@code applyBone} — applied in the parent's frame, before the
     * bone's own rotation. That changes the LOCAL-frame math vs. the cube version:
     * <ul>
     * <li><b>LOCAL</b>: visible X axis is {@code parentChain · R_bone · (1,0,0)}. To shift the bone's world position
     * along that direction by {@code δ}, we need {@code Δposition_parent_frame = R_bone · (δ along axis)} — the bone's
     * own rotation has to be applied manually because {@code position} doesn't get rotated by it.</li>
     * <li><b>GLOBAL</b>: {@code δ} is along world X/Y/Z. World shift = {@code parentChain · Δposition_parent}, so
     * {@code Δposition_parent = parentChain^T · δ_world}. Inverse-transform through the parent chain only, NOT through
     * the bone's own rotation.</li>
     * </ul>
     */
    private static void applyBoneTranslate(ModelerGizmoState.DragState drag, double cx, double cy, int w, int h) {
        var s = drag.startSnapshot();
        var bone = s.bone();
        var baseline = drag.startBone();
        if (bone == null || baseline == null) {
            return;
        }
        float delta = GizmoMath.axisDelta(s.geometry(), drag.axis(), drag.sign(), drag.startCursorX(), drag.startCursorY(), cx, cy, w, h);
        if (delta == 0f) {
            return;
        }

        var dLocal = new Vector3f(0, 0, 0);
        dLocal.setComponent(drag.axis(), delta);

        if (s.frame() == ModelerGizmoFrame.LOCAL) {
            var startRot = baseline.rotation();
            var rbone = new Matrix3f()
                .rotateZ((float) Math.toRadians(startRot.z))
                .rotateY((float) Math.toRadians(startRot.y))
                .rotateX((float) Math.toRadians(startRot.x));
            rbone.transform(dLocal);
        } else {
            var parentInv = new Matrix3f(s.boneChainRotation()).transpose();
            parentInv.transform(dLocal);
        }

        var startPos = baseline.position();
        bone.position = new Vec3(startPos.x + dLocal.x, startPos.y + dLocal.y, startPos.z + dLocal.z);
    }

    /**
     * Bone ROTATE — identical algorithm to {@link #applyRotate} but reads/writes the bone's rotation. The rotation is
     * applied around the bone's pivot by {@code applyBone}, so the gizmo (positioned at the pivot) rotates the bone in
     * place. Frame-over-frame angle accumulation around the projected gizmo origin, post-multiply by axis-local
     * rotation, decompose back to Z-Y-X Euler.
     */
    private static void applyBoneRotate(ModelerGizmoState.DragState drag, double cx, double cy, int w, int h) {
        var s = drag.startSnapshot();
        var bone = s.bone();
        var baseline = drag.startBone();
        if (bone == null || baseline == null) {
            return;
        }
        var origin = GizmoMath.projectToScreen(s.geometry().viewPivot(), s.geometry().projection(), w, h);
        if (origin == null) {
            return;
        }

        double currentAngle = Math.atan2(cy - origin.y, cx - origin.x);
        double frameDeltaRad = currentAngle - drag.previousCursorAngleRad();
        while (frameDeltaRad > Math.PI) {
            frameDeltaRad -= 2 * Math.PI;
        }
        while (frameDeltaRad < -Math.PI) {
            frameDeltaRad += 2 * Math.PI;
        }

        var axisView = s.geometry().axis(drag.axis());
        float signFactor = axisView.z > 0 ? -1f : 1f;
        float frameDeltaDegrees = (float) Math.toDegrees(frameDeltaRad) * signFactor;
        float newAccumulated = drag.accumulatedRotationDegrees() + frameDeltaDegrees;

        var startRot = baseline.rotation();
        var mStart = new Matrix3f()
            .rotateZ((float) Math.toRadians(startRot.z))
            .rotateY((float) Math.toRadians(startRot.y))
            .rotateX((float) Math.toRadians(startRot.x));

        float thetaRad = (float) Math.toRadians(newAccumulated);
        var mNew = new Matrix3f(mStart);
        switch (drag.axis()) {
            case 0 -> mNew.rotateX(thetaRad);
            case 1 -> mNew.rotateY(thetaRad);
            default -> mNew.rotateZ(thetaRad);
        }

        bone.rotation = decomposeZYX(mNew);

        ModelerGizmoState.setDrag(
            new ModelerGizmoState.DragState(
                drag.mode(),
                drag.axis(),
                drag.sign(),
                drag.startCube(),
                drag.startBone(),
                drag.startCursorX(),
                drag.startCursorY(),
                drag.startSnapshot(),
                currentAngle,
                newAccumulated
            )
        );
    }

    /**
     * Bone PIVOT translate — moves {@code bone.pivot} while compensating {@code bone.position} to keep the rendered
     * body stationary. Two differences from {@link #applyPivotTranslate}:
     * <ol>
     * <li><b>Pivot shift direction</b>: bone {@code pivot} (like {@code position}) lives in the parent's frame, NOT
     * inside the bone's rotation. So to move the world pivot along the visible (post-rotation) axis by {@code δ}, we
     * shift {@code pivot} by {@code R_bone · (δ along local axis)} — same shape as {@link #applyBoneTranslate}'s
     * LOCAL-frame delta.</li>
     * <li><b>Body compensation includes scale</b>: the bone has a non-trivial scale field. The body transform is
     * {@code T(pos)·T(pivot)·R·S·T(-pivot)}; shifting pivot by {@code Δp} moves the body by {@code (I - R·S)·Δp}, so to
     * cancel we set {@code Δpos = (R·S - I)·Δp}. Cubes don't have a scale, which is why the cube version uses just
     * {@code R}.</li>
     * </ol>
     */
    private static void applyBonePivotTranslate(ModelerGizmoState.DragState drag, double cx, double cy, int w, int h) {
        var s = drag.startSnapshot();
        var bone = s.bone();
        var baseline = drag.startBone();
        if (bone == null || baseline == null) {
            return;
        }
        float delta = GizmoMath.axisDelta(s.geometry(), drag.axis(), drag.sign(), drag.startCursorX(), drag.startCursorY(), cx, cy, w, h);
        if (delta == 0f) {
            return;
        }

        var startRot = baseline.rotation();
        var rbone = new Matrix3f()
            .rotateZ((float) Math.toRadians(startRot.z))
            .rotateY((float) Math.toRadians(startRot.y))
            .rotateX((float) Math.toRadians(startRot.x));

        // Δp in parent frame. LOCAL: rotate cube-local δ by R_bone. GLOBAL: inverse-transform world δ through parent.
        var dPivot = new Vector3f(0, 0, 0);
        dPivot.setComponent(drag.axis(), delta);
        if (s.frame() == ModelerGizmoFrame.LOCAL) {
            rbone.transform(dPivot);
        } else {
            var parentInv = new Matrix3f(s.boneChainRotation()).transpose();
            parentInv.transform(dPivot);
        }

        // Body compensation: Δpos = (R*S - I) * Δp. Build R*S explicitly so non-unit bone scale stays correct.
        var startScale = baseline.scale();
        var rs = new Matrix3f(rbone).scale((float) startScale.x, (float) startScale.y, (float) startScale.z);
        var rsDp = new Vector3f(dPivot);
        rs.transform(rsDp);
        var dPos = new Vector3f(rsDp).sub(dPivot);

        var startPivot = baseline.pivot();
        var startPos = baseline.position();
        bone.pivot = new Vec3(startPivot.x + dPivot.x, startPivot.y + dPivot.y, startPivot.z + dPivot.z);
        bone.position = new Vec3(startPos.x + dPos.x, startPos.y + dPos.y, startPos.z + dPos.z);
    }
}
