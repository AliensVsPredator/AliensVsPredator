package com.blib.engine.ui;

import com.mojang.blaze3d.platform.NativeImage;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import java.util.function.IntConsumer;

import com.blib.api.common.color.v1.Color;
import com.blib.mod.BLib;

/**
 * Floating HSL color-picker popup. Hosts a hue ring around an inscribed saturation/lightness square; click + drag in
 * either zone updates the color and fires {@link #onCommit} so the caller can push the new value to its model in real
 * time. Rendered on top of all panels by {@link EngineWorkspaceScreen} via the same overlay pattern as
 * {@link SearchableSelect.Popup}.
 * <p>
 * The static-singleton {@link #openPopup} field mirrors {@link SearchableSelect}'s open-popup tracking so the workspace
 * can route events uniformly to whichever overlay is open at the time.
 */
@ApiStatus.Internal
public final class HslColorPickerPopup {

    private static final int POPUP_WIDTH = 120;

    private static final int POPUP_HEIGHT = 140;

    private static final int PADDING = 6;

    private static final int OUTER_RADIUS = 50;

    private static final int INNER_RADIUS = 38;

    /** Half-edge of the S/L square; chosen so corner-to-center distance < INNER_RADIUS (26 * √2 ≈ 36.8 < 38). */
    private static final int SQUARE_HALF = 26;

    private static final int BG_COLOR = 0xF01A1A1F;

    private static final int BORDER_COLOR = 0xFF353540;

    private static final int TEXT_COLOR = 0xFFD0D0D0;

    private static final int MARKER_OUTLINE = 0xFFFFFFFF;

    private static final int MARKER_FILL = 0xFF000000;

    private static final int RING_DIAMETER = OUTER_RADIUS * 2 + 1;

    private static final int SQUARE_EDGE = SQUARE_HALF * 2;

    /**
     * Lazy-initialised hue-ring texture. Built once on first render() — earlier than that, MC's GL context isn't
     * guaranteed ready. The ring is a pure function of (dx, dy), so this texture is reused for the JVM lifetime; total
     * cost is ~40 KB GPU memory for a one-time bake of 3300 HSL→RGB conversions. Replaces ~3300 per-frame graphics.fill
     * quad submissions with a single textured blit.
     */
    private static @Nullable DynamicTexture hueRingTexture;

    private static @Nullable ResourceLocation hueRingTextureId;

    /**
     * Shared S/L-square texture. Static rather than per-instance because only one popup is ever open at a time
     * (enforced by {@link #openPopup}). Re-uploaded when the active popup's {@link #hue} differs from the value baked
     * into the texture.
     */
    private static @Nullable DynamicTexture slSquareTexture;

    private static @Nullable ResourceLocation slSquareTextureId;

    private static float slSquareTextureHue = Float.NaN;

    private static @Nullable HslColorPickerPopup openPopup;

    private final IntConsumer onCommit;

    private final int anchorCenterX;

    private final int anchorBottomY;

    /**
     * {@code true} until the first {@link #render} call resolves the final position via the workspace's logical dims.
     */
    private boolean positionResolved;

    private int popupX;

    private int popupY;

    /** Current selection in HSL space; updated by hue-ring and S/L-square interactions. Hue is in [0, 1). */
    private float hue;

    private float saturation;

    private float lightness;

    private boolean draggingHue;

    private boolean draggingSL;

    private HslColorPickerPopup(int anchorCenterX, int anchorBottomY, int currentArgb, IntConsumer onCommit) {
        this.onCommit = onCommit;
        this.anchorCenterX = anchorCenterX;
        this.anchorBottomY = anchorBottomY;
        // Seed H/S/L from the current color so the markers land on the user's existing pick.
        var hsl = Color.ARGBtoHSL(currentArgb);
        this.hue = hsl[0];
        this.saturation = hsl[1];
        this.lightness = hsl[2];
    }

    public static void openAt(int anchorCenterX, int anchorBottomY, int currentArgb, IntConsumer onCommit) {
        openPopup = new HslColorPickerPopup(anchorCenterX, anchorBottomY, currentArgb, onCommit);
    }

    public static @Nullable HslColorPickerPopup getOpenPopup() {
        return openPopup;
    }

    public static void closeOpenPopup() {
        openPopup = null;
    }

    public boolean isInside(int mouseX, int mouseY) {
        // Before render() resolves position against the workspace's logical dims, popupX/Y are 0 — answer "not inside"
        // so the first-frame mouse routes to panels below rather than to a phantom rect at the workspace origin.
        if (!positionResolved) {
            return false;
        }
        return mouseX >= popupX && mouseX < popupX + POPUP_WIDTH && mouseY >= popupY && mouseY < popupY + POPUP_HEIGHT;
    }

    public void render(GuiGraphics graphics, int mouseX, int mouseY, int logicalWidth, int logicalHeight) {
        if (!positionResolved) {
            // First-frame placement against the workspace's actual logical dims (the constructor doesn't have them).
            // Center horizontally on the anchor, drop below; flip above if the natural placement would clip the bottom.
            popupX = Math.max(4, Math.min(logicalWidth - POPUP_WIDTH - 4, anchorCenterX - POPUP_WIDTH / 2));
            var below = anchorBottomY + 2;
            if (below + POPUP_HEIGHT > logicalHeight - 4) {
                popupY = Math.max(4, anchorBottomY - POPUP_HEIGHT - 4);
            } else {
                popupY = below;
            }
            positionResolved = true;
        }
        graphics.fill(popupX, popupY, popupX + POPUP_WIDTH, popupY + POPUP_HEIGHT, BG_COLOR);
        graphics.fill(popupX, popupY, popupX + POPUP_WIDTH, popupY + 1, BORDER_COLOR);
        graphics.fill(popupX, popupY + POPUP_HEIGHT - 1, popupX + POPUP_WIDTH, popupY + POPUP_HEIGHT, BORDER_COLOR);
        graphics.fill(popupX, popupY, popupX + 1, popupY + POPUP_HEIGHT, BORDER_COLOR);
        graphics.fill(popupX + POPUP_WIDTH - 1, popupY, popupX + POPUP_WIDTH, popupY + POPUP_HEIGHT, BORDER_COLOR);

        var centerX = popupX + POPUP_WIDTH / 2;
        var centerY = popupY + PADDING + OUTER_RADIUS;
        renderHueRing(graphics, centerX, centerY);
        renderSLSquare(graphics, centerX, centerY);
        renderMarkers(graphics, centerX, centerY);
        renderHexLabel(graphics);
    }

    /** Annular ring colored by hue — drawn as a single textured blit. The texture is built once and reused. */
    private void renderHueRing(GuiGraphics graphics, int cx, int cy) {
        ensureHueRingTexture();
        if (hueRingTextureId == null) {
            return;
        }
        graphics.blit(
            hueRingTextureId,
            cx - OUTER_RADIUS,
            cy - OUTER_RADIUS,
            0,
            0,
            RING_DIAMETER,
            RING_DIAMETER,
            RING_DIAMETER,
            RING_DIAMETER
        );
    }

    /** Inscribed S/L square — drawn as a single textured blit. Re-uploaded only when {@link #hue} changes. */
    private void renderSLSquare(GuiGraphics graphics, int cx, int cy) {
        ensureSLSquareTexture();
        if (slSquareTextureId == null) {
            return;
        }
        graphics.blit(slSquareTextureId, cx - SQUARE_HALF, cy - SQUARE_HALF, 0, 0, SQUARE_EDGE, SQUARE_EDGE, SQUARE_EDGE, SQUARE_EDGE);
    }

    /**
     * Build the hue-ring texture exactly once. Lazy because Minecraft's GL context isn't ready at class-load time.
     * Lives for the JVM lifetime; the ring is pure pixel data (no hue/sat/lightness inputs vary over time).
     */
    private static void ensureHueRingTexture() {
        if (hueRingTexture != null) {
            return;
        }
        var tex = new DynamicTexture(RING_DIAMETER, RING_DIAMETER, false);
        var pixels = tex.getPixels();
        if (pixels == null) {
            tex.close();
            return;
        }
        var innerSq = INNER_RADIUS * INNER_RADIUS;
        var outerSq = OUTER_RADIUS * OUTER_RADIUS;
        for (var dy = -OUTER_RADIUS; dy <= OUTER_RADIUS; dy++) {
            for (var dx = -OUTER_RADIUS; dx <= OUTER_RADIUS; dx++) {
                var distSq = dx * dx + dy * dy;
                if (distSq < innerSq || distSq > outerSq) {
                    pixels.setPixelRGBA(dx + OUTER_RADIUS, dy + OUTER_RADIUS, 0);
                    continue;
                }
                var angle = (float) Math.atan2(dy, dx);
                var h = angle / (float) (Math.PI * 2);
                h = h - (float) Math.floor(h);
                pixels.setPixelRGBA(dx + OUTER_RADIUS, dy + OUTER_RADIUS, argbToAbgr(Color.HSLtoARGB(h, 1f, 0.5f)));
            }
        }
        tex.upload();
        hueRingTexture = tex;
        hueRingTextureId = BLib.MOD.resources().createLocation("dynamic/hue_ring");
        Minecraft.getInstance().getTextureManager().register(hueRingTextureId, tex);
    }

    /**
     * Build the S/L-square texture lazily and re-upload its pixels when {@link #hue} differs from what's currently
     * baked. The texture is shared across popup instances (only one is open at a time) so we don't burn GPU memory for
     * every open/close cycle.
     */
    private void ensureSLSquareTexture() {
        if (slSquareTexture == null) {
            var tex = new DynamicTexture(SQUARE_EDGE, SQUARE_EDGE, false);
            if (tex.getPixels() == null) {
                tex.close();
                return;
            }
            slSquareTexture = tex;
            slSquareTextureId = BLib.MOD.resources().createLocation("dynamic/sl_square");
            Minecraft.getInstance().getTextureManager().register(slSquareTextureId, tex);
        }
        if (hue == slSquareTextureHue) {
            return;
        }
        var pixels = slSquareTexture.getPixels();
        if (pixels == null) {
            return;
        }
        for (var dy = 0; dy < SQUARE_EDGE; dy++) {
            for (var dx = 0; dx < SQUARE_EDGE; dx++) {
                var s = dx / (float) SQUARE_EDGE;
                var l = 1f - dy / (float) SQUARE_EDGE;
                pixels.setPixelRGBA(dx, dy, argbToAbgr(Color.HSLtoARGB(hue, s, l)));
            }
        }
        slSquareTexture.upload();
        slSquareTextureHue = hue;
    }

    /**
     * Swap R and B bytes of an ARGB int to match {@link NativeImage}'s native byte order. NativeImage stores pixels
     * RGBA byte-by-byte in memory, which on a little-endian read shows up as ABGR — the opposite of Java's standard
     * ARGB packing.
     */
    private static int argbToAbgr(int argb) {
        return (argb & 0xFF00FF00) | ((argb & 0xFF) << 16) | ((argb >> 16) & 0xFF);
    }

    private void renderMarkers(GuiGraphics graphics, int cx, int cy) {
        // Hue marker — small 4×4 outline + 2×2 fill on the ring at the current hue.
        var angle = hue * (float) (Math.PI * 2);
        var ringMid = (INNER_RADIUS + OUTER_RADIUS) / 2;
        var hx = cx + (int) (Math.cos(angle) * ringMid);
        var hy = cy + (int) (Math.sin(angle) * ringMid);
        graphics.fill(hx - 2, hy - 2, hx + 2, hy + 2, MARKER_OUTLINE);
        graphics.fill(hx - 1, hy - 1, hx + 1, hy + 1, MARKER_FILL);

        // S/L marker — same shape inside the square at the current (saturation, lightness).
        var sx = cx - SQUARE_HALF + (int) (saturation * (SQUARE_HALF * 2));
        var sy = cy - SQUARE_HALF + (int) ((1f - lightness) * (SQUARE_HALF * 2));
        graphics.fill(sx - 2, sy - 2, sx + 2, sy + 2, MARKER_OUTLINE);
        graphics.fill(sx - 1, sy - 1, sx + 1, sy + 1, MARKER_FILL);
    }

    private void renderHexLabel(GuiGraphics graphics) {
        var font = EngineFont.get();
        var argb = Color.HSLtoARGB(hue, saturation, lightness);
        var hex = String.format(java.util.Locale.ROOT, "#%06X", argb & 0xFFFFFF);
        var swatchX = popupX + PADDING;
        var swatchY = popupY + POPUP_HEIGHT - PADDING - 12;
        graphics.fill(swatchX, swatchY, swatchX + 12, swatchY + 12, argb);
        graphics.fill(swatchX, swatchY, swatchX + 12, swatchY + 1, BORDER_COLOR);
        graphics.fill(swatchX, swatchY + 11, swatchX + 12, swatchY + 12, BORDER_COLOR);
        graphics.fill(swatchX, swatchY, swatchX + 1, swatchY + 12, BORDER_COLOR);
        graphics.fill(swatchX + 11, swatchY, swatchX + 12, swatchY + 12, BORDER_COLOR);
        var textY = swatchY + (12 - font.lineHeight + 2) / 2;
        graphics.drawString(font, Component.literal(hex), swatchX + 18, textY, TEXT_COLOR, false);
    }

    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button != 0) {
            return false;
        }
        var centerX = popupX + POPUP_WIDTH / 2;
        var centerY = popupY + PADDING + OUTER_RADIUS;
        var dx = mouseX - centerX;
        var dy = mouseY - centerY;
        var distSq = dx * dx + dy * dy;
        if (distSq >= INNER_RADIUS * INNER_RADIUS && distSq <= OUTER_RADIUS * OUTER_RADIUS) {
            draggingHue = true;
            updateHueFromMouse(mouseX, mouseY);
            return true;
        }
        if (Math.abs(dx) <= SQUARE_HALF && Math.abs(dy) <= SQUARE_HALF) {
            draggingSL = true;
            updateSLFromMouse(mouseX, mouseY);
            return true;
        }
        // Click landed inside the popup chrome (background / hex label) — consume so the screen doesn't close us.
        return true;
    }

    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        if (button != 0) {
            return false;
        }
        if (draggingHue) {
            updateHueFromMouse(mouseX, mouseY);
            return true;
        }
        if (draggingSL) {
            updateSLFromMouse(mouseX, mouseY);
            return true;
        }
        return false;
    }

    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        if (button != 0) {
            return false;
        }
        var was = draggingHue || draggingSL;
        draggingHue = false;
        draggingSL = false;
        return was;
    }

    public boolean mouseScrolled(double mouseX, double mouseY, double scrollY) {
        // The popup itself has no scrollable content — just absorb scroll over the popup rect so it doesn't leak to
        // panels underneath while the picker is open.
        return isInside((int) mouseX, (int) mouseY);
    }

    private void updateHueFromMouse(double mouseX, double mouseY) {
        var centerX = popupX + POPUP_WIDTH / 2;
        var centerY = popupY + PADDING + OUTER_RADIUS;
        var angle = (float) Math.atan2(mouseY - centerY, mouseX - centerX);
        var h = angle / (float) (Math.PI * 2);
        h = h - (float) Math.floor(h);
        hue = h;
        commit();
    }

    private void updateSLFromMouse(double mouseX, double mouseY) {
        var centerX = popupX + POPUP_WIDTH / 2;
        var centerY = popupY + PADDING + OUTER_RADIUS;
        var sx = (float) ((mouseX - (centerX - SQUARE_HALF)) / (SQUARE_HALF * 2));
        var sy = (float) ((mouseY - (centerY - SQUARE_HALF)) / (SQUARE_HALF * 2));
        saturation = Math.max(0f, Math.min(1f, sx));
        lightness = Math.max(0f, Math.min(1f, 1f - sy));
        commit();
    }

    private void commit() {
        onCommit.accept(Color.HSLtoARGB(hue, saturation, lightness));
    }
}
