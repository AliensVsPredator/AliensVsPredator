package com.blib.engine.ui.popup;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.function.BooleanSupplier;
import java.util.function.Supplier;

import com.blib.engine.ui.EngineFont;
import com.blib.engine.ui.layout.UiRect;
import com.blib.engine.ui.layout.UiText;
import com.blib.engine.ui.widget.TextInput;

/**
 * Searchable checklist overlay for larger context-menu management flows. The context menu stays compact, while this
 * popup handles the "manage all" case for lists that can grow beyond a sensible submenu size.
 */
@ApiStatus.Internal
public final class ChecklistManagePopup {

    public record Entry(
        String label,
        @Nullable Integer swatchColor,
        BooleanSupplier checked,
        Runnable action,
        boolean enabled
    ) {}

    private static final int POPUP_WIDTH = 240;

    private static final int ROW_HEIGHT = 14;

    private static final int HEADER_HEIGHT = 16;

    private static final int MAX_VISIBLE_ROWS = 12;

    private static final int PADDING_X = 8;

    private static final int BORDER_THICKNESS = 1;

    private static final int SEARCH_GAP = 5;

    private static final int ROWS_GAP = 5;

    private static final int CLOSE_SIZE = 12;

    private static final int CHECK_WIDTH = 12;

    private static final int SWATCH_SIZE = 8;

    private static final int SWATCH_GAP = 6;

    private static final int SCROLLBAR_WIDTH = 6;

    private static final int MIN_THUMB_HEIGHT = 10;

    private static final int POPUP_Z = 400;

    private static final int BG_COLOR = 0xF01A1A1F;

    private static final int BORDER_COLOR = 0xFF353540;

    private static final int HEADER_BG_COLOR = 0xFF252530;

    private static final int ROW_HOVER_BG_COLOR = 0xFF353540;

    private static final int TEXT_COLOR = 0xFFD0D0D0;

    private static final int MUTED_TEXT_COLOR = 0xFF808088;

    private static final int CHECK_COLOR = 0xFFE6C26B;

    private static final int SCROLLBAR_TRACK_COLOR = 0xFF14141A;

    private static final int SCROLLBAR_THUMB_COLOR = 0xFF3D3D45;

    private static final int SCROLLBAR_THUMB_HOVER_COLOR = 0xFF565660;

    private static @Nullable ChecklistManagePopup openPopup;

    private final int anchorX;

    private final int anchorY;

    private final String title;

    private final Supplier<List<Entry>> entriesProvider;

    private final TextInput searchInput = new TextInput("Search...");

    private final List<RowHit> rowHits = new ArrayList<>();

    private boolean positionResolved;

    private int popupX;

    private int popupY;

    private int scrollOffset;

    private @Nullable UiRect closeRect;

    private @Nullable UiRect scrollbarRect;

    private @Nullable UiRect scrollbarThumbRect;

    private boolean draggingScrollbar;

    private int dragStartMouseY;

    private int dragStartScrollOffset;

    private ChecklistManagePopup(int anchorX, int anchorY, String title, Supplier<List<Entry>> entriesProvider) {
        this.anchorX = anchorX;
        this.anchorY = anchorY;
        this.title = title;
        this.entriesProvider = entriesProvider;
    }

    public static void openAt(int anchorX, int anchorY, String title, Supplier<List<Entry>> entriesProvider) {
        openPopup = new ChecklistManagePopup(anchorX, anchorY, title, entriesProvider);
        openPopup.searchInput.focus();
    }

    public static @Nullable ChecklistManagePopup getOpenPopup() {
        return openPopup;
    }

    public static void closeOpenPopup() {
        openPopup = null;
        TextInput.clearFocus();
    }

    public boolean isInside(double mouseX, double mouseY) {
        if (!positionResolved) {
            return false;
        }
        var filtered = filteredEntries();
        return mouseX >= popupX
            && mouseX < popupX + POPUP_WIDTH
            && mouseY >= popupY
            && mouseY < popupY + contentHeight(filtered.size());
    }

    public void render(GuiGraphics graphics, int mouseX, int mouseY, int logicalWidth, int logicalHeight) {
        var filtered = filteredEntries();
        clampScroll(filtered.size());
        if (!positionResolved) {
            popupX = Math.max(4, Math.min(logicalWidth - POPUP_WIDTH - 4, anchorX));
            var height = contentHeight(filtered.size());
            popupY = Math.max(4, Math.min(logicalHeight - height - 4, anchorY));
            positionResolved = true;
        }

        var pose = graphics.pose();
        pose.pushPose();
        pose.translate(0.0F, 0.0F, POPUP_Z);
        try {
            renderAtOverlayDepth(graphics, filtered, mouseX, mouseY);
        } finally {
            pose.popPose();
        }
    }

    private void renderAtOverlayDepth(GuiGraphics graphics, List<Entry> filtered, int mouseX, int mouseY) {
        rowHits.clear();
        scrollbarRect = null;
        scrollbarThumbRect = null;
        var height = contentHeight(filtered.size());
        graphics.fill(popupX, popupY, popupX + POPUP_WIDTH, popupY + height, BG_COLOR);
        graphics.fill(popupX, popupY, popupX + POPUP_WIDTH, popupY + BORDER_THICKNESS, BORDER_COLOR);
        graphics.fill(popupX, popupY + height - BORDER_THICKNESS, popupX + POPUP_WIDTH, popupY + height, BORDER_COLOR);
        graphics.fill(popupX, popupY, popupX + BORDER_THICKNESS, popupY + height, BORDER_COLOR);
        graphics.fill(popupX + POPUP_WIDTH - BORDER_THICKNESS, popupY, popupX + POPUP_WIDTH, popupY + height, BORDER_COLOR);

        var font = EngineFont.get();
        var headerY = popupY + BORDER_THICKNESS;
        graphics.fill(
            popupX + BORDER_THICKNESS,
            headerY,
            popupX + POPUP_WIDTH - BORDER_THICKNESS,
            headerY + HEADER_HEIGHT,
            HEADER_BG_COLOR
        );
        closeRect = UiRect.of(popupX + POPUP_WIDTH - PADDING_X - CLOSE_SIZE, headerY + 2, CLOSE_SIZE, CLOSE_SIZE);
        UiText.drawClipped(
            graphics,
            font,
            title,
            popupX + PADDING_X,
            headerY + (HEADER_HEIGHT - font.lineHeight + 2) / 2,
            Math.max(0, closeRect.x() - popupX - 2 * PADDING_X),
            TEXT_COLOR
        );
        var closeHovered = closeRect.contains(mouseX, mouseY);
        graphics.drawString(
            font,
            Component.literal("x"),
            closeRect.x() + (CLOSE_SIZE - font.width("x")) / 2,
            closeRect.y() + (CLOSE_SIZE - font.lineHeight + 2) / 2,
            closeHovered ? TEXT_COLOR : MUTED_TEXT_COLOR,
            false
        );

        var searchY = headerY + HEADER_HEIGHT + SEARCH_GAP;
        searchInput.render(graphics, popupX + PADDING_X, searchY, POPUP_WIDTH - 2 * PADDING_X, mouseX, mouseY);
        renderRows(graphics, filtered, searchY + TextInput.HEIGHT + ROWS_GAP, mouseX, mouseY);
    }

    private void renderRows(GuiGraphics graphics, List<Entry> filtered, int rowsTop, int mouseX, int mouseY) {
        var font = EngineFont.get();
        if (filtered.isEmpty()) {
            graphics.drawString(
                font,
                Component.literal(searchInput.content().isBlank() ? "(no entries)" : "(no matches)"),
                popupX + PADDING_X,
                rowsTop + (ROW_HEIGHT - font.lineHeight + 2) / 2,
                MUTED_TEXT_COLOR,
                false
            );
            return;
        }

        var visible = Math.min(MAX_VISIBLE_ROWS, filtered.size());
        var scrollable = filtered.size() > MAX_VISIBLE_ROWS;
        var rowsRight = popupX + POPUP_WIDTH - BORDER_THICKNESS - (scrollable ? SCROLLBAR_WIDTH : 0);
        for (var i = 0; i < visible; i++) {
            var globalIdx = i + scrollOffset;
            if (globalIdx >= filtered.size()) {
                break;
            }
            var entry = filtered.get(globalIdx);
            var rowY = rowsTop + i * ROW_HEIGHT;
            var hovered = entry.enabled()
                && mouseX >= popupX + BORDER_THICKNESS
                && mouseX < rowsRight
                && mouseY >= rowY
                && mouseY < rowY + ROW_HEIGHT;
            if (hovered) {
                graphics.fill(
                    popupX + BORDER_THICKNESS,
                    rowY,
                    rowsRight,
                    rowY + ROW_HEIGHT,
                    ROW_HOVER_BG_COLOR
                );
            }

            var textY = rowY + (ROW_HEIGHT - font.lineHeight + 2) / 2;
            var labelX = popupX + PADDING_X + CHECK_WIDTH;
            if (entry.checked().getAsBoolean()) {
                graphics.drawString(font, Component.literal("✓"), popupX + PADDING_X, textY, CHECK_COLOR, false);
            }
            if (entry.swatchColor() != null) {
                var swatchX = labelX;
                var swatchY = rowY + (ROW_HEIGHT - SWATCH_SIZE) / 2;
                graphics.fill(swatchX, swatchY, swatchX + SWATCH_SIZE, swatchY + SWATCH_SIZE, entry.swatchColor() | 0xFF000000);
                labelX += SWATCH_SIZE + SWATCH_GAP;
            }

            UiText.drawClipped(
                graphics,
                font,
                entry.label(),
                labelX,
                textY,
                Math.max(0, rowsRight - PADDING_X - labelX),
                entry.enabled() ? TEXT_COLOR : MUTED_TEXT_COLOR
            );
            rowHits.add(new RowHit(popupX + BORDER_THICKNESS, rowY, rowsRight - popupX - BORDER_THICKNESS, ROW_HEIGHT, entry));
        }
        if (scrollable) {
            renderScrollbar(graphics, filtered.size(), visible, rowsTop, mouseX, mouseY);
        }
    }

    private void renderScrollbar(GuiGraphics graphics, int entryCount, int visibleRows, int rowsTop, int mouseX, int mouseY) {
        var trackX = popupX + POPUP_WIDTH - BORDER_THICKNESS - SCROLLBAR_WIDTH;
        var trackH = visibleRows * ROW_HEIGHT;
        scrollbarRect = UiRect.of(trackX, rowsTop, SCROLLBAR_WIDTH, trackH);
        graphics.fill(trackX, rowsTop, trackX + SCROLLBAR_WIDTH, rowsTop + trackH, SCROLLBAR_TRACK_COLOR);

        var thumbH = Math.max(MIN_THUMB_HEIGHT, trackH * visibleRows / entryCount);
        var maxOffset = Math.max(1, entryCount - visibleRows);
        var thumbTravel = Math.max(0, trackH - thumbH);
        var thumbY = rowsTop + (thumbTravel * scrollOffset / maxOffset);
        scrollbarThumbRect = UiRect.of(trackX, thumbY, SCROLLBAR_WIDTH, thumbH);
        var hovered = scrollbarThumbRect.contains(mouseX, mouseY) || draggingScrollbar;
        graphics.fill(
            trackX + 1,
            thumbY,
            trackX + SCROLLBAR_WIDTH - 1,
            thumbY + thumbH,
            hovered ? SCROLLBAR_THUMB_HOVER_COLOR : SCROLLBAR_THUMB_COLOR
        );
    }

    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button != 0) {
            return false;
        }
        if (closeRect != null && closeRect.contains(mouseX, mouseY)) {
            closeOpenPopup();
            return true;
        }
        if (searchInput.mouseClicked(mouseX, mouseY, button)) {
            return true;
        }
        if (scrollbarRect != null && scrollbarRect.contains(mouseX, mouseY)) {
            updateScrollFromMouse(mouseY);
            draggingScrollbar = true;
            dragStartMouseY = (int) mouseY;
            dragStartScrollOffset = scrollOffset;
            return true;
        }
        for (var hit : rowHits) {
            if (hit.contains(mouseX, mouseY)) {
                if (hit.entry().enabled()) {
                    hit.entry().action().run();
                }
                return true;
            }
        }
        return isInside(mouseX, mouseY);
    }

    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        if (!draggingScrollbar || button != 0) {
            return false;
        }
        updateScrollFromDrag(mouseY);
        return true;
    }

    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        if (!draggingScrollbar) {
            return false;
        }
        draggingScrollbar = false;
        return true;
    }

    public boolean mouseScrolled(double mouseX, double mouseY, double scrollY) {
        if (!isInside(mouseX, mouseY)) {
            return false;
        }
        var filtered = filteredEntries();
        if (filtered.size() <= MAX_VISIBLE_ROWS) {
            return true;
        }
        var max = filtered.size() - MAX_VISIBLE_ROWS;
        scrollOffset = Math.max(0, Math.min(max, scrollOffset - (int) Math.signum(scrollY)));
        return true;
    }

    private void updateScrollFromMouse(double mouseY) {
        var filtered = filteredEntries();
        var visibleRows = Math.min(MAX_VISIBLE_ROWS, filtered.size());
        var scrollbar = scrollbarRect;
        var thumb = scrollbarThumbRect;
        if (scrollbar == null || thumb == null || visibleRows <= 0 || filtered.size() <= visibleRows) {
            return;
        }
        var thumbCenter = thumb.height() / 2;
        setScrollFromThumbTop((int) mouseY - thumbCenter, filtered.size(), visibleRows, scrollbar);
    }

    private void updateScrollFromDrag(double mouseY) {
        var filtered = filteredEntries();
        var visibleRows = Math.min(MAX_VISIBLE_ROWS, filtered.size());
        var scrollbar = scrollbarRect;
        var thumb = scrollbarThumbRect;
        if (scrollbar == null || thumb == null || visibleRows <= 0 || filtered.size() <= visibleRows) {
            return;
        }
        var delta = (int) mouseY - dragStartMouseY;
        var maxOffset = filtered.size() - visibleRows;
        var travel = Math.max(1, scrollbar.height() - thumb.height());
        scrollOffset = Math.max(0, Math.min(maxOffset, dragStartScrollOffset + Math.round((float) delta * maxOffset / travel)));
    }

    private void setScrollFromThumbTop(int thumbTop, int entryCount, int visibleRows, UiRect scrollbar) {
        var thumb = scrollbarThumbRect;
        if (thumb == null) {
            return;
        }
        var maxOffset = entryCount - visibleRows;
        var travel = Math.max(1, scrollbar.height() - thumb.height());
        var clampedTop = Math.max(scrollbar.y(), Math.min(scrollbar.bottom() - thumb.height(), thumbTop));
        scrollOffset = Math.max(0, Math.min(maxOffset, Math.round((float) (clampedTop - scrollbar.y()) * maxOffset / travel)));
    }

    private List<Entry> filteredEntries() {
        var entries = entriesProvider.get();
        var query = searchInput.content().toLowerCase(Locale.ROOT).trim();
        if (query.isEmpty()) {
            return List.copyOf(entries);
        }
        var filtered = new ArrayList<Entry>();
        for (var entry : entries) {
            if (entry.label().toLowerCase(Locale.ROOT).contains(query)) {
                filtered.add(entry);
            }
        }
        return filtered;
    }

    private void clampScroll(int filteredSize) {
        var max = Math.max(0, filteredSize - MAX_VISIBLE_ROWS);
        scrollOffset = Math.max(0, Math.min(max, scrollOffset));
    }

    private static int contentHeight(int filteredSize) {
        var visibleRows = Math.max(1, Math.min(MAX_VISIBLE_ROWS, filteredSize));
        return 2 * BORDER_THICKNESS + HEADER_HEIGHT + SEARCH_GAP + TextInput.HEIGHT + ROWS_GAP + visibleRows * ROW_HEIGHT;
    }

    private record RowHit(
        int x,
        int y,
        int w,
        int h,
        Entry entry
    ) {

        boolean contains(double mouseX, double mouseY) {
            return mouseX >= x && mouseX < x + w && mouseY >= y && mouseY < y + h;
        }
    }
}
