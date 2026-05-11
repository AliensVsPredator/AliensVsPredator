package com.blib.engine.ui;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.List;

import com.blib.api.common.faction.v1.RelationshipState;
import com.blib.internal.client.faction.ClientFactionDirectoryCache;
import com.blib.mod.BLib;
import com.blib.mod.common.network.packet.C2SSetFactionRelationshipPayload;
import com.blib.mod.common.network.packet.S2CFactionDirectoryPayload;

/**
 * N×N grid showing the pairwise {@link RelationshipState} between every faction in {@link ClientFactionDirectoryCache}.
 * Cells are color-coded (NEUTRAL=grey, ALLIED=green, HOSTILE=red); clicking an off-diagonal cell cycles its state and
 * fires {@link C2SSetFactionRelationshipPayload} to the server. The server normalizes the pair (lex-min) and pushes the
 * directory back, so the matrix self-refreshes after each click.
 * <p>
 * Diagonal cells (faction-with-itself) are inert and rendered with the faction's own color stripe. The grid scrolls
 * vertically (whole-grid pan) and horizontally (cells + column-headers pan; row-header gutter stays fixed on the left).
 */
@ApiStatus.Internal
public final class DiplomacyMatrixPanel implements Panel {

    private static final int BACKGROUND_COLOR = 0xFF14141A;

    private static final int GRID_BORDER_COLOR = 0xFF2A2A35;

    private static final int HEADER_TEXT_COLOR = 0xFFD0D0D0;

    private static final int HEADER_TEXT_HOVER_COLOR = 0xFFFFFFFF;

    private static final int HEADER_BG_COLOR = 0xFF1F1F26;

    private static final int HEADER_BG_HOVER_COLOR = 0xFF2A2A38;

    private static final int CELL_NEUTRAL_BG = 0xFF2A2A35;

    private static final int CELL_ALLIED_BG = 0xFF2D6633;

    private static final int CELL_HOSTILE_BG = 0xFF6E2F2F;

    private static final int CELL_HOVER_BORDER = 0xFFE6C26B;

    private static final int CELL_CROSSHAIR_TINT = 0x18FFFFFF;

    private static final int EMPTY_TEXT_COLOR = 0xFF606068;

    private static final int CONTENT_PADDING = 5;

    private static final int CELL_SIZE = 18;

    private static final int ROW_HEADER_WIDTH = 90;

    // Tall enough for: 2px top padding + 3px swatch + 3px gap + ~9px text line + 3px bottom padding. The old value
    // (56) was provisioned for rotated full-length labels, but we render horizontally-truncated names that fit in
    // CELL_SIZE wide — the extra height was just empty space between the header text and the cells below.
    private static final int COL_HEADER_HEIGHT = 20;

    private final TextInput searchInput = new TextInput("Filter factions…");

    private final ScrollContainer scroll = new ScrollContainer();

    private final HorizontalScrollContainer scrollX = new HorizontalScrollContainer();

    /** Per-frame cell hits: cell rect + (factionA, factionB, currentState). */
    private final List<CellHit> cellHits = new ArrayList<>();

    /**
     * Tooltip computed during {@link #render} when the cursor is over a matrix cell, then read by {@link #tooltipText}
     * after the workspace's render pass. Reset to {@code null} at the top of every frame so a stale hover doesn't
     * ghost.
     */
    private @Nullable Component hoveredCellTooltip;

    /**
     * Pan-drag state — left-click on a cell starts the pan; subsequent drag updates both scroll axes relative to the
     * click anchor so the matrix view follows the cursor. Right-click is reserved for cycling the cell's relationship
     * state.
     */
    private boolean panning;

    private double panAnchorMouseX;

    private double panAnchorMouseY;

    private float panAnchorScrollX;

    private float panAnchorScrollY;

    private int rectX;

    private int rectY;

    private int rectWidth;

    private int rectHeight;

    @Override
    public String title() {
        return "Diplomacy Matrix";
    }

    @Override
    public void onShown() {
        scroll.reset();
        scrollX.reset();
    }

    @Override
    public @Nullable Component tooltipText() {
        return hoveredCellTooltip;
    }

    @Override
    public void render(GuiGraphics graphics, int x, int y, int width, int height, int mouseX, int mouseY, float partialTick) {
        this.rectX = x;
        this.rectY = y;
        this.rectWidth = width;
        this.rectHeight = height;
        cellHits.clear();
        hoveredCellTooltip = null;

        graphics.fill(x, y, x + width, y + height, BACKGROUND_COLOR);

        var topRowY = y + CONTENT_PADDING;
        searchInput.render(graphics, x + CONTENT_PADDING, topRowY, width - 2 * CONTENT_PADDING, mouseX, mouseY);

        var gridX = x + CONTENT_PADDING;
        var gridY = topRowY + TextInput.HEIGHT + 4;
        var gridW = width - 2 * CONTENT_PADDING;
        var gridH = Math.max(0, height - (gridY - y) - CONTENT_PADDING);
        if (gridH <= 0) {
            return;
        }

        var query = searchInput.content().toLowerCase(java.util.Locale.ROOT).trim();
        var entries = filter(query);

        var font = EngineFont.get();
        if (entries.isEmpty()) {
            var msg = ClientFactionDirectoryCache.entries().isEmpty() ? "(no factions)" : "(no matches)";
            graphics.drawString(font, Component.literal(msg), gridX, gridY, EMPTY_TEXT_COLOR, false);
            return;
        }

        var n = entries.size();
        var columnHeaderH = COL_HEADER_HEIGHT;
        var rowsHeight = n * CELL_SIZE;
        // Sticky column header — it stays pinned at the top of the grid during vertical scroll, so the vertical
        // scroll viewport excludes the column-header band and the scroll content is just the rows.
        var rowsAreaY = gridY + columnHeaderH;
        var rowsAreaH = Math.max(0, gridH - columnHeaderH);
        scroll.layout(rowsAreaH, rowsHeight);

        // Horizontal scroll only applies to the cells + column-headers band — the row-header gutter on the left stays
        // fixed so users can identify which faction each row belongs to while panning sideways.
        var cellsAreaX = gridX + ROW_HEADER_WIDTH;
        var cellsAreaW = Math.max(0, gridW - ROW_HEADER_WIDTH);
        scrollX.layout(cellsAreaW, n * CELL_SIZE);
        var sx = (int) scrollX.scrollX();

        applyRawScissor(graphics, gridX, gridY, gridW, gridH);
        try {
            var scrollY = (int) scroll.scrollY();
            // Column header stays at gridY regardless of vertical scroll (the sticky behavior).
            var headerTopY = gridY;
            // Rows scroll vertically below the sticky column header.
            var firstRowY = rowsAreaY - scrollY;

            // Resolve which row/column the cursor is over (for crosshair highlight + tooltip dispatch). -1 means
            // outside the cell grid in that axis. Both axes also gate on the cursor being inside the panel rect at
            // all — without that, a cursor over a neighboring panel below the matrix would still satisfy the col
            // check (mouseX in cells X range) and light up a phantom column highlight.
            var hoveredCol = -1;
            var hoveredRow = -1;
            var insidePanel = mouseX >= x && mouseX < x + width && mouseY >= y && mouseY < y + height;
            if (insidePanel) {
                if (mouseX >= cellsAreaX && mouseX < cellsAreaX + cellsAreaW) {
                    var col = (mouseX - cellsAreaX + sx) / CELL_SIZE;
                    if (col >= 0 && col < n) {
                        hoveredCol = col;
                    }
                }
                // Only count rows whose visible Y falls inside the rows area — the sticky column header above
                // overpaints any rows scrolled into its band, so hovering there shouldn't light up a row.
                if (mouseY >= rowsAreaY && mouseY < gridY + gridH) {
                    var row = (mouseY - firstRowY) / CELL_SIZE;
                    if (row >= 0 && row < n) {
                        hoveredRow = row;
                    }
                }
            }

            // Rows: cells first, row header second so it overpaints any cell bleed into the gutter. Drawn BEFORE the
            // column header so when a row scrolls up into the column-header area, the column header overpaints it
            // (sticky behavior).
            for (var row = 0; row < n; row++) {
                var rowY = firstRowY + row * CELL_SIZE;
                if (rowY + CELL_SIZE < rowsAreaY || rowY > gridY + gridH) {
                    continue;
                }
                var rowEntry = entries.get(row);
                var rowSwatch = (rowEntry.color() & 0xFFFFFF) | 0xFF000000;

                // Cells
                for (var col = 0; col < n; col++) {
                    var cellX = cellsAreaX + col * CELL_SIZE - sx;
                    if (cellX + CELL_SIZE <= cellsAreaX || cellX >= gridX + gridW) {
                        continue;
                    }
                    var colEntry = entries.get(col);
                    var diagonal = row == col;
                    var state = ClientFactionDirectoryCache.relationship(rowEntry.id(), colEntry.id());
                    var bg = diagonal ? HEADER_BG_COLOR : cellBgFor(state);
                    graphics.fill(cellX, rowY, cellX + CELL_SIZE, rowY + CELL_SIZE, bg);
                    if (diagonal) {
                        // Inert diagonal — color stripe so the faction's own color is visible at A,A.
                        graphics.fill(cellX + 4, rowY + 4, cellX + CELL_SIZE - 4, rowY + CELL_SIZE - 4, rowSwatch);
                    }
                    // Crosshair tint on cells in the hovered row/column (but not the hovered cell itself — it gets
                    // the brighter yellow border instead).
                    var hovered = row == hoveredRow && col == hoveredCol;
                    if ((row == hoveredRow || col == hoveredCol) && !hovered) {
                        graphics.fill(cellX, rowY, cellX + CELL_SIZE, rowY + CELL_SIZE, CELL_CROSSHAIR_TINT);
                    }
                    // Grid border first so the hover highlight below sits on top intact (otherwise the bottom/right
                    // grid lines would clip the yellow hover border).
                    graphics.fill(cellX, rowY + CELL_SIZE - 1, cellX + CELL_SIZE, rowY + CELL_SIZE, GRID_BORDER_COLOR);
                    graphics.fill(cellX + CELL_SIZE - 1, rowY, cellX + CELL_SIZE, rowY + CELL_SIZE, GRID_BORDER_COLOR);
                    if (!diagonal) {
                        if (hovered) {
                            // Inset 1px highlight border on all four sides.
                            graphics.fill(cellX, rowY, cellX + CELL_SIZE, rowY + 1, CELL_HOVER_BORDER);
                            graphics.fill(cellX, rowY + CELL_SIZE - 1, cellX + CELL_SIZE, rowY + CELL_SIZE, CELL_HOVER_BORDER);
                            graphics.fill(cellX, rowY, cellX + 1, rowY + CELL_SIZE, CELL_HOVER_BORDER);
                            graphics.fill(cellX + CELL_SIZE - 1, rowY, cellX + CELL_SIZE, rowY + CELL_SIZE, CELL_HOVER_BORDER);
                            hoveredCellTooltip = buildCellTooltip(rowEntry, colEntry, state);
                        }
                        cellHits.add(new CellHit(cellX, rowY, CELL_SIZE, CELL_SIZE, rowEntry.id(), colEntry.id(), state));
                    } else if (hovered) {
                        hoveredCellTooltip = Component.literal(rowEntry.name() + " (self)");
                    }
                }

                // Row header — drawn AFTER cells so it overpaints any cell bleed into the left gutter when sx>0.
                var rowHeaderHovered = row == hoveredRow;
                graphics.fill(
                    gridX,
                    rowY,
                    gridX + ROW_HEADER_WIDTH,
                    rowY + CELL_SIZE,
                    rowHeaderHovered ? HEADER_BG_HOVER_COLOR : HEADER_BG_COLOR
                );
                graphics.fill(gridX + 2, rowY + 2, gridX + 5, rowY + CELL_SIZE - 2, rowSwatch);
                var truncatedRowLabel = font.plainSubstrByWidth(rowEntry.name(), ROW_HEADER_WIDTH - 10);
                graphics.drawString(
                    font,
                    Component.literal(truncatedRowLabel),
                    gridX + 8,
                    rowY + (CELL_SIZE - font.lineHeight + 2) / 2,
                    rowHeaderHovered ? HEADER_TEXT_HOVER_COLOR : HEADER_TEXT_COLOR,
                    false
                );
            }

            // Sticky column header — drawn AFTER all rows so when a row scrolls up into this area, this strip
            // overpaints it. Position is fixed at gridY regardless of vertical scroll. Horizontal scroll still applies
            // to the column entries themselves so they stay aligned with their cells below.
            graphics.fill(gridX, headerTopY, gridX + gridW, headerTopY + columnHeaderH, HEADER_BG_COLOR);
            for (var col = 0; col < n; col++) {
                var entry = entries.get(col);
                var colX = cellsAreaX + col * CELL_SIZE - sx;
                if (colX + CELL_SIZE <= cellsAreaX || colX >= gridX + gridW) {
                    continue;
                }
                var headerHovered = col == hoveredCol;
                if (headerHovered) {
                    graphics.fill(colX, headerTopY, colX + CELL_SIZE, headerTopY + columnHeaderH, HEADER_BG_HOVER_COLOR);
                }
                var swatchColor = (entry.color() & 0xFFFFFF) | 0xFF000000;
                graphics.fill(colX + 2, headerTopY + 2, colX + CELL_SIZE - 2, headerTopY + 5, swatchColor);
                var truncated = font.plainSubstrByWidth(entry.name(), CELL_SIZE - 4);
                graphics.drawString(
                    font,
                    Component.literal(truncated),
                    colX + 2,
                    headerTopY + 8,
                    headerHovered ? HEADER_TEXT_HOVER_COLOR : HEADER_TEXT_COLOR,
                    false
                );
            }
            // Overpaint the row-header gutter slice of the column-header strip — covers any column-header bleed when
            // the grid is horizontally scrolled past 0.
            graphics.fill(gridX, headerTopY, cellsAreaX, headerTopY + columnHeaderH, HEADER_BG_COLOR);
        } finally {
            graphics.flush();
            RenderSystem.disableScissor();
        }
        scroll.renderScrollbar(graphics, gridX, rowsAreaY, gridW, rowsAreaH, mouseX, mouseY);
        scrollX.renderScrollbar(graphics, cellsAreaX, rowsAreaY, cellsAreaW, rowsAreaH, mouseX, mouseY);
    }

    private static Component buildCellTooltip(
        S2CFactionDirectoryPayload.FactionEntry rowEntry,
        S2CFactionDirectoryPayload.FactionEntry colEntry,
        RelationshipState state
    ) {
        var stateLabel = switch (state) {
            case NEUTRAL -> "Neutral";
            case ALLIED -> "Allied";
            case HOSTILE -> "Hostile";
        };
        return Component.literal(rowEntry.name() + " → " + colEntry.name() + "\n" + stateLabel + "\nRight-click to cycle");
    }

    private static int cellBgFor(RelationshipState state) {
        return switch (state) {
            case NEUTRAL -> CELL_NEUTRAL_BG;
            case ALLIED -> CELL_ALLIED_BG;
            case HOSTILE -> CELL_HOSTILE_BG;
        };
    }

    private List<S2CFactionDirectoryPayload.FactionEntry> filter(String query) {
        var all = ClientFactionDirectoryCache.entries();
        if (query.isEmpty()) {
            return all;
        }
        var out = new ArrayList<S2CFactionDirectoryPayload.FactionEntry>();
        for (var entry : all) {
            if (
                entry.name().toLowerCase(java.util.Locale.ROOT).contains(query)
                    || entry.id().toString().toLowerCase(java.util.Locale.ROOT).contains(query)
            ) {
                out.add(entry);
            }
        }
        return out;
    }

    private static RelationshipState nextState(RelationshipState current) {
        return switch (current) {
            case NEUTRAL -> RelationshipState.ALLIED;
            case ALLIED -> RelationshipState.HOSTILE;
            case HOSTILE -> RelationshipState.NEUTRAL;
        };
    }

    @Override
    public boolean mouseClickedCapture(double mouseX, double mouseY, int button) {
        // Scrollbar thumbs claim clicks first so their drag works without being eaten by pan-drag below.
        if (scroll.mouseClicked(mouseX, mouseY, button) || scrollX.mouseClicked(mouseX, mouseY, button)) {
            return true;
        }
        // Left-click anywhere in the grid area starts a pan drag — the workspace captures this panel so subsequent
        // drag / release events route here regardless of cursor position. The search bar is excluded so its
        // text-input clicks fall through to mouseClicked unchanged.
        if (button == 0 && isInGridArea(mouseX, mouseY)) {
            panning = true;
            panAnchorMouseX = mouseX;
            panAnchorMouseY = mouseY;
            panAnchorScrollX = scrollX.scrollX();
            panAnchorScrollY = scroll.scrollY();
            return true;
        }
        return false;
    }

    private boolean isInGridArea(double mouseX, double mouseY) {
        var gridTop = rectY + CONTENT_PADDING + TextInput.HEIGHT + 4;
        return mouseX >= rectX && mouseX < rectX + rectWidth && mouseY >= gridTop && mouseY < rectY + rectHeight;
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (searchInput.mouseClicked(mouseX, mouseY, button)) {
            return true;
        }
        // Right-click on a cell cycles its relationship state. Left-click is reserved for pan-drag, handled in
        // mouseClickedCapture above.
        if (button != 1) {
            return false;
        }
        for (var hit : cellHits) {
            if (mouseX >= hit.x && mouseX < hit.x + hit.w && mouseY >= hit.y && mouseY < hit.y + hit.h) {
                var next = nextState(hit.currentState);
                BLib.MOD.networking()
                    .sendToServer(C2SSetFactionRelationshipPayload.of(hit.factionA, hit.factionB, next));
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        if (scroll.mouseDragged(mouseX, mouseY, button)) {
            return true;
        }
        if (scrollX.mouseDragged(mouseX, mouseY, button)) {
            return true;
        }
        if (panning && button == 0) {
            // Content follows the cursor: dragging right moves content right (scroll offset decreases). Anchor-based
            // math instead of incremental deltas so partial-frame skips don't accumulate drift.
            var targetScrollX = panAnchorScrollX - (float) (mouseX - panAnchorMouseX);
            var targetScrollY = panAnchorScrollY - (float) (mouseY - panAnchorMouseY);
            scrollX.scrollBy(targetScrollX - scrollX.scrollX());
            scroll.scrollBy(targetScrollY - scroll.scrollY());
            return true;
        }
        return false;
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        var consumed = scroll.mouseReleased(mouseX, mouseY, button) | scrollX.mouseReleased(mouseX, mouseY, button);
        if (panning && button == 0) {
            panning = false;
            return true;
        }
        return consumed;
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
        if (mouseX < rectX || mouseX >= rectX + rectWidth || mouseY < rectY || mouseY >= rectY + rectHeight) {
            return false;
        }
        // Horizontal trackpad / explicit X-axis scroll input pans the matrix sideways.
        var horizontal = false;
        if (scrollX != 0) {
            horizontal = this.scrollX.mouseScrolled(scrollX);
        }
        // Shift+wheel converts a vertical wheel tick into horizontal pan — standard convention for tabular UIs.
        if (scrollY != 0 && isShiftHeld()) {
            return this.scrollX.mouseScrolled(scrollY) || horizontal;
        }
        var vertical = scrollY != 0 && scroll.mouseScrolled(scrollY);
        return vertical || horizontal;
    }

    private static boolean isShiftHeld() {
        var window = Minecraft.getInstance().getWindow().getWindow();
        return org.lwjgl.glfw.GLFW.glfwGetKey(window, org.lwjgl.glfw.GLFW.GLFW_KEY_LEFT_SHIFT) == org.lwjgl.glfw.GLFW.GLFW_PRESS
            || org.lwjgl.glfw.GLFW.glfwGetKey(window, org.lwjgl.glfw.GLFW.GLFW_KEY_RIGHT_SHIFT) == org.lwjgl.glfw.GLFW.GLFW_PRESS;
    }

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

    private record CellHit(
        int x,
        int y,
        int w,
        int h,
        ResourceLocation factionA,
        ResourceLocation factionB,
        RelationshipState currentState
    ) {}
}
