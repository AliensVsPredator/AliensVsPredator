package com.blib.engine.ui.panel.action;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import org.jetbrains.annotations.ApiStatus;

import com.blib.engine.history.ClientActionHistory;
import com.blib.engine.modeler.history.ModelerActionHistory;
import com.blib.engine.ui.EngineFont;
import com.blib.engine.ui.EngineWorkspaceScreen;
import com.blib.engine.ui.PanelPlaceholder;
import com.blib.engine.ui.dock.Panel;
import com.blib.engine.ui.layout.ScrollViewport;
import com.blib.engine.ui.layout.UiRect;
import com.blib.engine.ui.layout.UiText;
import com.blib.mod.common.network.packet.ActionDescriptor;

/**
 * Display-only view of the server-side action history. Each row corresponds to one {@code EditorAction} pushed since
 * the engine session began; the cursor divider marks the boundary between undoable (above) and redoable (below). No row
 * interactivity per design — undo/redo always flow through Ctrl+Z / Ctrl+Y or the Edit menu so there's exactly one
 * mechanism for time-travel.
 * <p>
 * State is read live from {@link ClientActionHistory#INSTANCE} on every {@link #render} — the panel doesn't subscribe
 * because the cache is updated atomically by the S2C sync handler before the next frame.
 */
@ApiStatus.Internal
public final class ActionStackPanel implements Panel {

    private static final int BACKGROUND_COLOR = 0xFF101010;

    private static final int ROW_BG_HOVER_COLOR = 0xFF1F1F26;

    private static final int ROW_TEXT_COLOR = 0xFFD0D0D0;

    private static final int ROW_TEXT_REDO_COLOR = 0xFF707078;

    private static final int META_TEXT_COLOR = 0xFF707078;

    private static final int META_TEXT_REDO_COLOR = 0xFF50505A;

    private static final int CURSOR_LINE_COLOR = 0xFF6B9BD8;

    private static final int EMPTY_TEXT_COLOR = 0xFF606068;

    private static final int CONTENT_PADDING = 5;

    private static final int ROW_HEIGHT = 12;

    private static final int CURSOR_LINE_HEIGHT = 1;

    private final ScrollViewport scroll = new ScrollViewport();

    @Override
    public String title() {
        return "Action Stack";
    }

    @Override
    public void onShown() {
        scroll.reset();
    }

    @Override
    public void render(GuiGraphics graphics, int x, int y, int width, int height, int mouseX, int mouseY, float partialTick) {
        graphics.fill(x, y, x + width, y + height, BACKGROUND_COLOR);

        // Local authoring layouts have a parallel client-side history (heap-only — no server roundtrip). Picking the
        // right source by layout-context lets the same panel cover both worlds.
        var localHistoryMode = EngineWorkspaceScreen.activeLayoutHasLocalHistoryPanel();
        // Server-history needs a world; local history is purely client-side and works at the title screen too. Only
        // gate on the "needs world" placeholder when we're showing the server history.
        if (!localHistoryMode && Minecraft.getInstance().level == null) {
            scroll.clear();
            PanelPlaceholder.drawCentered(graphics, x, y, width, height, PanelPlaceholder.NEEDS_WORLD);
            return;
        }

        var history = localHistoryMode ? ModelerActionHistory.asService() : ClientActionHistory.INSTANCE;
        var entries = history.entries();
        var cursor = history.undoCursor();

        if (entries.isEmpty()) {
            scroll.clear();
            renderEmpty(graphics, x + CONTENT_PADDING, y + CONTENT_PADDING);
            return;
        }

        // Each entry is one row, plus a cursor divider line if it falls between rows.
        var hasCursor = cursor > 0 || cursor < entries.size();
        var contentHeight = entries.size() * ROW_HEIGHT + (hasCursor ? CURSOR_LINE_HEIGHT : 0);
        var listH = Math.max(0, height - 2 * CONTENT_PADDING);
        if (listH <= 0) {
            return;
        }
        var frame = scroll.begin(
            graphics,
            UiRect.of(x + CONTENT_PADDING, y + CONTENT_PADDING, width - 2 * CONTENT_PADDING, listH),
            contentHeight
        );
        try {
            var listX = frame.contentX();
            var listW = frame.contentWidth();
            var cursorY = frame.contentY();
            for (var i = 0; i < entries.size(); i++) {
                if (i == cursor) {
                    graphics.fill(listX, cursorY, listX + listW, cursorY + CURSOR_LINE_HEIGHT, CURSOR_LINE_COLOR);
                    cursorY += CURSOR_LINE_HEIGHT;
                }
                renderRow(graphics, listX, cursorY, listW, entries.get(i), i < cursor, mouseX, mouseY);
                cursorY += ROW_HEIGHT;
            }
            // Cursor at the very bottom (entries.size() == cursor — all undoable, nothing to redo). Skip drawing then
            // since there's no row below to visually separate from; the very-end case isn't load-bearing for the user.
        } finally {
            scroll.end(graphics, mouseX, mouseY);
        }
    }

    private void renderRow(
        GuiGraphics graphics,
        int x,
        int y,
        int width,
        ActionDescriptor entry,
        boolean isUndoable,
        int mouseX,
        int mouseY
    ) {
        var rowRight = x + width;
        var row = UiRect.of(x, y, width, ROW_HEIGHT);
        var hovered = row.contains(mouseX, mouseY);
        if (hovered) {
            graphics.fill(x, y, rowRight, y + ROW_HEIGHT, ROW_BG_HOVER_COLOR);
        }

        var font = EngineFont.get();
        var textColor = isUndoable ? ROW_TEXT_COLOR : ROW_TEXT_REDO_COLOR;
        var metaColor = isUndoable ? META_TEXT_COLOR : META_TEXT_REDO_COLOR;

        var meta = relativeTime(entry.timestamp());
        var metaWidth = font.width(meta);
        var textY = y + (ROW_HEIGHT - font.lineHeight) / 2;

        // Right-align meta first; truncate the description if it overflows the remaining space.
        UiText.drawRight(graphics, font, meta, UiRect.of(x, y, width - 2, ROW_HEIGHT), metaColor);

        var available = (rowRight - metaWidth - 6) - (x + 2);
        UiText.drawClipped(graphics, font, entry.description(), x + 2, textY, Math.max(0, available), textColor);
    }

    private void renderEmpty(GuiGraphics graphics, int x, int y) {
        var font = EngineFont.get();
        UiText.drawClipped(graphics, font, "(no actions yet)", x, y, 120, EMPTY_TEXT_COLOR);
    }

    private static String relativeTime(long ts) {
        var delta = Math.max(0L, System.currentTimeMillis() - ts);
        var seconds = delta / 1000L;
        if (seconds < 60) {
            return seconds + "s";
        }
        var minutes = seconds / 60L;
        if (minutes < 60) {
            return minutes + "m";
        }
        var hours = minutes / 60L;
        return hours + "h";
    }

    @Override
    public boolean mouseClickedCapture(double mouseX, double mouseY, int button) {
        return scroll.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        return scroll.mouseDragged(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        return scroll.mouseReleased(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
        return scroll.mouseScrolled(mouseX, mouseY, scrollY);
    }
}
