package com.blib.engine.session;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.ApiStatus;

import java.util.ArrayList;

import com.blib.engine.domain.selection.picking.BlockSelectable;
import com.blib.engine.domain.selection.picking.EntitySelectable;
import com.blib.engine.domain.selection.picking.PlacedJigsawPieceSelectable;
import com.blib.engine.domain.selection.picking.Selectable;
import com.blib.engine.domain.selection.picking.Selection;
import com.blib.engine.domain.selection.picking.SelectionManager;
import com.blib.engine.domain.selection.volume.BlockSelection;
import com.blib.engine.jigsaw.ClientPlacedPieceRegistry;
import com.blib.engine.jigsaw.JigsawPlacementCursor;

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
    }

    public static void endOrbitDrag(EngineSession session) {
        // No-op now that orbit-drag-active state is gone (no callers ever read it). Kept as a named entry point so the
        // viewport's gesture-end callsites remain symmetric with {@link #beginOrbitDrag}.
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
     * generic block hit (via {@link JigsawPlacementCursor#clipFromCursor}); whichever is closer to the camera wins.
     * Every block — jigsaw or otherwise — becomes a {@link BlockSelectable}; the inspector decides whether to render
     * jigsaw-specific extras based on the live {@code BlockState}. On miss, clears any existing selection.
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
            rayDir.x * EngineInteractionRange.MAX,
            rayDir.y * EngineInteractionRange.MAX,
            rayDir.z * EngineInteractionRange.MAX
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
            EngineInteractionRange.MAX_SQR
        );

        // Generic block raycast — same camera-cursor ray geometry the FREE / SNAP resolvers use, so the inspector's
        // pick matches the placement preview's anchor candidate. Returns the raw BlockHitResult (any block type), not
        // just jigsaws — the post-check below dispatches jigsaw vs. generic.
        var blockHit = JigsawPlacementCursor.clipFromCursor(session);

        // Placed-piece raycast — gated on the Ctrl modifier so the user can drill through a piece to pick the block
        // inside it. Mirrors EngineHoverProbe; Ctrl (not Alt) because the Linux window manager already owns Alt+drag.
        var ctrlHeld = Screen.hasControlDown();
        var pieceHit = ctrlHeld ? null : ClientPlacedPieceRegistry.raycast(origin, rayDir, EngineInteractionRange.MAX);

        var entityDistSq = (entityHit != null && entityHit.getEntity() instanceof LivingEntity)
            ? entityHit.getLocation().distanceToSqr(origin)
            : Double.POSITIVE_INFINITY;
        // Block-distance reference is the cube center — close enough to the bbox-hit reference used for entities for
        // the "which is closer" heuristic to feel right; both are within ~0.5 blocks of the actual surface hit.
        var blockDistSq = blockHit != null
            ? Vec3.atCenterOf(blockHit.getBlockPos()).distanceToSqr(origin)
            : Double.POSITIVE_INFINITY;

        if (entityDistSq == Double.POSITIVE_INFINITY && blockDistSq == Double.POSITIVE_INFINITY && pieceHit == null) {
            // Sky miss: drop the active selection but leave any staged volume corners in place — the user might want
            // to defocus the inspector without abandoning the volume they marqueed earlier.
            SelectionManager.clear();
            return;
        }

        // Picking a single-thing target replaces any staged block volume — the volume's wireframe, gizmos, and RMB
        // context menu must not coexist with a single-block / entity / jigsaw / piece inspection (the selection modes
        // are mutually exclusive from the user's POV). User preferences (gizmo mode, capture mode) survive so a
        // follow-up re-marquee picks up where they left off.
        BlockSelection.clearVolume();

        if (entityDistSq <= blockDistSq) {
            SelectionManager.selectSingle(new EntitySelectable((LivingEntity) entityHit.getEntity()));
        } else if (pieceHit != null) {
            // Piece beats any block (jigsaw or generic) when the user is hovering inside the piece's AABB. Alt was
            // the fallthrough that skipped the piece raycast above; here we know pieceHit is real and the click
            // wasn't an Alt-drill. Jigsaw blocks aren't given priority over pieces — they're just blocks with a
            // richer inspector view.
            SelectionManager.selectSingle(new PlacedJigsawPieceSelectable(pieceHit.pieceId()));
        } else {
            SelectionManager.selectSingle(new BlockSelectable(blockHit.getBlockPos()));
        }
    }

    public static void selectVisibleEntitiesOfSameType(EngineSession session, LivingEntity seed) {
        var mc = Minecraft.getInstance();
        if (mc.level == null || mc.player == null || !seed.isAlive()) {
            return;
        }

        var origin = EngineCameraFrame.cameraPosition();
        if (origin == null) {
            origin = session.cameraPosition();
        }

        var seedType = seed.getType();
        var selected = new ArrayList<Selectable>();
        for (var candidate : mc.level.entitiesForRendering()) {
            if (!(candidate instanceof LivingEntity living)) {
                continue;
            }
            if (living == mc.player || living.isSpectator() || !living.isAlive() || living.getType() != seedType) {
                continue;
            }
            if (living != seed && !isCameraVisible(mc, origin, living)) {
                continue;
            }
            selected.add(new EntitySelectable(living));
        }

        if (selected.isEmpty()) {
            selected.add(new EntitySelectable(seed));
        }
        BlockSelection.clearVolume();
        SelectionManager.replace(new Selection(selected));
    }

    private static boolean isCameraVisible(Minecraft mc, Vec3 origin, LivingEntity entity) {
        if (!EngineCameraFrame.isAabbInView(entity.getBoundingBox())) {
            return false;
        }
        if (mc.player != null && entity.isInvisibleTo(mc.player)) {
            return false;
        }
        return hasCameraLineOfSight(mc, origin, entity);
    }

    private static boolean hasCameraLineOfSight(Minecraft mc, Vec3 origin, LivingEntity entity) {
        var box = entity.getBoundingBox();
        var centerX = (box.minX + box.maxX) * 0.5;
        var centerY = (box.minY + box.maxY) * 0.5;
        var centerZ = (box.minZ + box.maxZ) * 0.5;
        var height = Math.max(0.0, box.maxY - box.minY);
        var insetY = Math.min(0.2, height * 0.25);
        var lowerY = Math.min(box.maxY, box.minY + insetY);
        var upperY = Math.max(box.minY, box.maxY - insetY);

        return hasUnblockedCameraRay(mc, origin, entity.getEyePosition())
            || hasUnblockedCameraRay(mc, origin, new Vec3(centerX, centerY, centerZ))
            || hasUnblockedCameraRay(mc, origin, new Vec3(centerX, lowerY, centerZ))
            || hasUnblockedCameraRay(mc, origin, new Vec3(centerX, upperY, centerZ))
            || hasUnblockedCameraRay(mc, origin, new Vec3(box.minX, centerY, centerZ))
            || hasUnblockedCameraRay(mc, origin, new Vec3(box.maxX, centerY, centerZ))
            || hasUnblockedCameraRay(mc, origin, new Vec3(centerX, centerY, box.minZ))
            || hasUnblockedCameraRay(mc, origin, new Vec3(centerX, centerY, box.maxZ));
    }

    private static boolean hasUnblockedCameraRay(Minecraft mc, Vec3 origin, Vec3 target) {
        if (mc.level == null || mc.player == null) {
            return true;
        }

        var targetDistSqr = target.distanceToSqr(origin);
        if (targetDistSqr < 1.0E-8) {
            return true;
        }

        var hit = mc.level.clip(new ClipContext(origin, target, ClipContext.Block.VISUAL, ClipContext.Fluid.NONE, mc.player));
        if (hit.getType() == HitResult.Type.MISS) {
            return true;
        }

        return hit.getLocation().distanceToSqr(origin) >= targetDistSqr - 0.04;
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
            rayDir.x * EngineInteractionRange.MAX,
            rayDir.y * EngineInteractionRange.MAX,
            rayDir.z * EngineInteractionRange.MAX
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
        return EngineCameraBasis.forward(yaw, pitch);
    }

    /** Screen-right unit vector — perpendicular to forward, in the world-horizontal plane. */
    private static Vec3 screenRight(float yaw) {
        return EngineCameraBasis.screenRight(yaw);
    }

    /** Screen-up unit vector — camera's local up axis. */
    private static Vec3 screenUp(float yaw, float pitch) {
        return EngineCameraBasis.screenUp(yaw, pitch);
    }

    private static float clampPitch(float pitch) {
        return Math.max(-89.9f, Math.min(89.9f, pitch));
    }
}
