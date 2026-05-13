package com.blib.engine.ui.panel.chrome;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import java.util.LinkedHashMap;
import java.util.Map;

import com.blib.engine.ui.EngineFont;
import com.blib.engine.ui.panel.viewport.ModelerViewportPanel;
import com.blib.engine.ui.widget.DropdownMenu;

/**
 * Modeler-scoped menu bar drawn along the top edge of the {@link ModelerViewportPanel}. Mirrors the top-level
 * {@link MenuBarPanel} chip style so the two read as the same affordance, but the entries here are modeler-only (Open /
 * Save / Import / Export / etc.) and live with the panel that owns those actions rather than cluttering the global
 * menu.
 * <p>
 * Stateless utility — the host panel calls {@link #render} every frame, then {@link #hitChipAt} on clicks to figure out
 * which chip was struck and {@link #chipRect} to anchor a {@link DropdownMenu} at the chip's bottom edge.
 */
@ApiStatus.Internal
public final class ModelerMenuBar {

    /** Matches {@link MenuBarPanel#HEIGHT} so the panel-local strip reads as the same kind of affordance. */
    public static final int HEIGHT = MenuBarPanel.HEIGHT;

    public static final String CHIP_FILE = "File";

    public static final String CHIP_TRANSFORM = "Transform";

    private static final String[] CHIPS = { CHIP_FILE, CHIP_TRANSFORM };

    // Color + spacing constants are intentionally identical to {@link MenuBarPanel} — the two menu bars must read as
    // the same control type. If the global bar ever drifts visually, update both together (or move the constants to
    // a shared place).
    private static final int BACKGROUND_COLOR = 0xFF202024;

    private static final int BORDER_COLOR = 0xFF101013;

    private static final int CHIP_TEXT_COLOR = 0xFFD0D0D0;

    private static final int CHIP_BG_COLOR = 0xFF2C2C32;

    private static final int CHIP_HOVER_BG_COLOR = 0xFF3C3C46;

    private static final int CHIP_PADDING_X = 4;

    private static final int CHIP_GAP = 3;

    private static final int EDGE_PADDING = 6;

    /**
     * Per-frame layout snapshot — repopulated by {@link #render} so {@link #hitChipAt} / {@link #chipRect} can resolve
     * the click against the rects just drawn.
     */
    private static final Map<String, ChipRect> chipRects = new LinkedHashMap<>();

    public record ChipRect(
        int x,
        int y,
        int width,
        int height
    ) {}

    private ModelerMenuBar() {}

    public static void render(GuiGraphics graphics, int panelX, int panelY, int panelWidth, int mouseX, int mouseY) {
        graphics.fill(panelX, panelY, panelX + panelWidth, panelY + HEIGHT, BACKGROUND_COLOR);
        graphics.fill(panelX, panelY + HEIGHT - 1, panelX + panelWidth, panelY + HEIGHT, BORDER_COLOR);

        var font = EngineFont.get();
        // Match the {@link MenuBarPanel} centering trick — substituting visible glyph height for the full line box so
        // ASCII-only labels (every chip here) center optically instead of math-centering with the descender slack.
        var textY = panelY + (HEIGHT - font.lineHeight + 2) / 2;

        // Chip insets and resting fill mirror {@link MenuBarPanel} exactly. The +2 / -4 chipY/chipH math leaves a
        // 2-px gutter top and bottom so the chip fills don't crowd the bar's top edge or the bottom border line.
        chipRects.clear();
        var cursorX = panelX + EDGE_PADDING;
        for (var chip : CHIPS) {
            var chipWidth = font.width(chip) + 2 * CHIP_PADDING_X;
            var chipY = panelY + 2;
            var chipH = HEIGHT - 4;
            var hovered = mouseX >= cursorX && mouseX < cursorX + chipWidth && mouseY >= chipY && mouseY < chipY + chipH;
            graphics.fill(cursorX, chipY, cursorX + chipWidth, chipY + chipH, hovered ? CHIP_HOVER_BG_COLOR : CHIP_BG_COLOR);
            graphics.drawString(font, Component.literal(chip), cursorX + CHIP_PADDING_X, textY, CHIP_TEXT_COLOR, false);

            chipRects.put(chip, new ChipRect(cursorX, chipY, chipWidth, chipH));
            cursorX += chipWidth + CHIP_GAP;
        }
    }

    public static @Nullable String hitChipAt(double mouseX, double mouseY) {
        for (var entry : chipRects.entrySet()) {
            var r = entry.getValue();
            if (mouseX >= r.x && mouseX < r.x + r.width && mouseY >= r.y && mouseY < r.y + r.height) {
                return entry.getKey();
            }
        }
        return null;
    }

    public static @Nullable ChipRect chipRect(String chip) {
        return chipRects.get(chip);
    }
}
