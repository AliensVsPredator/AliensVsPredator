package com.blib.engine.jigsaw;

import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import com.blib.engine.session.EngineNavigation;
import com.blib.engine.session.EngineSession;

/**
 * Helper that resolves the world {@link BlockPos} the user's cursor is pointing at while hovering the workspace
 * viewport. Lives outside {@link com.blib.engine.session.EngineNavigation} because placement isn't a navigation
 * concern, but reuses the same camera-ray math under the hood.
 * <p>
 * Two pieces of state are stitched together each frame: the {@link com.blib.engine.ui.ViewportPanel}'s rect (published
 * via {@link #updateViewportRect}) and the global mouse position (via {@link Minecraft#mouseHandler}). When both are
 * known and the cursor is inside the viewport, a clip ray is cast from the engine camera to find the hit block; the
 * placement anchor is the block on the hit face (the standard "place on surface" position).
 */
@ApiStatus.Internal
public final class JigsawPlacementCursor {

    private static final double PLACEMENT_RAYCAST_DISTANCE = 96.0;

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
     * Run a clip raycast from the engine camera through the cursor's viewport position. Returns the raw
     * {@link BlockHitResult} so callers that care about the hit block itself (rather than the surface-adjacent
     * placement cell) can inspect it. Returns {@code null} on the same conditions as {@link #resolveAnchorBlock} —
     * cursor outside viewport, ray misses every block, etc.
     */
    public static @Nullable BlockHitResult clipFromCursor(EngineSession session) {
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

        var relX = (cursorX - rectX) / (double) rectWidth;
        var relY = (cursorY - rectY) / (double) rectHeight;

        var origin = session.cameraPosition();
        var dir = EngineNavigation.cursorRayDirection(session, relX, relY);
        var end = origin.add(
            dir.x * PLACEMENT_RAYCAST_DISTANCE,
            dir.y * PLACEMENT_RAYCAST_DISTANCE,
            dir.z * PLACEMENT_RAYCAST_DISTANCE
        );

        var hit = mc.level.clip(new ClipContext(origin, end, ClipContext.Block.OUTLINE, ClipContext.Fluid.NONE, mc.player));
        if (hit.getType() == HitResult.Type.MISS) {
            return null;
        }
        return hit;
    }
}
