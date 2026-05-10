package com.blib.engine.blockselection;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import com.blib.engine.session.EngineSession;

/**
 * Axis-aligned scale gizmo for the capture AABB. After both corners are set via two-corner click, six face handles (±X,
 * ±Y, ±Z) become draggable in the viewport — the user grabs a face, drags it along its axis, and the AABB grows or
 * shrinks while the wireframe updates live. Dragging snaps to integer block boundaries so volumes stay aligned to the
 * capture grid.
 * <p>
 * Drag math: when a face is grabbed we capture the world point at the face center and an axis-containing plane whose
 * normal is most aligned with the camera (so cursor motion has the largest meaningful projection onto the dragged
 * axis). Each frame we ray-plane-intersect the cursor ray against that plane and project the hit onto the dragged axis;
 * the projected delta becomes the new face position. AABB clamping prevents a face from passing its opposite (no
 * negative volumes), and the AABB stays within the volume cap that {@link BlockSelection} enforces.
 * <p>
 * Hover state is recomputed each frame by the renderer (which has the cursor ray) and read by both renderer and input
 * handler — the input handler only needs hover info to know which face the user clicked, and the renderer uses it to
 * color the handle. No per-frame caching outside the drag itself.
 */
@ApiStatus.Internal
public final class BlockSelectionScaleGizmo {

    /**
     * Length of each face arrow's shaft (from the face plane outward to the tip-cube center), in world units at the
     * reference distance. Shorter than the translate gizmo's arrows ({@code 2.0}) because there are six of them and
     * they sit on the AABB surface where space is tight; the shorter length keeps the six face arrows visually distinct
     * from the translate arrows that share the AABB-center origin.
     */
    public static final double ARROW_LENGTH = 1.0;

    /** Half-thickness of the arrow shaft perpendicular to its axis. */
    public static final double SHAFT_HALF_WIDTH = 0.05;

    /**
     * Half-extent of the cube at the arrow tip — the visual "knob" the user grabs. Also drives the perpendicular
     * picking radius so the user can click anywhere along the arrow's visible footprint and still grab.
     */
    public static final double TIP_HALF_WIDTH = 0.16;

    /**
     * Gap between the AABB face plane and the arrow tail, along the face normal. Zero means the shaft starts flush with
     * the wireframe; small positive values would let the arrow detach slightly from the volume edge.
     */
    public static final double ARROW_OFFSET = 0.0;

    /**
     * Reference camera distance for which the base arrow geometry ({@link #ARROW_LENGTH}, {@link #TIP_HALF_WIDTH},
     * etc.) is sized appropriately. Beyond it arrows grow proportionally so on-screen size stays roughly constant
     * ("Blender-style" gizmo).
     */
    private static final double SCALE_REFERENCE_DISTANCE = 16.0;

    /** Floor on the per-handle scale. {@code 1.0} means handles never shrink below the base size. */
    private static final double MIN_SCALE = 1.0;

    /** Ceiling on the per-handle scale. Prevents absurd handle sizes when the camera is hundreds of blocks away. */
    private static final double MAX_SCALE = 20.0;

    /**
     * Compute the scale factor for handle visuals + picking, given the camera position and the world point the handle
     * is anchored to (typically the face center). Linear in distance with a floor (no shrinking below base size) and a
     * ceiling (no runaway growth at extreme distances). Renderer and picker call this with the same anchor + camera so
     * visuals and hit boxes always agree.
     */
    public static double scaleForCamera(net.minecraft.world.phys.Vec3 cameraPos, net.minecraft.world.phys.Vec3 anchor) {
        var dx = anchor.x - cameraPos.x;
        var dy = anchor.y - cameraPos.y;
        var dz = anchor.z - cameraPos.z;
        var distance = Math.sqrt(dx * dx + dy * dy + dz * dz);
        var raw = distance / SCALE_REFERENCE_DISTANCE;
        return Math.max(MIN_SCALE, Math.min(MAX_SCALE, raw));
    }

    /**
     * Which face of the AABB a handle controls. The axis + sign drives drag direction; {@link #targetIsMax} determines
     * whether dragging this face moves {@code max} or {@code min} along its axis. {@code MIN_X} face is the −X face —
     * dragging it changes the AABB's minX; {@code MAX_X} is +X and changes maxX.
     */
    public enum Face {

        MIN_X(Direction.WEST, false),
        MAX_X(Direction.EAST, true),
        MIN_Y(Direction.DOWN, false),
        MAX_Y(Direction.UP, true),
        MIN_Z(Direction.NORTH, false),
        MAX_Z(Direction.SOUTH, true);

        private final Direction direction;

        private final boolean targetIsMax;

        Face(Direction direction, boolean targetIsMax) {
            this.direction = direction;
            this.targetIsMax = targetIsMax;
        }

        public Direction direction() {
            return direction;
        }

        public Direction.Axis axis() {
            return direction.getAxis();
        }

        /** True if dragging this face moves the AABB's max along the axis; false → min. */
        public boolean targetIsMax() {
            return targetIsMax;
        }

        /** Unit vector along the face's outward normal. */
        public Vec3 normal() {
            return new Vec3(direction.getStepX(), direction.getStepY(), direction.getStepZ());
        }
    }

    private static @Nullable Face hoveredFace;

    private static @Nullable DragState drag;

    private BlockSelectionScaleGizmo() {}

    public static @Nullable Face hoveredFace() {
        return hoveredFace;
    }

    public static void setHoveredFace(@Nullable Face face) {
        hoveredFace = face;
    }

    public static @Nullable Face draggingFace() {
        return drag == null ? null : drag.face;
    }

    public static boolean isDragging() {
        return drag != null;
    }

    /**
     * Begin dragging {@code face}. Snapshots the AABB along the face's axis and computes a drag plane whose normal is
     * perpendicular to the dragged axis and roughly facing the camera (so cursor pixel motion projects cleanly onto the
     * axis). After this returns successfully, subsequent {@link #updateDrag} calls move the face.
     */
    public static void beginDrag(Face face, EngineSession session) {
        var aabb = BlockSelection.aabb();
        if (aabb.isEmpty()) {
            return;
        }
        var cornerAOrig = BlockSelection.cornerA();
        var cornerBOrig = BlockSelection.cornerB();
        if (cornerAOrig == null || cornerBOrig == null) {
            return;
        }
        var box = aabb.get();
        // Inclusive block bounds — convert the AABB's exclusive max back to inclusive integer bounds.
        var minX = (int) Math.floor(box.minX);
        var minY = (int) Math.floor(box.minY);
        var minZ = (int) Math.floor(box.minZ);
        var maxX = (int) Math.floor(box.maxX) - 1;
        var maxY = (int) Math.floor(box.maxY) - 1;
        var maxZ = (int) Math.floor(box.maxZ) - 1;

        // Face center in world space. The face is the appropriate AABB face; pick the world Y/Z (etc.) center for
        // the drag plane anchor so the drag math has a stable origin regardless of where the user grabbed.
        var center = aabbFaceCenter(face, minX, minY, minZ, maxX, maxY, maxZ);
        var axis = face.normal();

        // Drag plane: contains axis, normal is perpendicular to axis and roughly facing camera. The plane's normal
        // is `axis × (axis × camForward)` simplified — but easier: pick the axis-perpendicular vector that has the
        // largest dot with cameraForward, then its perpendicular-in-axis-plane is the plane normal.
        // Use the camera position vanilla rendered the most recent frame from — matches the wireframe's apparent
        // origin during partial-tick interpolation (rendered cam lags session.cameraPosition by up to half a tick).
        var capturedCam = com.blib.engine.session.EngineCameraFrame.cameraPosition();
        var camPos = capturedCam != null ? capturedCam : session.cameraPosition();
        var camToCenter = center.subtract(camPos);
        // Project camToCenter onto the plane perpendicular to axis, then normalize — that gives the in-plane
        // direction we want the drag plane normal to align with.
        var inPlane = camToCenter.subtract(axis.scale(camToCenter.dot(axis)));
        if (inPlane.lengthSqr() < 1.0e-6) {
            // Looking nearly straight down the axis — fall back to a world-up reference.
            inPlane = axis.cross(new Vec3(0, 1, 0));
            if (inPlane.lengthSqr() < 1.0e-6) {
                inPlane = axis.cross(new Vec3(0, 0, 1));
            }
        }
        var planeNormal = inPlane.normalize();

        var initialValue = switch (face.axis()) {
            case X -> face.targetIsMax() ? maxX : minX;
            case Y -> face.targetIsMax() ? maxY : minY;
            case Z -> face.targetIsMax() ? maxZ : minZ;
        };

        // Sample the click cursor's projection onto the axis so the first frame's delta is zero. Without this, the
        // user grabbing the handle (offset by ~HANDLE_OFFSET × scale outside the face) instantly grows the AABB by
        // round(HANDLE_OFFSET × scale) blocks before they've moved the cursor.
        var initialAxisOffset = computeInitialAxisOffset(session, center, planeNormal, axis, camPos);

        drag = new DragState(
            face,
            planeNormal,
            center,
            axis,
            initialAxisOffset,
            initialValue,
            minX,
            minY,
            minZ,
            maxX,
            maxY,
            maxZ,
            cornerAOrig,
            cornerBOrig
        );
    }

    private static double computeInitialAxisOffset(
        EngineSession session,
        Vec3 planePoint,
        Vec3 planeNormal,
        Vec3 axisDir,
        Vec3 camPos
    ) {
        var rayDir = com.blib.engine.jigsaw.JigsawPlacementCursor.cursorRayDirection(session);
        if (rayDir == null) {
            return 0.0;
        }
        var denom = rayDir.dot(planeNormal);
        if (Math.abs(denom) < 1.0e-6) {
            return 0.0;
        }
        var t = planePoint.subtract(camPos).dot(planeNormal) / denom;
        if (t <= 0) {
            return 0.0;
        }
        var hit = camPos.add(rayDir.scale(t));
        return hit.subtract(planePoint).dot(axisDir);
    }

    /**
     * Update the dragged face's position based on the current cursor ray. The new value is clamped so the dragged face
     * can't pass its opposite (preventing zero/negative-volume AABBs); the drag origin remains the value captured at
     * {@link #beginDrag} so the user can return to the original position by un-dragging.
     */
    public static void updateDrag(EngineSession session, Vec3 cursorRayDir) {
        var d = drag;
        if (d == null) {
            return;
        }
        // Use the camera position vanilla rendered the most recent frame from — matches the wireframe's apparent
        // origin during partial-tick interpolation (rendered cam lags session.cameraPosition by up to half a tick).
        var capturedCam = com.blib.engine.session.EngineCameraFrame.cameraPosition();
        var camPos = capturedCam != null ? capturedCam : session.cameraPosition();

        // Ray-plane intersection: t = (planePoint - rayOrigin) · planeNormal / (rayDir · planeNormal).
        var denom = cursorRayDir.dot(d.planeNormal);
        if (Math.abs(denom) < 1.0e-6) {
            // Cursor ray is parallel to plane — can't intersect; ignore this frame.
            return;
        }
        var t = d.planePoint.subtract(camPos).dot(d.planeNormal) / denom;
        if (t <= 0) {
            // Plane is behind the camera; ignore.
            return;
        }
        var hit = camPos.add(cursorRayDir.scale(t));

        // Project the hit onto the dragged axis. Subtract the initial click offset so the first frame produces a
        // delta of zero — without this we'd snap the dragged face by round(HANDLE_OFFSET × scale) blocks the moment
        // the user clicks the handle, before any cursor motion.
        var deltaAlongAxis = hit.subtract(d.planePoint).dot(d.axis) - d.initialAxisOffset;
        // Direction sign: face axis dot (positive direction toward face's outward normal). For a +X face,
        // axis = (1,0,0), and a positive deltaAlongAxis along +X grows the AABB. For a -X face, axis = (-1,0,0),
        // so dragging "outward" along that axis (which is -X in world) gives a positive deltaAlongAxis. We then
        // need to subtract that from minX (because growing -X face means moving minX leftward in world).
        // Compute the new bound along the axis from initialValue + (signed) delta.
        // For MAX faces: newValue = initial + delta(rounded). For MIN faces: newValue = initial - delta(rounded).
        var rounded = (int) Math.round(deltaAlongAxis);
        var newValue = d.face.targetIsMax() ? d.initialValue + rounded : d.initialValue - rounded;

        // Clamp so the dragged face doesn't pass its opposite.
        var min = switch (d.face.axis()) {
            case X -> d.minX;
            case Y -> d.minY;
            case Z -> d.minZ;
        };
        var max = switch (d.face.axis()) {
            case X -> d.maxX;
            case Y -> d.maxY;
            case Z -> d.maxZ;
        };
        if (d.face.targetIsMax()) {
            newValue = Math.max(newValue, min);
        } else {
            newValue = Math.min(newValue, max);
        }

        // Apply: build new min/max from the snapshotted bounds, replacing the dragged-face component.
        var newMinX = d.minX;
        var newMinY = d.minY;
        var newMinZ = d.minZ;
        var newMaxX = d.maxX;
        var newMaxY = d.maxY;
        var newMaxZ = d.maxZ;
        switch (d.face) {
            case MIN_X -> newMinX = newValue;
            case MAX_X -> newMaxX = newValue;
            case MIN_Y -> newMinY = newValue;
            case MAX_Y -> newMaxY = newValue;
            case MIN_Z -> newMinZ = newValue;
            case MAX_Z -> newMaxZ = newValue;
        }
        // Map each component of the new corners by checking which extreme each *original* corner held — preserves
        // A/B labelling so the user's corner-marker rendering doesn't flicker between min/max as they drag a face.
        // setBounds would componentwise-min/max-reassign and swap labels.
        //
        // Degenerate-axis case: when the AABB is 1-block-flat on the dragged axis (origMin == origMax), both corners
        // hold the same value on that axis and the {@code == origMin} test is ambiguously true for both — both
        // would get assigned newMin, and the drag's newMax value would be dropped (the AABB never grows). Tie-break
        // by giving cornerA the new max and cornerB the new min on the dragged axis; after one drag the axis is
        // non-degenerate and the standard per-corner mapping resumes.
        var dragAxis = d.face.axis();
        var degenX = dragAxis == Direction.Axis.X && d.minX == d.maxX;
        var degenY = dragAxis == Direction.Axis.Y && d.minY == d.maxY;
        var degenZ = dragAxis == Direction.Axis.Z && d.minZ == d.maxZ;
        var newCornerA = new BlockPos(
            degenX ? newMaxX : (d.cornerAOrig.getX() == d.minX ? newMinX : newMaxX),
            degenY ? newMaxY : (d.cornerAOrig.getY() == d.minY ? newMinY : newMaxY),
            degenZ ? newMaxZ : (d.cornerAOrig.getZ() == d.minZ ? newMinZ : newMaxZ)
        );
        var newCornerB = new BlockPos(
            degenX ? newMinX : (d.cornerBOrig.getX() == d.minX ? newMinX : newMaxX),
            degenY ? newMinY : (d.cornerBOrig.getY() == d.minY ? newMinY : newMaxY),
            degenZ ? newMinZ : (d.cornerBOrig.getZ() == d.minZ ? newMinZ : newMaxZ)
        );
        BlockSelection.setCornersDirect(newCornerA, newCornerB);
    }

    public static void endDrag() {
        drag = null;
    }

    public static void clear() {
        hoveredFace = null;
        drag = null;
    }

    /**
     * Pick the closest face handle under the cursor, or {@code null} if the cursor doesn't intersect any. Returns the
     * closest hit by ray-distance — handles can overlap in screen space (e.g. when the camera is near-axis), and
     * "closest along the cursor ray" gives the user the front-most handle which is what they expect to grab.
     * <p>
     * Returns null if the AABB is incomplete (only one corner picked) or the cursor is outside the viewport, since
     * gizmos can't be hit in either case.
     */
       /** Pick result that pairs the hit face with the ray-distance, so callers can compare against other gizmos. */
    public record FaceHit(
        Face face,
        double t
    ) {}

    public static @Nullable Face pickUnderCursor(EngineSession session) {
        var hit = pickUnderCursorWithDistance(session);
        return hit == null ? null : hit.face();
    }

    public static @Nullable FaceHit pickUnderCursorWithDistance(EngineSession session) {
        var aabb = BlockSelection.aabb();
        if (aabb.isEmpty()) {
            return null;
        }
        var rayDir = com.blib.engine.jigsaw.JigsawPlacementCursor.cursorRayDirection(session);
        if (rayDir == null) {
            return null;
        }
        var box = aabb.get();
        var minX = (int) Math.floor(box.minX);
        var minY = (int) Math.floor(box.minY);
        var minZ = (int) Math.floor(box.minZ);
        var maxX = (int) Math.floor(box.maxX) - 1;
        var maxY = (int) Math.floor(box.maxY) - 1;
        var maxZ = (int) Math.floor(box.maxZ) - 1;
        // Use the captured camera position from the most recent render frame so picking origin matches the rendered
        // camera (especially important during partial-tick interpolation between camera-motion ticks).
        var capturedCam = com.blib.engine.session.EngineCameraFrame.cameraPosition();
        var origin = capturedCam != null ? capturedCam : session.cameraPosition();

        Face closest = null;
        var closestT = Double.POSITIVE_INFINITY;
        for (var face : Face.values()) {
            // Per-face scale so each arrow's pick box matches its rendered geometry exactly — far arrows get bigger
            // pick boxes because they're drawn bigger. With proper unprojection there's no precision gap to
            // compensate for, so the pick box matches visible geometry tightly.
            var faceCenter = aabbFaceCenter(face, minX, minY, minZ, maxX, maxY, maxZ);
            var scale = scaleForCamera(origin, faceCenter);
            var handle = HandleBox.forFace(face, minX, minY, minZ, maxX, maxY, maxZ, scale);
            var t = rayHitsBox(origin.x, origin.y, origin.z, rayDir.x, rayDir.y, rayDir.z, handle);
            if (t > 0 && t < closestT) {
                closestT = t;
                closest = face;
            }
        }
        return closest == null ? null : new FaceHit(closest, closestT);
    }

    /**
     * Slab-method ray vs. AABB intersection. Returns the parametric t for the first hit (positive only) or NaN if the
     * ray misses. {@code rayDir} need not be normalized — t is in units of the input direction.
     */
    private static double rayHitsBox(double ox, double oy, double oz, double dx, double dy, double dz, HandleBox box) {
        var tMin = Double.NEGATIVE_INFINITY;
        var tMax = Double.POSITIVE_INFINITY;
        for (var i = 0; i < 3; i++) {
            var o = i == 0 ? ox : (i == 1 ? oy : oz);
            var d = i == 0 ? dx : (i == 1 ? dy : dz);
            var min = i == 0 ? box.minX : (i == 1 ? box.minY : box.minZ);
            var max = i == 0 ? box.maxX : (i == 1 ? box.maxY : box.maxZ);
            if (Math.abs(d) < 1.0e-9) {
                if (o < min || o > max) {
                    return Double.NaN;
                }
                continue;
            }
            var t1 = (min - o) / d;
            var t2 = (max - o) / d;
            if (t1 > t2) {
                var swap = t1;
                t1 = t2;
                t2 = swap;
            }
            tMin = Math.max(tMin, t1);
            tMax = Math.min(tMax, t2);
            if (tMin > tMax) {
                return Double.NaN;
            }
        }
        return tMin > 0 ? tMin : tMax;
    }

    /** World-space center of one face of the inclusive integer AABB defined by {@code (min, max)}. */
    public static Vec3 aabbFaceCenter(Face face, int minX, int minY, int minZ, int maxX, int maxY, int maxZ) {
        var cx = (minX + maxX) / 2.0 + 0.5;
        var cy = (minY + maxY) / 2.0 + 0.5;
        var cz = (minZ + maxZ) / 2.0 + 0.5;
        return switch (face) {
            case MIN_X -> new Vec3(minX, cy, cz);
            case MAX_X -> new Vec3(maxX + 1, cy, cz);
            case MIN_Y -> new Vec3(cx, minY, cz);
            case MAX_Y -> new Vec3(cx, maxY + 1, cz);
            case MIN_Z -> new Vec3(cx, cy, minZ);
            case MAX_Z -> new Vec3(cx, cy, maxZ + 1);
        };
    }

    /**
     * World-space AABB enclosing one face arrow's pickable footprint. The arrow extends from the face plane outward
     * along the face normal by {@link #ARROW_LENGTH}; the tip cube extends a further {@link #TIP_HALF_WIDTH} past the
     * shaft end. Perpendicular extents (the two axes orthogonal to the face normal) match the tip cube's half-width so
     * users can click anywhere along the arrow's visible footprint and still grab.
     */
    public record HandleBox(
        double minX,
        double minY,
        double minZ,
        double maxX,
        double maxY,
        double maxZ
    ) {

        /**
         * Compute the arrow pick box for one face. Picker and renderer share {@link #scaleForCamera} so the box matches
         * the rendered geometry at every camera distance.
         */
        public static HandleBox forFace(Face face, int minX, int minY, int minZ, int maxX, int maxY, int maxZ, double scale) {
            var center = aabbFaceCenter(face, minX, minY, minZ, maxX, maxY, maxZ);
            var n = face.normal();
            var perp = TIP_HALF_WIDTH * scale;
            var startAlong = ARROW_OFFSET * scale;
            // Outward extent: shaft length + tip cube's half-extent past the shaft end.
            var endAlong = startAlong + ARROW_LENGTH * scale + perp;

            var p1x = center.x + n.x * startAlong;
            var p1y = center.y + n.y * startAlong;
            var p1z = center.z + n.z * startAlong;
            var p2x = center.x + n.x * endAlong;
            var p2y = center.y + n.y * endAlong;
            var p2z = center.z + n.z * endAlong;

            // Perpendicular thickness applies only to axes orthogonal to the face normal.
            var bx = (n.x == 0) ? perp : 0;
            var by = (n.y == 0) ? perp : 0;
            var bz = (n.z == 0) ? perp : 0;
            return new HandleBox(
                Math.min(p1x, p2x) - bx,
                Math.min(p1y, p2y) - by,
                Math.min(p1z, p2z) - bz,
                Math.max(p1x, p2x) + bx,
                Math.max(p1y, p2y) + by,
                Math.max(p1z, p2z) + bz
            );
        }
    }

    /** Internal drag state, captured at {@link #beginDrag} and read each frame in {@link #updateDrag}. */
    private record DragState(
        Face face,
        Vec3 planeNormal,
        Vec3 planePoint,
        Vec3 axis,
        double initialAxisOffset,
        int initialValue,
        int minX,
        int minY,
        int minZ,
        int maxX,
        int maxY,
        int maxZ,
        BlockPos cornerAOrig,
        BlockPos cornerBOrig
    ) {}
}
