package com.blib.engine.ui.panel.action;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.ApiStatus;
import org.joml.Vector3f;

import com.blib.engine.history.ClientActionHistory;
import com.blib.engine.modeler.history.ModelerActionHistory;
import com.blib.engine.ui.EngineFont;
import com.blib.engine.ui.EngineWorkspaceScreen;
import com.blib.engine.ui.PanelPlaceholder;
import com.blib.engine.ui.dock.Panel;
import com.blib.engine.ui.widget.ScrollContainer;
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

    private final ScrollContainer scroll = new ScrollContainer();

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

        // Modeler layout has a parallel client-side history (the modeler scene is heap-only — no server roundtrip).
        // Picking the right source by layout-context lets the same panel cover both worlds.
        var modelerMode = EngineWorkspaceScreen.activeLayoutHasModelerPanel();
        // Server-history needs a world; modeler-history is purely client-side and works at the title screen too. Only
        // gate on the "needs world" placeholder when we're showing the server history.
        if (!modelerMode && Minecraft.getInstance().level == null) {
            PanelPlaceholder.drawCentered(graphics, x, y, width, height, PanelPlaceholder.NEEDS_WORLD);
            return;
        }

        var history = modelerMode ? ModelerActionHistory.asService() : ClientActionHistory.INSTANCE;
        var entries = history.entries();
        var cursor = history.undoCursor();

        if (entries.isEmpty()) {
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
        scroll.layout(listH, contentHeight);

        var listX = x + CONTENT_PADDING;
        var listY = y + CONTENT_PADDING;
        var listW = width - 2 * CONTENT_PADDING;

        applyRawScissor(graphics, listX, listY, listW, listH);
        try {
            var scrollY = (int) scroll.scrollY();
            var cursorY = listY - scrollY;
            for (var i = 0; i < entries.size(); i++) {
                if (i == cursor) {
                    graphics.fill(
                        listX,
                        cursorY,
                        listX + listW - ScrollContainer.SCROLLBAR_GUTTER,
                        cursorY + CURSOR_LINE_HEIGHT,
                        CURSOR_LINE_COLOR
                    );
                    cursorY += CURSOR_LINE_HEIGHT;
                }
                renderRow(graphics, listX, cursorY, listW, entries.get(i), i < cursor, mouseX, mouseY);
                cursorY += ROW_HEIGHT;
            }
            // Cursor at the very bottom (entries.size() == cursor — all undoable, nothing to redo). Skip drawing then
            // since there's no row below to visually separate from; the very-end case isn't load-bearing for the user.
        } finally {
            graphics.flush();
            RenderSystem.disableScissor();
        }

        scroll.renderScrollbar(graphics, listX, listY, listW, listH, mouseX, mouseY);
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
        var rowRight = x + width - ScrollContainer.SCROLLBAR_GUTTER;
        var hovered = mouseX >= x && mouseX < rowRight && mouseY >= y && mouseY < y + ROW_HEIGHT;
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
        graphics.drawString(font, Component.literal(meta), rowRight - metaWidth - 2, textY, metaColor, false);

        var available = (rowRight - metaWidth - 6) - (x + 2);
        var desc = truncate(font, entry.description(), Math.max(0, available));
        graphics.drawString(font, Component.literal(desc), x + 2, textY, textColor, false);
    }

    private void renderEmpty(GuiGraphics graphics, int x, int y) {
        var font = EngineFont.get();
        graphics.drawString(font, Component.literal("(no actions yet)"), x, y, EMPTY_TEXT_COLOR, false);
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

    private static String truncate(Font font, String text, int maxWidth) {
        if (font.width(text) <= maxWidth) {
            return text;
        }
        var ellipsis = "…";
        var ellipsisWidth = font.width(ellipsis);
        var clipWidth = Math.max(0, maxWidth - ellipsisWidth);
        if (clipWidth <= 0) {
            return ellipsis;
        }
        var truncated = font.plainSubstrByWidth(text, clipWidth);
        return truncated + ellipsis;
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
        scroll.scrollBy((int) (-scrollY * ROW_HEIGHT));
        return true;
    }

    /**
     * Raw GL scissor in workspace logical-pixel space. Same pattern as OutlinerPanel — bypasses GuiGraphics's scissor
     * stack so we don't depend on upstream cleanliness.
     */
    private static void applyRawScissor(GuiGraphics graphics, int x, int y, int w, int h) {
        if (w <= 0 || h <= 0) {
            RenderSystem.disableScissor();
            return;
        }
        graphics.flush();
        var matrix = graphics.pose().last().pose();
        var topLeft = matrix.transformPosition((float) x, (float) y, 0f, new Vector3f());
        var bottomRight = matrix.transformPosition((float) (x + w), (float) (y + h), 0f, new Vector3f());
        var window = Minecraft.getInstance().getWindow();
        var winHeight = window.getHeight();
        var guiScale = window.getGuiScale();
        var leftRaw = (int) ((double) topLeft.x * guiScale);
        var bottomRaw = (int) ((double) winHeight - (double) bottomRight.y * guiScale);
        var widthRaw = Math.max(0, (int) ((double) (bottomRight.x - topLeft.x) * guiScale));
        var heightRaw = Math.max(0, (int) ((double) (bottomRight.y - topLeft.y) * guiScale));
        RenderSystem.enableScissor(leftRaw, bottomRaw, widthRaw, heightRaw);
    }
}
