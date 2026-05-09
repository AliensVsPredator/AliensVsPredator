package com.blib.engine.ui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Top-of-screen menu bar — full-bleed (no chrome). Branding on the left, dropdown chips on the right. The chips are
 * hit-testable via {@link #hitChipAt} and {@link #chipRect}; the {@link EngineWorkspaceScreen} reads those to open the
 * matching {@link DropdownMenu} when a chip is clicked.
 */
@ApiStatus.Internal
public final class MenuBarPanel implements Panel {

    public static final int HEIGHT = 16;

    public static final String CHIP_FILE = "File";

    public static final String CHIP_EDIT = "Edit";

    public static final String CHIP_VIEW = "View";

    public static final String CHIP_WINDOW = "Window";

    public static final String CHIP_LAYOUT = "Layout";

    private static final String[] CHIPS = { CHIP_FILE, CHIP_EDIT, CHIP_VIEW, CHIP_WINDOW, CHIP_LAYOUT };

    private static final int BACKGROUND_COLOR = 0xFF202024;

    private static final int BORDER_COLOR = 0xFF101013;

    private static final int BRAND_COLOR = 0xFFE6C26B;

    private static final int CHIP_TEXT_COLOR = 0xFFD0D0D0;

    private static final int CHIP_BG_COLOR = 0xFF2C2C32;

    private static final int CHIP_HOVER_BG_COLOR = 0xFF3C3C46;

    private static final int CHIP_PADDING_X = 4;

    private static final int CHIP_GAP = 3;

    private static final int EDGE_PADDING = 6;

    public record ChipRect(
        int x,
        int y,
        int width,
        int height
    ) {}

    private final Map<String, ChipRect> chipRects = new LinkedHashMap<>();

    @Override
    public String title() {
        return "Menu";
    }

    @Override
    public boolean hasChrome() {
        return false;
    }

    @Override
    public void render(GuiGraphics graphics, int x, int y, int width, int height, int mouseX, int mouseY, float partialTick) {
        graphics.fill(x, y, x + width, y + height, BACKGROUND_COLOR);
        graphics.fill(x, y + height - 1, x + width, y + height, BORDER_COLOR);

        var font = Minecraft.getInstance().font;
        // Center the *visible* glyph rather than the full 9-pixel line box. MC's font reserves the bottom 2 pixels
        // of each 9-pixel line for descenders (g, p, y, q, j); for ASCII labels without those — every label here —
        // those 2 pixels are empty space, so the math-centered line box visually floats above the rect's center.
        // Substituting "visible height = lineHeight − 2 = 7" gives true visual centering and matches across both
        // even- and odd-height containers (where +1 would no-op on odd heights due to integer truncation).
        var textY = y + (height - font.lineHeight + 2) / 2;

        graphics.drawString(font, Component.literal("BLib Engine"), x + EDGE_PADDING, textY, BRAND_COLOR, false);

        chipRects.clear();
        var cursorX = x + EDGE_PADDING + font.width("BLib Engine") + 12;
        for (var chip : CHIPS) {
            var chipWidth = font.width(chip) + 2 * CHIP_PADDING_X;
            var chipY = y + 2;
            var chipH = height - 4;
            var hovered = mouseX >= cursorX && mouseX < cursorX + chipWidth && mouseY >= chipY && mouseY < chipY + chipH;
            graphics.fill(cursorX, chipY, cursorX + chipWidth, chipY + chipH, hovered ? CHIP_HOVER_BG_COLOR : CHIP_BG_COLOR);
            graphics.drawString(font, Component.literal(chip), cursorX + CHIP_PADDING_X, textY, CHIP_TEXT_COLOR, false);

            chipRects.put(chip, new ChipRect(cursorX, chipY, chipWidth, chipH));
            cursorX += chipWidth + CHIP_GAP;
        }
    }

    public @Nullable String hitChipAt(double mouseX, double mouseY) {
        for (var entry : chipRects.entrySet()) {
            var r = entry.getValue();
            if (mouseX >= r.x && mouseX < r.x + r.width && mouseY >= r.y && mouseY < r.y + r.height) {
                return entry.getKey();
            }
        }
        return null;
    }

    public @Nullable ChipRect chipRect(String chipName) {
        return chipRects.get(chipName);
    }
}
