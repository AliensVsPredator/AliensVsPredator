package com.blib.engine.jigsaw;

import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import com.blib.engine.session.EngineCameraFrame;
import com.blib.engine.session.EngineInteractionRange;
import com.blib.engine.session.EngineNavigation;
import com.blib.engine.session.EngineSession;

/**
 * Helper that resolves the world {@link BlockPos} the user's cursor is pointing at while hovering the workspace
 * viewport. Lives outside {@link com.blib.engine.session.EngineNavigation} because placement isn't a navigation
 * concern, but reuses the same camera-ray math under the hood.
 * <p>
 * Two pieces of state are stitched together each frame: the {@link com.blib.engine.ui.panel.viewport.ViewportPanel}'s
 * rect (published via {@link #updateViewportRect}) and the global mouse position (via {@link Minecraft#mouseHandler}).
 * When both are known and the cursor is inside the viewport, a clip ray is cast from the engine camera to find the hit
 * block; the placement anchor is the block on the hit face (the standard "place on surface" position).
 */
@ApiStatus.Internal
public final class JigsawPlacementCursor {

    /**
     * Last-known viewport rect in raw window pixels (i.e. screen pixels, not workspace-logical pixels). Set by the
     * panel each frame; cleared when the screen closes.
     */
    private static int rectX;

    private static int rectY;

    private static int rectWidth;

    private static int rectHeight;

    private static boolean rectKnown;

    private JigsawPlacementCursor() {}

    /**
     * Publish the viewport's rect in raw window-pixel space so the world-render hook can map cursor coords back into
     * the same [0,1] viewport-relative coordinate system the navigation code uses.
     */
    public static void updateViewportRect(int x, int y, int width, int height) {
        rectX = x;
        rectY = y;
        rectWidth = width;
        rectHeight = height;
        rectKnown = width > 0 && height > 0;
    }

    public static void clearViewportRect() {
        rectKnown = false;
    }

    /**
     * Resolve where the cursor is currently pointing in the world. Returns {@code null} if the workspace isn't open,
     * the cursor is outside the viewport, the camera ray misses every block, or the engine session is gone. The
     * returned position is the anchor block — the empty cell the structure should occupy, on the hit face.
     */
    public static @Nullable BlockPos resolveAnchorBlock(EngineSession session) {
        var hit = clipFromCursor(session);
        if (hit == null) {
            return null;
        }
        // Anchor on the surface the user pointed at: targeted block + the face direction (e.g. clicking the top
        // face of a grass block anchors at y+1, matching how vanilla item placement chooses its target cell).
        return hit.getBlockPos().relative(hit.getDirection());
    }

    /**
     * Return the world-space direction of the cursor's ray through the viewport, or {@code null} if the cursor is
     * outside the viewport rect / the workspace isn't ready. Callers compose this with
     * {@link EngineSession#cameraPosition()} for the ray origin. Used by the capture-AABB gizmo for ray-vs-handle
     * picking and drag-plane intersection; differs from {@link #clipFromCursor} in that it returns the raw ray rather
     * than a clip result against world blocks (the gizmo handles are virtual, not blocks).
     */
    public static @Nullable Vec3 cursorRayDirection(EngineSession session) {
        var rel = cursorRelativeInViewport();
        if (rel == null) {
            return null;
        }
        // Prefer the matrices vanilla actually rendered with — they account for FOV modifiers (sprint, item-use,
        // zoom, fluid) and any extra projection transforms. Falls back to the analytical reconstruction when no
        // frame has been captured yet (very first render before the mixin runs).
        if (EngineCameraFrame.hasFrame()) {
            var dir = EngineCameraFrame.cursorRayDirection(rel[0], rel[1]);
            if (dir != null) {
                return dir;
            }
        }
        return EngineNavigation.cursorRayDirection(session, rel[0], rel[1]);
    }

    /**
     * World-space ray origin matching the camera position vanilla used to render the most recent frame. Falls back to
     * {@code session.cameraPosition()} when no frame has been captured yet. Used by the gizmo picker so the ray origin
     * matches the rendered camera (important during partial-tick interpolation, where rendered camera lags the
     * session's current position by up to half a tick).
     */
    public static Vec3 cursorRayOrigin(EngineSession session) {
        var captured = EngineCameraFrame.cameraPosition();
        return captured != null ? captured : session.cameraPosition();
    }

    /**
     * Run a clip raycast from the engine camera through the cursor's viewport position. Returns the raw
     * {@link BlockHitResult} so callers that care about the hit block itself (rather than the surface-adjacent
     * placement cell) can inspect it. Returns {@code null} on the same conditions as {@link #resolveAnchorBlock} —
     * cursor outside viewport, ray misses every block, etc.
     */
    public static @Nullable BlockHitResult clipFromCursor(EngineSession session) {
        var mc = Minecraft.getInstance();
        if (mc.level == null || mc.player == null) {
            return null;
        }
        var dir = cursorRayDirection(session);
        if (dir == null) {
            return null;
        }
        var origin = cursorRayOrigin(session);
        var end = origin.add(
            dir.x * EngineInteractionRange.MAX,
            dir.y * EngineInteractionRange.MAX,
            dir.z * EngineInteractionRange.MAX
        );

        var hit = mc.level.clip(new ClipContext(origin, end, ClipContext.Block.OUTLINE, ClipContext.Fluid.NONE, mc.player));
        if (hit.getType() == HitResult.Type.MISS) {
            return null;
        }
        return hit;
    }

    /**
     * Common helper: read the cursor's position from {@link Minecraft#mouseHandler} and convert it to viewport-
     * relative coords in {@code [0, 1]}. Returns {@code null} if the cursor is outside the viewport rect or the
     * workspace isn't ready.
     */
    private static double @Nullable [] cursorRelativeInViewport() {
        if (!rectKnown) {
            return null;
        }
        var mc = Minecraft.getInstance();
        if (mc.level == null || mc.player == null) {
            return null;
        }
        var window = mc.getWindow();
        if (window.getScreenWidth() <= 0 || window.getScreenHeight() <= 0) {
            return null;
        }
        // MouseHandler reports cursor coords in raw screen-pixel units (the same units rectX/rectY use), so they
        // can be compared directly without any GUI-scale conversion.
        var cursorX = mc.mouseHandler.xpos();
        var cursorY = mc.mouseHandler.ypos();
        if (cursorX < rectX || cursorX >= rectX + rectWidth || cursorY < rectY || cursorY >= rectY + rectHeight) {
            return null;
        }
        return new double[] { (cursorX - rectX) / (double) rectWidth, (cursorY - rectY) / (double) rectHeight };
    }
}
