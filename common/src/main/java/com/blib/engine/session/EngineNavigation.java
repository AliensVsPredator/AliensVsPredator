package com.blib.engine.session;

import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.ApiStatus;

import com.blib.engine.jigsaw.placement.JigsawWorldRaycast;
import com.blib.engine.selection.EntitySelectable;
import com.blib.engine.selection.JigsawBlockSelectable;
import com.blib.engine.selection.SelectionManager;

/**
 * Camera-control math for engine mode. Driven directly from workspace mouse events: the {@code ViewportPanel} calls the
 * static helpers below in response to mouse drag / scroll / click. {@link #tick()} only handles previous-tick
 * snapshotting for partial-tick interpolation — all input application is now event-driven and immediate.
 */
@ApiStatus.Internal
public final class EngineNavigation {

    private static final float ORBIT_SENSITIVITY = 0.4f;

    /** World-blocks of pan per pixel of mouse delta, scaled by camera distance from pivot. */
    private static final double PAN_SENSITIVITY = 0.0025;

    /** Fraction of pivot-distance to step per scroll click (positive scroll = closer to pivot). */
    private static final double ZOOM_FRACTION_PER_CLICK = 0.15;

    /**
     * Fraction of pivot-distance to step per pixel of Ctrl+MMB drag. Drag-up = zoom-in (positive {@code rawDy} =
     * cursor-down = zoom out). Tuned so a 100-pixel drag is roughly equivalent to ~3 scroll-wheel clicks.
     */
    private static final double DOLLY_FRACTION_PER_PIXEL = 0.005;

    /** Hard floor on camera-pivot distance, so wheel-zoom can't pass through the pivot. */
    private static final double MIN_PIVOT_DISTANCE = 0.5;

    /** Fallback distance for orbit pivot when the screen-center raycast hits nothing. */
    private static final double DEFAULT_PIVOT_DISTANCE = 8.0;

    /**
     * Max raycast distance when computing the orbit pivot from the cursor ray. Set high enough to reach the far edge of
     * loaded chunks at any practical render distance — Minecraft's {@code clip} short-circuits at the first visible
     * block hit, so a long max only costs the walk past unloaded chunks (which is fast). The 64-block cap users
     * complained about was the source of "MMB-orbit feels broken when I click anything far away" — the raycast missed
     * and the pivot snapped to {@link #DEFAULT_PIVOT_DISTANCE} blocks ahead of camera instead.
     */
    private static final double PIVOT_RAYCAST_DISTANCE = 8192.0;

    /** Max raycast distance for click-to-select against entities. */
    private static final double SELECTION_RAYCAST_DISTANCE = 96.0;

    private EngineNavigation() {}

    /**
     * Snapshot the previous-tick camera pose so the camera mixin can lerp between prev and current at partial-tick.
     * Called from the per-client-tick callback in the loader bootstrap.
     */
    public static void tick() {
        var session = EngineMode.get().session();
        if (session == null) {
            return;
        }

        session.snapshotForTick();
    }

    /**
     * Begin an orbit drag at the given viewport-relative cursor position. Computes the pivot via a forward raycast from
     * the camera through the cursor (so the pivot is the point under the cursor when the drag starts), with a fallback
     * distance if the ray misses.
     */
    public static void beginOrbitDrag(EngineSession session, double relX, double relY) {
        session.setPivot(computePivot(session, relX, relY));
        session.setOrbitDragActive(true);
    }

    public static void endOrbitDrag(EngineSession session) {
        session.setOrbitDragActive(false);
    }

    /**
     * Apply an orbit rotation around {@link EngineSession#pivot()}. {@code dx} / {@code dy} are cursor pixel deltas
     * (positive dx = rightward cursor motion → camera rotates rightward around pivot).
     */
    public static void applyOrbitDelta(EngineSession session, double dx, double dy) {
        if (dx == 0 && dy == 0) {
            return;
        }

        var pivot = session.pivot();
        var distance = session.cameraPosition().subtract(pivot).length();
        if (distance < MIN_PIVOT_DISTANCE) {
            distance = MIN_PIVOT_DISTANCE;
        }

        var newYaw = session.yaw() + (float) (dx * ORBIT_SENSITIVITY);
        var newPitch = clampPitch(session.pitch() + (float) (dy * ORBIT_SENSITIVITY));
        session.setRotation(newYaw, newPitch);

        var forward = forwardVector(newYaw, newPitch);
        session.setPosition(
            pivot.x - forward.x * distance,
            pivot.y - forward.y * distance,
            pivot.z - forward.z * distance
        );
    }

    /**
     * Apply a screen-plane pan. Camera moves in the same screen-X direction as the cursor (drag right → camera right →
     * world appears to scroll left); cursor-down moves the camera up so the world appears to scroll down with the
     * cursor. Pan speed scales with pivot distance so distant views pan at usable speed.
     */
    public static void applyPanDelta(EngineSession session, double dx, double dy) {
        if (dx == 0 && dy == 0) {
            return;
        }

        var distance = session.cameraPosition().subtract(session.pivot()).length();
        var scale = PAN_SENSITIVITY * Math.max(distance, 1.0);

        var right = screenRight(session.yaw());
        var up = screenUp(session.yaw(), session.pitch());

        var deltaX = right.x * dx * scale + up.x * dy * scale;
        var deltaY = right.y * dx * scale + up.y * dy * scale;
        var deltaZ = right.z * dx * scale + up.z * dy * scale;

        session.translate(deltaX, deltaY, deltaZ);
        session.setPivot(session.pivot().add(deltaX, deltaY, deltaZ));
    }

    /**
     * Apply a per-pixel multiplicative dolly toward / away from the pivot, driven by Ctrl+MMB drag. Drag-up
     * ({@code rawDy < 0}) zooms in; drag-down zooms out. Mirrors {@link #applyZoomScroll} but scales by mouse-pixel
     * distance instead of scroll clicks.
     */
    public static void applyDollyDelta(EngineSession session, double rawDy) {
        if (rawDy == 0) {
            return;
        }

        var pivot = session.pivot();
        var offset = session.cameraPosition().subtract(pivot);
        var distance = offset.length();
        if (distance < 1.0E-4) {
            return;
        }

        var factor = Math.pow(1.0 - DOLLY_FRACTION_PER_PIXEL, -rawDy);
        var newDistance = Math.max(MIN_PIVOT_DISTANCE, distance * factor);
        var dir = offset.scale(1.0 / distance);

        session.setPosition(
            pivot.x + dir.x * newDistance,
            pivot.y + dir.y * newDistance,
            pivot.z + dir.z * newDistance
        );
    }

    /**
     * Apply a multiplicative zoom toward / away from the pivot. {@code scrollDy > 0} = scroll up = zoom in (camera
     * moves toward pivot).
     */
    public static void applyZoomScroll(EngineSession session, double scrollDy) {
        if (scrollDy == 0) {
            return;
        }

        var pivot = session.pivot();
        var offset = session.cameraPosition().subtract(pivot);
        var distance = offset.length();
        if (distance < 1.0E-4) {
            return;
        }

        var factor = Math.pow(1.0 - ZOOM_FRACTION_PER_CLICK, scrollDy);
        var newDistance = Math.max(MIN_PIVOT_DISTANCE, distance * factor);
        var dir = offset.scale(1.0 / distance);

        session.setPosition(
            pivot.x + dir.x * newDistance,
            pivot.y + dir.y * newDistance,
            pivot.z + dir.z * newDistance
        );
    }

    /**
     * Ray-pick the closest selectable along the cursor-through-camera ray. Tries both a {@link LivingEntity} hit and a
     * jigsaw-block hit (via {@link JigsawWorldRaycast}); whichever is closer to the camera wins. On miss, clears any
     * existing selection.
     * <p>
     * {@code (relX, relY)} are in {@code [0, 1]} relative to the rendered viewport (full-screen render — the viewport
     * panel just downsamples this, so screen-relative and panel-relative cursor positions map to the same world ray).
     */
    public static void performSelectionAt(EngineSession session, double relX, double relY) {
        var mc = Minecraft.getInstance();
        if (mc.level == null || mc.player == null) {
            return;
        }

        // Prefer the camera position + ray direction that vanilla actually rendered with — analytical reconstruction
        // accumulates a small angular error when FOV modifiers are active (sprint, item-use, zoom, fluid), which
        // shows up as a constant pixel offset between the cursor and the selected entity's hitbox. Same pattern
        // JigsawPlacementCursor uses for block picking. Falls back to analytical reconstruction when no frame has
        // been captured yet (first render before the camera mixin runs).
        var capturedOrigin = EngineCameraFrame.cameraPosition();
        var origin = capturedOrigin != null ? capturedOrigin : session.cameraPosition();
        Vec3 rayDir = null;
        if (EngineCameraFrame.hasFrame()) {
            rayDir = EngineCameraFrame.cursorRayDirection(relX, relY);
        }
        if (rayDir == null) {
            rayDir = cursorRayDirection(session, relX, relY);
        }
        var end = origin.add(
            rayDir.x * SELECTION_RAYCAST_DISTANCE,
            rayDir.y * SELECTION_RAYCAST_DISTANCE,
            rayDir.z * SELECTION_RAYCAST_DISTANCE
        );

        // The search AABB has to span the full ray path from camera origin to endpoint. The detached engine camera
        // can be far from the player body, so anchoring at the player would search the wrong region of space.
        var aabb = new AABB(origin, end).inflate(1.0);

        var entityHit = ProjectileUtil.getEntityHitResult(
            mc.player,
            origin,
            end,
            aabb,
            entity -> !entity.isSpectator() && entity != mc.player && entity instanceof LivingEntity,
            SELECTION_RAYCAST_DISTANCE * SELECTION_RAYCAST_DISTANCE
        );

        // Jigsaw-block raycast reuses the same camera-cursor ray geometry as the snap resolver, so the inspector's
        // pick matches what the user sees as the placement preview's anchor candidate.
        var jigsawTarget = JigsawWorldRaycast.raycastJigsaw(session);

        var entityDistSq = (entityHit != null && entityHit.getEntity() instanceof LivingEntity)
            ? entityHit.getLocation().distanceToSqr(origin)
            : Double.POSITIVE_INFINITY;
        // Block-distance reference is the cube center — close enough to the bbox-hit reference used for entities for
        // the "which is closer" heuristic to feel right; both are within ~0.5 blocks of the actual surface hit.
        var blockDistSq = jigsawTarget != null
            ? Vec3.atCenterOf(jigsawTarget.worldPos()).distanceToSqr(origin)
            : Double.POSITIVE_INFINITY;

        if (entityDistSq == Double.POSITIVE_INFINITY && blockDistSq == Double.POSITIVE_INFINITY) {
            SelectionManager.clear();
        } else if (entityDistSq <= blockDistSq) {
            SelectionManager.selectSingle(new EntitySelectable((LivingEntity) entityHit.getEntity()));
        } else {
            SelectionManager.selectSingle(new JigsawBlockSelectable(jigsawTarget.worldPos()));
        }
    }

    /**
     * Compute the pivot for an orbit drag: ray cast from the camera through the cursor, take the hit point. Falls back
     * to a fixed distance forward when the ray misses (open sky / void).
     */
    private static Vec3 computePivot(EngineSession session, double relX, double relY) {
        var mc = Minecraft.getInstance();
        var origin = session.cameraPosition();
        var rayDir = cursorRayDirection(session, relX, relY);
        var end = origin.add(
            rayDir.x * PIVOT_RAYCAST_DISTANCE,
            rayDir.y * PIVOT_RAYCAST_DISTANCE,
            rayDir.z * PIVOT_RAYCAST_DISTANCE
        );

        if (mc.level == null) {
            return origin.add(rayDir.x * DEFAULT_PIVOT_DISTANCE, rayDir.y * DEFAULT_PIVOT_DISTANCE, rayDir.z * DEFAULT_PIVOT_DISTANCE);
        }

        var hit = mc.level.clip(new ClipContext(origin, end, ClipContext.Block.VISUAL, ClipContext.Fluid.NONE, mc.player));
        if (hit.getType() == HitResult.Type.MISS) {
            return origin.add(rayDir.x * DEFAULT_PIVOT_DISTANCE, rayDir.y * DEFAULT_PIVOT_DISTANCE, rayDir.z * DEFAULT_PIVOT_DISTANCE);
        }
        return hit.getLocation();
    }

    /**
     * Unproject {@code (relX, relY)} (cursor in {@code [0, 1]} viewport-relative coords) into a world ray direction
     * using the player's FOV and the full-window aspect — the world is rendered at full window then downsampled into
     * the viewport panel, so the relative cursor position maps directly to the same world ray.
     */
    public static Vec3 cursorRayDirection(EngineSession session, double relX, double relY) {
        var mc = Minecraft.getInstance();
        var window = mc.getWindow();
        var width = window.getScreenWidth();
        var height = window.getScreenHeight();

        if (width <= 0 || height <= 0) {
            return forwardVector(session.yaw(), session.pitch());
        }

        var ndcX = 2.0 * relX - 1.0;
        var ndcY = 1.0 - 2.0 * relY;

        int fovDeg = mc.options.fov().get();
        var halfHeight = Math.tan(Math.toRadians(fovDeg) / 2.0);
        var halfWidth = halfHeight * (double) width / height;

        var forward = forwardVector(session.yaw(), session.pitch());
        var up = screenUp(session.yaw(), session.pitch());
        var right = screenRight(session.yaw());

        // ndcX is subtracted because `screenRight` is the world direction the camera moves to make the world appear
        // to scroll right (drag-the-world pan), which is opposite the world direction visible at the right edge of
        // the screen. A cursor with ndcX > 0 (right of screen) needs a ray with -screenRight world component.
        var dx = forward.x + ndcY * halfHeight * up.x - ndcX * halfWidth * right.x;
        var dy = forward.y + ndcY * halfHeight * up.y - ndcX * halfWidth * right.y;
        var dz = forward.z + ndcY * halfHeight * up.z - ndcX * halfWidth * right.z;

        var raw = new Vec3(dx, dy, dz);
        return raw.lengthSqr() < 1.0E-10 ? forward : raw.normalize();
    }

    /** Forward unit vector for MC yaw/pitch, mirroring {@code Entity.calculateViewVector}. */
    private static Vec3 forwardVector(float yaw, float pitch) {
        var yawRad = Math.toRadians(yaw);
        var pitchRad = Math.toRadians(pitch);
        var cosPitch = Math.cos(pitchRad);
        return new Vec3(-Math.sin(yawRad) * cosPitch, -Math.sin(pitchRad), Math.cos(yawRad) * cosPitch);
    }

    /** Screen-right unit vector — perpendicular to forward, in the world-horizontal plane. */
    private static Vec3 screenRight(float yaw) {
        var yawRad = Math.toRadians(yaw);
        return new Vec3(Math.cos(yawRad), 0, Math.sin(yawRad));
    }

    /** Screen-up unit vector — camera's local up axis (forward × screenRight). */
    private static Vec3 screenUp(float yaw, float pitch) {
        var yawRad = Math.toRadians(yaw);
        var pitchRad = Math.toRadians(pitch);
        var sinPitch = Math.sin(pitchRad);
        var cosPitch = Math.cos(pitchRad);
        return new Vec3(-Math.sin(yawRad) * sinPitch, cosPitch, Math.cos(yawRad) * sinPitch);
    }

    private static float clampPitch(float pitch) {
        return Math.max(-89.9f, Math.min(89.9f, pitch));
    }
}
