package com.blib.engine.ui;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import com.blib.engine.modeler.ModelerCamera;
import com.blib.engine.session.EngineCameraBasis;

/**
 * Bottom-right viewport overlay that shows the world axes oriented to the modeler camera — the Blender "navigation
 * gizmo" affordance. Three colored balls (X red, Y green, Z blue) labeled with the axis letter mark the positive
 * direction; three smaller dimmer balls behind them mark the negative direction so the user always sees both halves of
 * each axis. Balls are depth-sorted so the closer endpoint draws on top of the farther one.
 * <p>
 * Click-to-snap: clicking a ball snaps the camera's yaw / pitch to look down that axis (positive ball → camera on the
 * positive side looking back toward the origin, negative ball → camera on the negative side). Pitch is clamped to the
 * camera's existing ±89° range so Y-axis snaps land just under the pole rather than triggering gimbal lock.
 * <p>
 * Stateless utility — the host panel calls {@link #render} every frame, then {@link #hitTest} on clicks. No drag-to-
 * orbit yet; the user can fall back to MMB drag on the viewport for free-form rotation.
 */
@ApiStatus.Internal
public final class ModelerAxisGizmo {

    /** Distance (in panel-logical px) from each panel edge to the widget's bounding box. */
    private static final int INSET = 8;

    /** Axis-line length from the gizmo center to a positive-axis ball center, in panel-logical px. */
    private static final int RADIUS = 18;

    /** Half-size of the colored ball that marks each axis endpoint. */
    private static final int BALL_HALF = 3;

    /** Half-size of the dimmer ball that marks each negative axis. Smaller so positive-axis balls read as primary. */
    private static final int NEG_BALL_HALF = 2;

    /** Hit-test radius (panel-logical px) around each axis endpoint — generous enough for finger / touch input. */
    private static final int HIT_RADIUS = 6;

    /**
     * Render-scale applied to axis-letter labels. Below 1.0 so the X/Y/Z glyphs don't overpower the small balls they
     * sit on. Picked low enough to read as "compact label" while staying legible.
     */
    private static final float LABEL_SCALE = 0.8f;

    private static final int LINE_COLOR = 0xFF1A1A1A;

    private static final int LABEL_COLOR = 0xFF000000;

    /** Positive-axis fill colors. Match the modeler gizmo / Blender convention (X=red, Y=green, Z=blue). */
    private static final int COLOR_X = 0xFFFF3333;

    private static final int COLOR_Y = 0xFF33CC33;

    private static final int COLOR_Z = 0xFF3366FF;

    /** Negative-axis fills — darker tints of the corresponding positive color so depth + sign read together. */
    private static final int COLOR_NEG_X = 0xFF7A1A1A;

    private static final int COLOR_NEG_Y = 0xFF1A6A1A;

    private static final int COLOR_NEG_Z = 0xFF1A1F7A;

    private ModelerAxisGizmo() {}

    /**
     * Identifies one of the six axis endpoints the gizmo presents. Returned by {@link #hitTest} and consumed by
     * {@link #snapCamera} to apply the matching yaw/pitch.
     */
    public enum AxisHit {
        POS_X,
        NEG_X,
        POS_Y,
        NEG_Y,
        POS_Z,
        NEG_Z
    }

    public static void render(GuiGraphics graphics, int panelX, int panelY, int panelWidth, int panelHeight, ModelerCamera camera) {
        var centerX = panelX + panelWidth - INSET - RADIUS;
        var centerY = panelY + panelHeight - INSET - RADIUS;

        // Build the six endpoint records up front so depth sort + draw can iterate one list. Each Endpoint carries
        // its screen-space position, depth (negative = in front of camera), color, and optional label glyph.
        var endpoints = new Endpoint[] {
            endpointFor(AxisHit.POS_X, camera, centerX, centerY),
            endpointFor(AxisHit.NEG_X, camera, centerX, centerY),
            endpointFor(AxisHit.POS_Y, camera, centerX, centerY),
            endpointFor(AxisHit.NEG_Y, camera, centerX, centerY),
            endpointFor(AxisHit.POS_Z, camera, centerX, centerY),
            endpointFor(AxisHit.NEG_Z, camera, centerX, centerY)
        };

        // Sort back-to-front (largest depth first). With our depth = dot(axis, forward), a value > 0 means the axis
        // points away from the camera (behind the gizmo plane). Drawing those first lets the in-front endpoints
        // occlude them when ball rects overlap.
        java.util.Arrays.sort(endpoints, (a, b) -> Float.compare(b.depth, a.depth));

        // Lines come first as a separate pass so balls always draw on top of axis lines (including lines from other
        // axes that happen to overlap). Only positive axes get lines — negative endpoints are minor depth indicators.
        for (var ep : endpoints) {
            if (ep.primary) {
                drawLine(graphics, centerX, centerY, ep.screenX, ep.screenY, LINE_COLOR);
            }
        }

        // Balls + labels in depth order so the front endpoints occlude the back ones.
        for (var ep : endpoints) {
            var half = ep.primary ? BALL_HALF : NEG_BALL_HALF;
            graphics.fill(ep.screenX - half, ep.screenY - half, ep.screenX + half + 1, ep.screenY + half + 1, ep.color);
            if (ep.label != null) {
                drawScaledLabel(graphics, ep.label, ep.screenX, ep.screenY);
            }
        }
    }

    /**
     * Render the axis letter at {@link #LABEL_SCALE} times its native font size, centered on the ball. Done via pose
     * transform so the underlying {@code drawString} doesn't have to know about scaling — translate the matrix to the
     * ball, scale around that point, then draw at negative-half-extent coordinates to land centered.
     * <p>
     * The X offset uses a half-pixel float translation in post-scale space rather than integer drawString offsets: MC's
     * font advances include a 1-pixel trailing space (visible glyph is left-of-cell), so the bare {@code -lw/2} integer
     * offset puts the visible glyph slightly left of the ball center. Splitting the centering between an integer
     * drawString offset and a sub-pixel pose nudge gets the glyph optically centered without snapping to the wrong side
     * at a non-integer scale.
     */
    private static void drawScaledLabel(GuiGraphics graphics, String label, int centerX, int centerY) {
        var font = EngineFont.get();
        var labelComponent = Component.literal(label);
        var lw = font.width(labelComponent);
        var lh = font.lineHeight;
        var pose = graphics.pose();
        pose.pushPose();
        // Post-scale 0.5-px X nudge cancels the bitmap-font trailing-space asymmetry; without it the letter visibly
        // sits to the left of the ball's pixel center at LABEL_SCALE < 1.0.
        pose.translate(centerX + 0.5f, centerY, 0);
        pose.scale(LABEL_SCALE, LABEL_SCALE, 1.0f);
        graphics.drawString(font, labelComponent, -lw / 2, -lh / 2 + 1, LABEL_COLOR, false);
        pose.popPose();
    }

    /**
     * Returns the axis endpoint under {@code (mouseX, mouseY)}, or null if the cursor isn't over any ball. Picks the
     * closest endpoint within {@link #HIT_RADIUS}; ties broken by depth (closer to camera wins) so the visually
     * front-most ball is the one that grabs the click when two overlap.
     */
    public static @Nullable AxisHit hitTest(
        double mouseX,
        double mouseY,
        int panelX,
        int panelY,
        int panelWidth,
        int panelHeight,
        ModelerCamera camera
    ) {
        var centerX = panelX + panelWidth - INSET - RADIUS;
        var centerY = panelY + panelHeight - INSET - RADIUS;
        AxisHit best = null;
        float bestDepth = Float.POSITIVE_INFINITY;
        for (var which : AxisHit.values()) {
            var ep = endpointFor(which, camera, centerX, centerY);
            var dx = mouseX - ep.screenX;
            var dy = mouseY - ep.screenY;
            if (dx * dx + dy * dy <= HIT_RADIUS * HIT_RADIUS) {
                if (ep.depth < bestDepth) {
                    bestDepth = ep.depth;
                    best = which;
                }
            }
        }
        return best;
    }

    /**
     * Hover tooltip text for an axis ball. The +/- signs match what the user sees on the widget rather than the
     * underlying enum: the X-axis is rendered flipped (labeled "X" ball is at the modeler's {@code NEG_X} due to the
     * Bedrock X-flip convention), so its tooltip naming follows the visual rather than the field name.
     */
    public static Component tooltipFor(AxisHit axis) {
        var label = switch (axis) {
            case NEG_X -> "X";
            case POS_X -> "-X";
            case POS_Y -> "Y";
            case NEG_Y -> "-Y";
            case POS_Z -> "Z";
            case NEG_Z -> "-Z";
        };
        return Component.literal("View along " + label + " — click to snap the camera to this axis");
    }

    public static void snapCamera(ModelerCamera camera, AxisHit axis) {
        switch (axis) {
            case POS_X -> {
                camera.yaw = 90f;
                camera.pitch = 0f;
            }
            case NEG_X -> {
                camera.yaw = 270f;
                camera.pitch = 0f;
            }
            case POS_Y -> {
                camera.yaw = 0f;
                camera.pitch = 90f;
            }
            case NEG_Y -> {
                camera.yaw = 0f;
                camera.pitch = -90f;
            }
            case POS_Z -> {
                camera.yaw = 180f;
                camera.pitch = 0f;
            }
            case NEG_Z -> {
                camera.yaw = 0f;
                camera.pitch = 0f;
            }
        }
        camera.clampPitch();
    }

    /**
     * Project an axis's world direction to widget-local screen coords. {@code screenRight} / {@code screenUp} give the
     * camera's basis vectors; the dot product with each world axis tells us how that axis lays out on the 2D widget
     * plane. {@code forward} dotted gives depth (positive = behind the gizmo plane).
     */
    private static Endpoint endpointFor(AxisHit which, ModelerCamera camera, int centerX, int centerY) {
        var forward = EngineCameraBasis.forward(camera.yaw, camera.pitch);
        var screenRight = EngineCameraBasis.screenRight(camera.yaw);
        var screenUp = EngineCameraBasis.screenUp(camera.yaw, camera.pitch);

        double ax, ay, az;
        switch (which) {
            case POS_X -> {
                ax = 1;
                ay = 0;
                az = 0;
            }
            case NEG_X -> {
                ax = -1;
                ay = 0;
                az = 0;
            }
            case POS_Y -> {
                ax = 0;
                ay = 1;
                az = 0;
            }
            case NEG_Y -> {
                ax = 0;
                ay = -1;
                az = 0;
            }
            case POS_Z -> {
                ax = 0;
                ay = 0;
                az = 1;
            }
            default -> {
                ax = 0;
                ay = 0;
                az = -1;
            }
        }

        var sx = ax * screenRight.x + ay * screenRight.y + az * screenRight.z;
        var sy = ax * screenUp.x + ay * screenUp.y + az * screenUp.z;
        var depth = ax * forward.x + ay * forward.y + az * forward.z;

        // Y is flipped because screen coords have Y growing downward but the camera's screenUp grows upward.
        var screenX = centerX + (int) Math.round(sx * RADIUS);
        var screenY = centerY - (int) Math.round(sy * RADIUS);

        // The modeler's coordinate axes show Bedrock geometry post-X-flip (loader applies the same negate that the
        // runtime baker uses to map Bedrock geo into MC's entity-render space). Mentally, what the author calls
        // "X" is the modeler's -X column. Make the -X ball the primary one — the full-size ball with the line and
        // the "X" letter — and demote +X to a small secondary marker. Y / Z are unaffected because the loader
        // doesn't flip them.
        boolean primary = switch (which) {
            case NEG_X, POS_Y, POS_Z -> true;
            case POS_X, NEG_Y, NEG_Z -> false;
        };
        var color = switch (which) {
            case POS_X -> COLOR_NEG_X;
            case NEG_X -> COLOR_X;
            case POS_Y -> COLOR_Y;
            case NEG_Y -> COLOR_NEG_Y;
            case POS_Z -> COLOR_Z;
            case NEG_Z -> COLOR_NEG_Z;
        };
        var label = primary ? switch (which) {
            case NEG_X -> "X";
            case POS_Y -> "Y";
            case POS_Z -> "Z";
            default -> null;
        } : null;

        return new Endpoint(screenX, screenY, (float) depth, color, label, primary);
    }

    /**
     * 1-pixel-wide line via a Bresenham-style walk. {@link GuiGraphics} doesn't expose a diagonal-line primitive (only
     * {@code hLine} / {@code vLine}); for a ~20px widget line this hand-rolled pixel loop is cheap and avoids standing
     * up a vertex consumer just for one drawing.
     */
    private static void drawLine(GuiGraphics graphics, int x0, int y0, int x1, int y1, int color) {
        var dx = Math.abs(x1 - x0);
        var dy = Math.abs(y1 - y0);
        var sx = x0 < x1 ? 1 : -1;
        var sy = y0 < y1 ? 1 : -1;
        var err = dx - dy;
        var x = x0;
        var y = y0;
        while (true) {
            graphics.fill(x, y, x + 1, y + 1, color);
            if (x == x1 && y == y1) {
                break;
            }
            var e2 = 2 * err;
            if (e2 > -dy) {
                err -= dy;
                x += sx;
            }
            if (e2 < dx) {
                err += dx;
                y += sy;
            }
        }
    }

    private record Endpoint(
        int screenX,
        int screenY,
        float depth,
        int color,
        @Nullable String label,
        boolean primary
    ) {}
}
