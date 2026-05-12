package com.blib.engine.ui;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import com.blib.engine.modeler.gizmo.ModelerGizmoMode;
import com.blib.engine.modeler.gizmo.ModelerGizmoState;

/**
 * Tiny mode-switch overlay drawn on top of the modeler viewport (no FBO involvement — straight {@link GuiGraphics}
 * fills + glyphs). Four buttons select the active {@link ModelerGizmoMode}: OFF (select only), TRANSLATE, ROTATE,
 * RESIZE. The active mode renders with a brighter fill so the user knows what gizmo will appear on the selected cube.
 * <p>
 * Buttons live at the top-left of the panel's content rect, inside a small inset. Click routing is handled by
 * {@link #hitTest}: returns the mode the click would activate, or {@code null} when the cursor is outside any button.
 */
@ApiStatus.Internal
public final class ModelerViewportToolbar {

    private static final int BUTTON_SIZE = 16;

    private static final int BUTTON_GAP = 2;

    private static final int INSET = 4;

    private static final int BG_COLOR = 0xFF1A1A1A;

    private static final int BG_ACTIVE_COLOR = 0xFF4A4A4A;

    private static final int BORDER_COLOR = 0xFF3A3A3A;

    private static final int LABEL_COLOR = 0xFFD0D0D0;

    private static final int LABEL_ACTIVE_COLOR = 0xFFFFCC33;

    private static final ModelerGizmoMode[] MODES = {
        ModelerGizmoMode.OFF,
        ModelerGizmoMode.TRANSLATE,
        ModelerGizmoMode.ROTATE,
        ModelerGizmoMode.RESIZE };

    private ModelerViewportToolbar() {}

    public static void render(GuiGraphics graphics, int panelX, int panelY) {
        var active = ModelerGizmoState.mode();
        var font = EngineFont.get();

        for (int i = 0; i < MODES.length; i++) {
            int bx = panelX + INSET + i * (BUTTON_SIZE + BUTTON_GAP);
            int by = panelY + INSET;
            var mode = MODES[i];
            boolean isActive = mode == active;

            graphics.fill(bx, by, bx + BUTTON_SIZE, by + BUTTON_SIZE, isActive ? BG_ACTIVE_COLOR : BG_COLOR);
            graphics.fill(bx, by, bx + BUTTON_SIZE, by + 1, BORDER_COLOR);
            graphics.fill(bx, by + BUTTON_SIZE - 1, bx + BUTTON_SIZE, by + BUTTON_SIZE, BORDER_COLOR);
            graphics.fill(bx, by, bx + 1, by + BUTTON_SIZE, BORDER_COLOR);
            graphics.fill(bx + BUTTON_SIZE - 1, by, bx + BUTTON_SIZE, by + BUTTON_SIZE, BORDER_COLOR);

            var label = label(mode);
            int labelW = font.width(label);
            int labelH = font.lineHeight;
            int labelX = bx + (BUTTON_SIZE - labelW) / 2;
            int labelY = by + (BUTTON_SIZE - labelH) / 2;
            graphics.drawString(font, label, labelX, labelY, isActive ? LABEL_ACTIVE_COLOR : LABEL_COLOR, false);
        }
    }

    /**
     * Returns the mode whose button covers {@code (mouseX, mouseY)} relative to the panel rect, or null when the cursor
     * is outside any button.
     */
    public static @Nullable ModelerGizmoMode hitTest(double mouseX, double mouseY, int panelX, int panelY) {
        for (int i = 0; i < MODES.length; i++) {
            int bx = panelX + INSET + i * (BUTTON_SIZE + BUTTON_GAP);
            int by = panelY + INSET;
            if (mouseX >= bx && mouseX < bx + BUTTON_SIZE && mouseY >= by && mouseY < by + BUTTON_SIZE) {
                return MODES[i];
            }
        }
        return null;
    }

    private static Component label(ModelerGizmoMode mode) {
        return switch (mode) {
            case OFF -> Component.literal("—");
            case TRANSLATE -> Component.literal("T");
            case ROTATE -> Component.literal("R");
            case RESIZE -> Component.literal("S");
        };
    }
}
