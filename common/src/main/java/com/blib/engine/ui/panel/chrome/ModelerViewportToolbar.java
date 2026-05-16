package com.blib.engine.ui.panel.chrome;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import com.blib.engine.modeler.gizmo.ModelerGizmoFrame;
import com.blib.engine.modeler.gizmo.ModelerGizmoMode;
import com.blib.engine.modeler.gizmo.ModelerGizmoState;
import com.blib.engine.ui.EngineFont;
import com.blib.engine.ui.layout.UiRect;
import com.blib.engine.ui.widget.UiButton;

/**
 * Tiny mode-switch overlay drawn on top of the modeler viewport (no FBO involvement — straight {@link GuiGraphics}
 * fills + glyphs). Five buttons select the active {@link ModelerGizmoMode}: OFF (select only), TRANSLATE, ROTATE,
 * RESIZE, PIVOT. The active mode renders with a brighter fill so the user knows what gizmo will appear on the selected
 * cube. After the mode buttons sits a wider frame-cycle button: click cycles {@link ModelerGizmoFrame} (LOCAL → GLOBAL
 * → …). The frame applies only to TRANSLATE / PIVOT — for the other modes the setting persists but has no visible
 * effect.
 * <p>
 * Buttons live at the top-left of the panel's content rect, inside a small inset. Click routing is handled by
 * {@link #hitTestMode} / {@link #hitTestFrame}: each returns whether the click landed on the corresponding control, or
 * {@code null} / {@code false} when the cursor is outside.
 */
@ApiStatus.Internal
public final class ModelerViewportToolbar {

    private static final int BUTTON_SIZE = 16;

    private static final int BUTTON_GAP = 2;

    /** Extra gap between the last mode button and the frame button — visually separates the two control groups. */
    private static final int GROUP_GAP = 6;

    /** Wider than the mode buttons so "Local" / "Global" labels fit; matches the toolbar height to read as a row. */
    private static final int FRAME_BUTTON_WIDTH = 38;

    private static final int INSET = 4;

    private static final int BG_COLOR = 0xFF1A1A1A;

    private static final int BG_ACTIVE_COLOR = 0xFF4A4A4A;

    private static final int BORDER_COLOR = 0xFF3A3A3A;

    private static final int LABEL_COLOR = 0xFFD0D0D0;

    private static final int LABEL_ACTIVE_COLOR = 0xFFFFCC33;

    private static final UiButton.Style STYLE = new UiButton.Style(
        BG_COLOR,
        0xFF2A2A36,
        BG_ACTIVE_COLOR,
        0xFF565660,
        0xFF101013,
        BORDER_COLOR,
        LABEL_COLOR,
        0xFF606068,
        UiButton.ADDITIVE_CONTENT_COLOR
    );

    private static final ModelerGizmoMode[] MODES = {
        ModelerGizmoMode.OFF,
        ModelerGizmoMode.TRANSLATE,
        ModelerGizmoMode.ROTATE,
        ModelerGizmoMode.RESIZE,
        ModelerGizmoMode.PIVOT,
        ModelerGizmoMode.SCALE };

    private ModelerViewportToolbar() {}

    public static void render(GuiGraphics graphics, int panelX, int panelY, int mouseX, int mouseY) {
        var active = ModelerGizmoState.mode();
        var font = EngineFont.get();

        for (int i = 0; i < MODES.length; i++) {
            int bx = panelX + INSET + i * (BUTTON_SIZE + BUTTON_GAP);
            int by = panelY + INSET;
            var mode = MODES[i];
            boolean isActive = mode == active;
            var rect = UiRect.of(bx, by, BUTTON_SIZE, BUTTON_SIZE);

            UiButton.drawFrame(graphics, rect, STYLE, isActive, true, mouseX, mouseY);

            var label = label(mode);
            int labelW = font.width(label);
            int labelH = font.lineHeight;
            int labelX = bx + (BUTTON_SIZE - labelW) / 2;
            int labelY = by + (BUTTON_SIZE - labelH) / 2;
            graphics.drawString(font, label, labelX, labelY, isActive ? LABEL_ACTIVE_COLOR : LABEL_COLOR, false);
        }

        // Frame cycle button after the mode group. Rendered with the same chrome style; the label is the current
        // frame's display name. Click handling in hitTestFrame.
        var frameRect = frameButtonRect(panelX, panelY);
        var frame = ModelerGizmoState.frame();
        var frameUiRect = UiRect.of(frameRect.x, frameRect.y, frameRect.w, frameRect.h);
        UiButton.drawFrame(graphics, frameUiRect, STYLE, false, true, mouseX, mouseY);
        var frameLabel = Component.literal(frame.label());
        int frameLabelW = font.width(frameLabel);
        int frameLabelH = font.lineHeight;
        int frameLabelX = frameRect.x + (frameRect.w - frameLabelW) / 2;
        int frameLabelY = frameRect.y + (frameRect.h - frameLabelH) / 2;
        graphics.drawString(font, frameLabel, frameLabelX, frameLabelY, LABEL_COLOR, false);
    }

    /**
     * Returns the mode whose button covers {@code (mouseX, mouseY)} relative to the panel rect, or null when the cursor
     * is outside any mode button. Frame button hits go through {@link #hitTestFrame} instead.
     */
    public static @Nullable ModelerGizmoMode hitTestMode(double mouseX, double mouseY, int panelX, int panelY) {
        for (int i = 0; i < MODES.length; i++) {
            int bx = panelX + INSET + i * (BUTTON_SIZE + BUTTON_GAP);
            int by = panelY + INSET;
            if (mouseX >= bx && mouseX < bx + BUTTON_SIZE && mouseY >= by && mouseY < by + BUTTON_SIZE) {
                return MODES[i];
            }
        }
        return null;
    }

    /** Legacy alias retained for any external callers — forwards to {@link #hitTestMode}. */
    public static @Nullable ModelerGizmoMode hitTest(double mouseX, double mouseY, int panelX, int panelY) {
        return hitTestMode(mouseX, mouseY, panelX, panelY);
    }

    /** True when {@code (mouseX, mouseY)} sits inside the frame-cycle button. */
    public static boolean hitTestFrame(double mouseX, double mouseY, int panelX, int panelY) {
        var rect = frameButtonRect(panelX, panelY);
        return mouseX >= rect.x && mouseX < rect.x + rect.w && mouseY >= rect.y && mouseY < rect.y + rect.h;
    }

    private static Rect frameButtonRect(int panelX, int panelY) {
        int afterModes = panelX + INSET + MODES.length * (BUTTON_SIZE + BUTTON_GAP);
        int x = afterModes + GROUP_GAP;
        int y = panelY + INSET;
        return new Rect(x, y, FRAME_BUTTON_WIDTH, BUTTON_SIZE);
    }

    private static Component label(ModelerGizmoMode mode) {
        return switch (mode) {
            case OFF -> Component.literal("—");
            case TRANSLATE -> Component.literal("T");
            case ROTATE -> Component.literal("R");
            case RESIZE -> Component.literal("S");
            case PIVOT -> Component.literal("P");
            case SCALE -> Component.literal("U");
        };
    }

    /**
     * Hover tooltip for the mode buttons. Includes the hotkey letter in the user-facing text so users can learn
     * keyboard shortcuts by hovering — preferred to a separate cheat-sheet overlay since the hotkeys correspond 1:1
     * with the button order.
     */
    public static Component tooltipForMode(ModelerGizmoMode mode) {
        return switch (mode) {
            case OFF -> Component.literal("Select only — no gizmo handles");
            case TRANSLATE -> Component.literal("Translate (T) — drag the arrows to move the selected cube");
            case ROTATE -> Component.literal("Rotate (R) — drag a ring to rotate the selected cube");
            case RESIZE -> Component.literal("Resize (S) — drag a face handle to grow or shrink the cube");
            case PIVOT -> Component.literal("Move pivot (P) — drag the arrows to move the rotation center; the cube body stays in place");
            case SCALE -> Component.literal("Uniform scale (U) — drag any axis to scale bones / item transforms proportionally");
        };
    }

    /**
     * Hover tooltip for the frame-cycle button. Reads the current frame so the user sees both what's active and what
     * the click will do next, which matters more here than for the mode buttons because clicking is a cycle, not a
     * direct select.
     */
    public static Component tooltipForFrame() {
        var current = ModelerGizmoState.frame();
        var next = current.next();
        var explanation = switch (current) {
            case LOCAL -> "arrows align with the selected cube's axes";
            case GLOBAL -> "arrows align with world axes";
        };
        return Component.literal("Frame: " + current.label() + " — " + explanation + ". Click to switch to " + next.label() + ".");
    }

    private record Rect(
        int x,
        int y,
        int w,
        int h
    ) {}
}
