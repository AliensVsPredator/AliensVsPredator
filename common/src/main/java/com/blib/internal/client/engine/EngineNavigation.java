package com.blib.internal.client.engine;

import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.ApiStatus;

/**
 * Per-tick camera-control integration for engine mode. Routes input to either {@link NavigationMode#FLY}
 * (spectator-style WASD + mouse-look) or {@link NavigationMode#ORBIT} (DCC-style LMB-orbit, RMB-pan, scroll-zoom)
 * depending on the current session mode.
 * <p>
 * Mouse delta and scroll delta are accumulated by the input mixins; this class reads keyboard via
 * {@code Options.keyXxx.isDown()} and mouse-button state via {@code MouseHandler.isLeftPressed()} / etc.
 */
@ApiStatus.Internal
public final class EngineNavigation {

    private static final double FLY_BASE_SPEED = 0.25;

    private static final double FLY_SPRINT_MULTIPLIER = 4.0;

    private static final double FLY_SNEAK_DIVISOR = 4.0;

    /** Degrees of view rotation per pixel of mouse delta. Matches vanilla {@code MouseHandler} sensitivity scale. */
    private static final float MOUSE_LOOK_SENSITIVITY = 0.15f;

    private static final float ORBIT_SENSITIVITY = 0.4f;

    /** World-blocks of pan per pixel of mouse delta, scaled by camera distance from pivot. */
    private static final double PAN_SENSITIVITY = 0.0025;

    /** Fraction of pivot-distance to step per scroll click (positive scroll = closer to pivot). */
    private static final double ZOOM_FRACTION_PER_CLICK = 0.15;

    /** Hard floor on camera-pivot distance, so wheel-zoom can't pass through the pivot. */
    private static final double MIN_PIVOT_DISTANCE = 0.5;

    /** Fallback distance for orbit pivot when the screen-center raycast hits nothing. */
    private static final double DEFAULT_PIVOT_DISTANCE = 8.0;

    /** Max raycast distance when computing the orbit pivot from the screen center. */
    private static final double PIVOT_RAYCAST_DISTANCE = 64.0;

    /** Max raycast distance for click-to-select against entities. */
    private static final double SELECTION_RAYCAST_DISTANCE = 96.0;

    /**
     * Mouse-pixel distance threshold separating "click" (under) from "drag" (over). 5 px is generous enough that
     * unintentional micro-movements during a click don't trigger orbit, tight enough that intentional drags feel
     * responsive.
     */
    private static final double CLICK_VS_DRAG_THRESHOLD_PX = 5.0;

    private EngineNavigation() {}

    public static void tick() {
        var session = EngineMode.get().session();

        if (session == null) {
            return;
        }

        // Snapshot prev-tick pose first so the render-time interpolation lerps from the start-of-tick state to the
        // end-of-tick state. Without this the camera would teleport per tick — visibly choppy at high frame rates.
        session.snapshotForTick();

        syncMouseGrabState(session);

        switch (session.mode()) {
            case FLY -> tickFly(session);
            case ORBIT -> tickOrbit(session);
        }
    }

    /**
     * Auto-corrects the OS mouse grab state to match the current navigation mode whenever no UI screen is open: fly
     * wants the mouse grabbed (cursor hidden, deltas via turnPlayer), orbit wants it released (cursor visible). Runs
     * each tick so re-opening from chat or any other screen lands in the right state without dedicated transition
     * hooks.
     */
    private static void syncMouseGrabState(EngineSession session) {
        var mc = Minecraft.getInstance();

        if (mc.screen != null) {
            return;
        }

        var grabbed = mc.mouseHandler.isMouseGrabbed();
        var wantGrabbed = session.mode() == NavigationMode.FLY;

        if (grabbed && !wantGrabbed) {
            mc.mouseHandler.releaseMouse();
        } else if (!grabbed && wantGrabbed) {
            mc.mouseHandler.grabMouse();
        }
    }

    // -- Fly mode -----------------------------------------------------------------------------------------------------

    private static void tickFly(EngineSession session) {
        applyMouseLook(session);
        applyKeyboardMovement(session);
    }

    private static void applyMouseLook(EngineSession session) {
        var dx = session.consumeMouseDx();
        var dy = session.consumeMouseDy();

        if (dx == 0 && dy == 0) {
            return;
        }

        session.rotate((float) (dx * MOUSE_LOOK_SENSITIVITY), (float) (dy * MOUSE_LOOK_SENSITIVITY));
    }

    private static void applyKeyboardMovement(EngineSession session) {
        var options = Minecraft.getInstance().options;

        var forward = downAxis(options.keyUp, options.keyDown);
        // Match MC's `leftImpulse` convention: A=+1, D=-1.
        var leftImpulse = downAxis(options.keyLeft, options.keyRight);
        var vertical = downAxis(options.keyJump, options.keyShift);

        if (forward == 0 && leftImpulse == 0 && vertical == 0) {
            return;
        }

        var speed = FLY_BASE_SPEED;

        if (options.keySprint.isDown()) {
            speed *= FLY_SPRINT_MULTIPLIER;
        }

        if (options.keySprint.isDown() && options.keyShift.isDown()) {
            speed /= FLY_SNEAK_DIVISOR;
        }

        // Mirror MC's `LivingEntity.getInputVector`:
        // world.x = leftImpulse * cos(yaw) - forwardImpulse * sin(yaw)
        // world.z = forwardImpulse * cos(yaw) + leftImpulse * sin(yaw)
        var yawRad = Math.toRadians(session.yaw());
        var sinYaw = Math.sin(yawRad);
        var cosYaw = Math.cos(yawRad);

        var dx = (leftImpulse * cosYaw - forward * sinYaw) * speed;
        var dz = (forward * cosYaw + leftImpulse * sinYaw) * speed;
        var dy = vertical * speed;

        session.translate(dx, dy, dz);
    }

    // -- Orbit mode ---------------------------------------------------------------------------------------------------

    private static void tickOrbit(EngineSession session) {
        var mc = Minecraft.getInstance();
        var leftDown = mc.mouseHandler.isLeftPressed();
        var rightDown = mc.mouseHandler.isRightPressed();

        handleLeftButton(session, leftDown);

        if (rightDown) {
            applyPan(session);
        } else if (!leftDown) {
            // Drain unused mouse delta so it doesn't accumulate between drags. (When LMB is held, the delta is
            // consumed in handleLeftButton.)
            session.consumeMouseDx();
            session.consumeMouseDy();
        }

        applyZoom(session);

        session.setPrevLeftDown(leftDown);
        session.setPrevRightDown(rightDown);
    }

    /**
     * Press-time selection with drag-to-orbit. Industry-standard editor mapping: LMB press fires selection immediately
     * (snappy feedback, no release-edge ambiguity from hand drift); a held-and-dragged LMB transitions to orbit once
     * the cursor moves past {@link #CLICK_VS_DRAG_THRESHOLD_PX}. The drag-time selection still applies to whatever was
     * under the cursor at press, which matches Unity/Unreal: clicking-then-dragging selects the thing under the cursor
     * and then orbits.
     */
    private static void handleLeftButton(EngineSession session, boolean leftDown) {
        var pressed = session.consumeLmbPressEvent();
        var released = session.consumeLmbReleaseEvent();

        if (pressed) {
            performSelection(session);
            session.resetLmbDragDistance();
            session.setOrbitDragActive(false);
        }

        if (released) {
            session.setOrbitDragActive(false);
            session.resetLmbDragDistance();
            // Drain any leftover delta so the next press starts clean.
            session.consumeMouseDx();
            session.consumeMouseDy();
            return;
        }

        if (!leftDown) {
            return;
        }

        var dx = session.consumeMouseDx();
        var dy = session.consumeMouseDy();

        if (session.orbitDragActive()) {
            applyOrbitFromDelta(session, dx, dy);
            return;
        }

        // Held but not yet a drag: accumulate distance and check the threshold.
        session.addLmbDragDistance(Math.sqrt(dx * dx + dy * dy));

        if (session.lmbDragDistance() > CLICK_VS_DRAG_THRESHOLD_PX) {
            session.setOrbitDragActive(true);
            session.setPivot(computePivot(session));
        }
    }

    private static void applyOrbitFromDelta(EngineSession session, double dx, double dy) {
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

        // Forward direction at the new orientation; camera sits behind the pivot along that direction.
        var forward = forwardVector(newYaw, newPitch);
        session.setPosition(
            pivot.x - forward.x * distance,
            pivot.y - forward.y * distance,
            pivot.z - forward.z * distance
        );
    }

    /**
     * Raycasts a ray through the cursor's screen position against living entities up to
     * {@link #SELECTION_RAYCAST_DISTANCE} blocks; sets the session's selected entity to the first hit (or clears
     * selection on a miss).
     */
    private static void performSelection(EngineSession session) {
        var mc = Minecraft.getInstance();

        if (mc.level == null || mc.player == null) {
            return;
        }

        var origin = session.cameraPosition();
        var rayDir = cursorRayDirection(session);
        var end = origin.add(
            rayDir.x * SELECTION_RAYCAST_DISTANCE,
            rayDir.y * SELECTION_RAYCAST_DISTANCE,
            rayDir.z * SELECTION_RAYCAST_DISTANCE
        );

        // The search AABB has to span the full ray path from camera origin to endpoint. In normal gameplay
        // anchoring at the player works because the camera sits at the player's eye, but in engine mode the camera
        // is detached and the player's body is frozen on the ground — anchoring at the player would search the
        // wrong region of space and miss every entity along the cursor ray.
        var aabb = new AABB(origin, end).inflate(1.0);

        var hit = ProjectileUtil.getEntityHitResult(
            mc.player,
            origin,
            end,
            aabb,
            entity -> !entity.isSpectator()
                && entity != mc.player
                && entity instanceof LivingEntity,
            SELECTION_RAYCAST_DISTANCE * SELECTION_RAYCAST_DISTANCE
        );

        if (hit != null && hit.getEntity() instanceof LivingEntity living) {
            session.setSelectedEntity(living);
        } else {
            session.setSelectedEntity(null);
        }
    }

    /**
     * Unprojects the cursor's pixel position into a world-space ray direction. Built in camera-local space using FOV +
     * aspect, then expressed in world coords via the camera's basis vectors. Used for click-to-select; orbit pivot
     * still uses screen center.
     */
    private static Vec3 cursorRayDirection(EngineSession session) {
        var mc = Minecraft.getInstance();
        var window = mc.getWindow();
        var width = window.getScreenWidth();
        var height = window.getScreenHeight();

        if (width <= 0 || height <= 0) {
            return forwardVector(session.yaw(), session.pitch());
        }

        var cursorX = mc.mouseHandler.xpos();
        var cursorY = mc.mouseHandler.ypos();

        // NDC: -1..+1, +1 = right edge / top edge. Y flip because screen-Y increases downward.
        var ndcX = (2.0 * cursorX / width) - 1.0;
        var ndcY = 1.0 - (2.0 * cursorY / height);

        int fovDeg = mc.options.fov().get();
        var halfHeight = Math.tan(Math.toRadians(fovDeg) / 2.0);
        var halfWidth = halfHeight * (double) width / height;

        var forward = forwardVector(session.yaw(), session.pitch());
        var up = screenUp(session.yaw(), session.pitch());
        var right = screenRight(session.yaw());

        // The ndcX term is subtracted because `screenRight` is the world direction the *camera* moves to make the
        // world appear to scroll right (drag-the-world pan), which is the OPPOSITE of the world direction visible at
        // the right side of the screen. At yaw=0 that's world-east; the camera rotation flips it so the right side
        // of the screen actually shows world-west. So a cursor with ndcX > 0 (right of screen) needs a ray with
        // -screenRight world component.
        var dx = forward.x + ndcY * halfHeight * up.x - ndcX * halfWidth * right.x;
        var dy = forward.y + ndcY * halfHeight * up.y - ndcX * halfWidth * right.y;
        var dz = forward.z + ndcY * halfHeight * up.z - ndcX * halfWidth * right.z;

        var raw = new Vec3(dx, dy, dz);
        return raw.lengthSqr() < 1.0E-10 ? forward : raw.normalize();
    }

    private static void applyPan(EngineSession session) {
        var dx = session.consumeMouseDx();
        var dy = session.consumeMouseDy();

        if (dx == 0 && dy == 0) {
            return;
        }

        // Pan speed scales with pivot distance so panning at far view feels natural.
        var distance = session.cameraPosition().subtract(session.pivot()).length();
        var scale = PAN_SENSITIVITY * Math.max(distance, 1.0);

        // Move the camera + pivot strictly in the camera's screen plane (the plane perpendicular to its view
        // direction), spanned by `screenRight` and `screenUp`. Drag-the-world convention (Blender / Blockbench): the
        // world translates in the same direction the mouse moves, so the camera moves opposite the mouse on both
        // axes. Both terms are subtracted from the camera position.
        var right = screenRight(session.yaw());
        var up = screenUp(session.yaw(), session.pitch());

        var deltaX = -right.x * dx * scale - up.x * dy * scale;
        var deltaY = -right.y * dx * scale - up.y * dy * scale;
        var deltaZ = -right.z * dx * scale - up.z * dy * scale;

        session.translate(deltaX, deltaY, deltaZ);
        session.setPivot(session.pivot().add(deltaX, deltaY, deltaZ));
    }

    private static void applyZoom(EngineSession session) {
        var scroll = session.consumeScrollDy();

        if (scroll == 0) {
            return;
        }

        var pivot = session.pivot();
        var offset = session.cameraPosition().subtract(pivot);
        var distance = offset.length();

        if (distance < 1.0E-4) {
            return;
        }

        // Multiplicative step: each click changes distance by ZOOM_FRACTION (positive scroll = scroll up = zoom in).
        var factor = Math.pow(1.0 - ZOOM_FRACTION_PER_CLICK, scroll);
        var newDistance = Math.max(MIN_PIVOT_DISTANCE, distance * factor);
        var dir = offset.scale(1.0 / distance);

        session.setPosition(
            pivot.x + dir.x * newDistance,
            pivot.y + dir.y * newDistance,
            pivot.z + dir.z * newDistance
        );
    }

    // -- Helpers ------------------------------------------------------------------------------------------------------

    private static Vec3 computePivot(EngineSession session) {
        var mc = Minecraft.getInstance();

        if (mc.level == null) {
            return defaultPivot(session);
        }

        var origin = session.cameraPosition();
        var forward = forwardVector(session.yaw(), session.pitch());
        var end = origin.add(
            forward.x * PIVOT_RAYCAST_DISTANCE,
            forward.y * PIVOT_RAYCAST_DISTANCE,
            forward.z * PIVOT_RAYCAST_DISTANCE
        );

        var hit = mc.level.clip(
            new ClipContext(origin, end, ClipContext.Block.VISUAL, ClipContext.Fluid.NONE, mc.player)
        );

        if (hit.getType() == HitResult.Type.MISS) {
            return defaultPivot(session);
        }

        return hit.getLocation();
    }

    private static Vec3 defaultPivot(EngineSession session) {
        var origin = session.cameraPosition();
        var forward = forwardVector(session.yaw(), session.pitch());
        return origin.add(
            forward.x * DEFAULT_PIVOT_DISTANCE,
            forward.y * DEFAULT_PIVOT_DISTANCE,
            forward.z * DEFAULT_PIVOT_DISTANCE
        );
    }

    /** Forward unit vector for MC yaw/pitch, mirroring {@code Entity.calculateViewVector}. */
    private static Vec3 forwardVector(float yaw, float pitch) {
        var yawRad = Math.toRadians(yaw);
        var pitchRad = Math.toRadians(pitch);
        var cosPitch = Math.cos(pitchRad);
        return new Vec3(-Math.sin(yawRad) * cosPitch, -Math.sin(pitchRad), Math.cos(yawRad) * cosPitch);
    }

    /**
     * Screen-right unit vector: perpendicular to forward, lying in the world-horizontal plane (no Y component). At
     * yaw=0 (south-facing) this is (1, 0, 0) = east, which is the player's right side.
     */
    private static Vec3 screenRight(float yaw) {
        var yawRad = Math.toRadians(yaw);
        return new Vec3(Math.cos(yawRad), 0, Math.sin(yawRad));
    }

    /**
     * Screen-up unit vector: the camera's local up axis, perpendicular to both forward and screenRight. Computed as
     * {@code forward × screenRight} so signs come out right for screen-plane panning. At pitch=0 this is world up (0,
     * 1, 0); as pitch tilts down, screen-up rotates toward the previous-forward direction (and away from world up).
     */
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

    private static double downAxis(KeyMapping positive, KeyMapping negative) {
        var p = positive.isDown() ? 1 : 0;
        var n = negative.isDown() ? 1 : 0;
        return p - n;
    }
}
