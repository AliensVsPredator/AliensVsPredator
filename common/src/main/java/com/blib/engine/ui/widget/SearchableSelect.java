package com.blib.engine.ui.widget;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

import com.blib.engine.ui.EngineFont;
import com.blib.engine.ui.layout.PanelScissor;
import com.blib.engine.ui.layout.UiRect;
import com.blib.engine.ui.layout.UiText;
import com.blib.engine.ui.panel.base.ListKeyboardNavigation;

/**
 * Generic searchable select widget. The inline element is a button-like row showing the current selection's label plus
 * a {@code ▼} indicator; clicking it opens a {@link Popup} overlay with a search input + scrollable filtered list.
 * Generic over the value type so the same widget powers the block picker today and pool / loot-table pickers in future
 * engine work.
 * <p>
 * Single popup at a time across the workspace — held in the static {@link #openPopup} slot, mirroring
 * {@link TextInput#focused}. The {@link com.blib.engine.ui.EngineWorkspaceScreen} reads it each frame for render and
 * event dispatch (see screen integration); the popup nulls it when an item is picked.
 */
@ApiStatus.Internal
public final class SearchableSelect<T> {

    public record Item<T>(
        T value,
        String label
    ) {}

    public static final int HEIGHT = 13;

    private static final int BG_COLOR = 0xFF14141A;

    private static final int BG_HOVER_COLOR = 0xFF1A1A22;

    private static final int BORDER_COLOR = 0xFF353540;

    private static final int BORDER_HOVER_COLOR = 0xFF4F8FFF;

    private static final int TEXT_COLOR = 0xFFD0D0D0;

    private static final int PLACEHOLDER_COLOR = 0xFF606068;

    private static final int ARROW_COLOR = 0xFF808088;

    private static final int PADDING_X = 4;

    private static final String ARROW = "▼";

    private static @Nullable Popup<?> openPopup;

    public static @Nullable Popup<?> getOpenPopup() {
        return openPopup;
    }

    public static void closeOpenPopup() {
        openPopup = null;
    }

    /**
     * Open a popup without an inline {@link SearchableSelect} row — e.g. when surfacing the picker from a menu item.
     * Anchors at {@code (anchorX, anchorY)} with the natural popup width clamped to {@code anchorWidth}; the popup
     * extends downward and flips above when it would overflow the viewport, same as the inline-spawned popups. Creates
     * a transient owner widget so the existing {@link Popup} routing (which writes back to {@code owner.currentValue})
     * keeps working — that field goes nowhere since the owner isn't rendered.
     */
    public static <T> void openPopupAt(
        int anchorX,
        int anchorY,
        int anchorWidth,
        int anchorHeight,
        List<Item<T>> items,
        Function<T, String> displayLabel,
        @Nullable Function<T, ItemStack> iconProvider,
        Consumer<T> onSelect
    ) {
        var transientOwner = new SearchableSelect<T>(() -> items, displayLabel, iconProvider, null, onSelect);
        var popup = new Popup<>(transientOwner, items, displayLabel, iconProvider, null);
        popup.openAt(anchorX, anchorY, anchorWidth, anchorHeight);
        openPopup = popup;
        popup.searchInput.focus();
    }

    private final Supplier<List<Item<T>>> itemsProvider;

    private final Function<T, String> displayLabel;

    /**
     * Optional per-item icon provider. When non-null, the popup renders a 16×16 vanilla item icon left of each row's
     * label (and shifts the text accordingly). Pass {@code null} for text-only selects (e.g. the future pool picker
     * where there's no natural icon for a structure pool id).
     */
    private final @Nullable Function<T, ItemStack> iconProvider;

    /**
     * Optional free-text parser. When non-null, pressing Enter in the popup's search input commits the typed text as
     * the selected value (after parsing). Use this for "select known + type new" combo-box semantics — e.g. the jigsaw
     * inspector's Target / Name fields, where you usually pick from existing matches but occasionally need to
     * forward-reference a piece you haven't built yet. Returns {@code null} from the parser to reject the input (commit
     * is silently dropped). Pure-select widgets (e.g. Pool picker) should pass {@code null}.
     */
    private final @Nullable Function<String, T> freeTextParser;

    private final Consumer<T> onSelect;

    private @Nullable T currentValue;

    private int rectX;

    private int rectY;

    private int rectWidth;

    public SearchableSelect(
        Supplier<List<Item<T>>> itemsProvider,
        Function<T, String> displayLabel,
        @Nullable T initialValue,
        Consumer<T> onSelect
    ) {
        this(itemsProvider, displayLabel, null, null, initialValue, onSelect);
    }

    public SearchableSelect(
        Supplier<List<Item<T>>> itemsProvider,
        Function<T, String> displayLabel,
        @Nullable Function<T, ItemStack> iconProvider,
        @Nullable T initialValue,
        Consumer<T> onSelect
    ) {
        this(itemsProvider, displayLabel, iconProvider, null, initialValue, onSelect);
    }

    public SearchableSelect(
        Supplier<List<Item<T>>> itemsProvider,
        Function<T, String> displayLabel,
        @Nullable Function<T, ItemStack> iconProvider,
        @Nullable Function<String, T> freeTextParser,
        @Nullable T initialValue,
        Consumer<T> onSelect
    ) {
        this.itemsProvider = itemsProvider;
        this.displayLabel = displayLabel;
        this.iconProvider = iconProvider;
        this.freeTextParser = freeTextParser;
        this.currentValue = initialValue;
        this.onSelect = onSelect;
    }

    public @Nullable T currentValue() {
        return currentValue;
    }

    public void setCurrentValue(@Nullable T value) {
        this.currentValue = value;
    }

    public void render(GuiGraphics graphics, int x, int y, int width, int mouseX, int mouseY) {
        this.rectX = x;
        this.rectY = y;
        this.rectWidth = width;
        if (width <= 0) {
            return;
        }

        var hovered = mouseX >= x && mouseX < x + width && mouseY >= y && mouseY < y + HEIGHT;
        var bg = hovered ? BG_HOVER_COLOR : BG_COLOR;
        var border = hovered ? BORDER_HOVER_COLOR : BORDER_COLOR;

        graphics.fill(x, y, x + width, y + HEIGHT, bg);
        graphics.fill(x, y, x + width, y + 1, border);
        graphics.fill(x, y + HEIGHT - 1, x + width, y + HEIGHT, border);
        graphics.fill(x, y, x + 1, y + HEIGHT, border);
        graphics.fill(x + width - 1, y, x + width, y + HEIGHT, border);

        var font = EngineFont.get();
        // +2 compensates for MC font's descender padding so labels visually center; see MenuBarPanel.
        var textY = y + (HEIGHT - font.lineHeight + 2) / 2;

        var labelText = currentValue == null ? "(none)" : displayLabel.apply(currentValue);
        var labelColor = currentValue == null ? PLACEHOLDER_COLOR : TEXT_COLOR;

        // Reserve space for the arrow on the right; truncate the label so it never overlaps the arrow or leaks past
        // the right border. Same plainSubstrByWidth idiom as TextInput's leak fix.
        var arrowWidth = font.width(ARROW);
        var maxLabelWidth = Math.max(0, width - 2 * PADDING_X - arrowWidth - 2);
        var truncated = font.plainSubstrByWidth(labelText, maxLabelWidth);
        graphics.drawString(font, Component.literal(truncated), x + PADDING_X, textY, labelColor, false);
        graphics.drawString(font, Component.literal(ARROW), x + width - PADDING_X - arrowWidth, textY, ARROW_COLOR, false);
    }

    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button != 0) {
            return false;
        }
        if (mouseX < rectX || mouseX >= rectX + rectWidth || mouseY < rectY || mouseY >= rectY + HEIGHT) {
            return false;
        }

        // Toggle: clicking the same widget while its popup is open closes it.
        if (openPopup != null && openPopup.owner == this) {
            openPopup = null;
            return true;
        }

        var newPopup = new Popup<>(this, itemsProvider.get(), displayLabel, iconProvider, freeTextParser);
        newPopup.openAt(rectX, rectY, rectWidth, HEIGHT);
        openPopup = newPopup;
        // Auto-focus the search input so the user can start typing immediately without an extra click.
        newPopup.searchInput.focus();
        return true;
    }

    private void selectItemFromPopup(T value) {
        this.currentValue = value;
        if (onSelect != null) {
            onSelect.accept(value);
        }
        openPopup = null;
        TextInput.clearFocus();
    }

    /**
     * Overlay popup spawned by a {@link SearchableSelect}. Holds the search input, scrollable filtered list, and layout
     * state. Rendered on top of all panels by {@link com.blib.engine.ui.EngineWorkspaceScreen}; click / scroll / key
     * events are routed to it before normal panel dispatch when it's open.
     */
    public static final class Popup<T> {

        private static final int POPUP_WIDTH_MIN = 200;

        private static final int MAX_VISIBLE_ROWS = 8;

        /** Tall enough to fit a 16×16 vanilla item icon with 1px top/bottom padding. */
        private static final int ROW_HEIGHT = 18;

        private static final int ICON_SIZE = 16;

        private static final int ICON_TEXT_GAP = 3;

        private static final int BG_COLOR = 0xF01A1A1F;

        private static final int BORDER_COLOR = 0xFF353540;

        private static final int ROW_HOVER_BG = 0xFF353540;

        private static final int ROW_SELECTED_BG = 0xFF3A3A48;

        private static final int ROW_TEXT_COLOR = 0xFFD0D0D0;

        private static final int EMPTY_NOTE_COLOR = 0xFF606068;

        private static final int PADDING_X = 4;

        private static final int CONTENT_PADDING = 2;

        private static final int SEARCH_BAR_HEIGHT = TextInput.HEIGHT;

        private final SearchableSelect<T> owner;

        private final List<Item<T>> allItems;

        private final Function<T, String> displayLabel;

        private final @Nullable Function<T, ItemStack> iconProvider;

        private final @Nullable Function<String, T> freeTextParser;

        private final TextInput searchInput;

        private final ScrollContainer scroll;

        private List<Item<T>> filteredItems;

        private @Nullable String lastSearchQuery;

        private int highlightedIndex = -1;

        // Layout — set by openAt() and updated each render once the popup height is known.
        private int popupX;

        private int popupY;

        private int popupWidth;

        private int popupHeight;

        private int anchorY;

        private int anchorHeight;

        // Cached row geometry from the last render so click handlers can hit-test without recomputing.
        private int listAreaX;

        private int listAreaY;

        private int listAreaWidth;

        private int listAreaHeight;

        Popup(
            SearchableSelect<T> owner,
            List<Item<T>> items,
            Function<T, String> displayLabel,
            @Nullable Function<T, ItemStack> iconProvider,
            @Nullable Function<String, T> freeTextParser
        ) {
            this.owner = owner;
            this.allItems = items;
            this.displayLabel = displayLabel;
            this.iconProvider = iconProvider;
            this.freeTextParser = freeTextParser;
            // Wire Enter on the search input to commit the typed text as a free-text value when a parser is set.
            // Pure-select widgets pass null and Enter does nothing useful (TextInput just defocuses; the popup
            // re-focuses next frame).
            this.searchInput = new TextInput("Search…", freeTextParser != null ? this::commitFreeText : null);
            this.scroll = new ScrollContainer();
            this.filteredItems = items;
            this.lastSearchQuery = "";
            this.highlightedIndex = items.isEmpty() ? -1 : indexOfCurrentValue();
        }

        /**
         * Free-text commit path — invoked when the user presses Enter in the popup's search input. Parses the typed
         * text via {@link #freeTextParser}; on success, selects it (closes the popup, fires onSelect). Parser returning
         * {@code null} means "invalid input" — we silently no-op rather than committing garbage.
         */
        private void commitFreeText(String text) {
            if (freeTextParser == null) {
                return;
            }
            var parsed = freeTextParser.apply(text);
            if (parsed != null) {
                owner.selectItemFromPopup(parsed);
            }
        }

        void openAt(int anchorX, int anchorY, int anchorWidth, int anchorHeight) {
            this.popupWidth = Math.max(POPUP_WIDTH_MIN, anchorWidth);
            this.popupX = anchorX;
            this.popupY = anchorY + anchorHeight;
            this.anchorY = anchorY;
            this.anchorHeight = anchorHeight;
        }

        public boolean isInside(double x, double y) {
            return x >= popupX && x < popupX + popupWidth && y >= popupY && y < popupY + popupHeight;
        }

        public void render(GuiGraphics graphics, int mouseX, int mouseY, int viewportWidth, int viewportHeight) {
            // Keep the search input focused while the popup is open. The workspace's TextInput.clearFocus on every
            // click (and the input's own Enter→defocus behavior) would otherwise leave the popup with an
            // unfocused-but-visible search box that silently swallows keystrokes.
            if (!searchInput.isFocused()) {
                searchInput.focus();
            }

            // 1) Refresh filter when search content changes.
            var query = searchInput.content().toLowerCase(Locale.ROOT);
            if (!query.equals(lastSearchQuery)) {
                lastSearchQuery = query;
                if (query.isEmpty()) {
                    filteredItems = allItems;
                } else {
                    var filtered = new ArrayList<Item<T>>();
                    for (var item : allItems) {
                        if (item.label().toLowerCase(Locale.ROOT).contains(query)) {
                            filtered.add(item);
                        }
                    }
                    filteredItems = filtered;
                }
                scroll.reset();
                highlightedIndex = filteredItems.isEmpty() ? -1 : Math.max(0, Math.min(indexOfCurrentValue(), filteredItems.size() - 1));
            }

            // 2) Compute popup height from filtered count, then clamp position to viewport.
            var visibleRows = Math.max(1, Math.min(filteredItems.size(), MAX_VISIBLE_ROWS));
            this.listAreaHeight = visibleRows * ROW_HEIGHT;
            this.popupHeight = SEARCH_BAR_HEIGHT + listAreaHeight + 2 * CONTENT_PADDING;

            if (popupX + popupWidth > viewportWidth - 2) {
                popupX = viewportWidth - popupWidth - 2;
            }
            if (popupX < 2) {
                popupX = 2;
            }
            if (popupY + popupHeight > viewportHeight - 2) {
                // Flip above the anchor when the natural below-anchor placement would overflow the bottom.
                popupY = anchorY - popupHeight;
            }
            if (popupY < 2) {
                popupY = 2;
            }

            // 3) Background + border.
            graphics.fill(popupX, popupY, popupX + popupWidth, popupY + popupHeight, BG_COLOR);
            graphics.fill(popupX, popupY, popupX + popupWidth, popupY + 1, BORDER_COLOR);
            graphics.fill(popupX, popupY + popupHeight - 1, popupX + popupWidth, popupY + popupHeight, BORDER_COLOR);
            graphics.fill(popupX, popupY, popupX + 1, popupY + popupHeight, BORDER_COLOR);
            graphics.fill(popupX + popupWidth - 1, popupY, popupX + popupWidth, popupY + popupHeight, BORDER_COLOR);

            // 4) Search bar.
            var searchX = popupX + CONTENT_PADDING;
            var searchY = popupY + CONTENT_PADDING;
            var searchWidth = popupWidth - 2 * CONTENT_PADDING;
            searchInput.render(graphics, searchX, searchY, searchWidth, mouseX, mouseY);

            // 5) List area geometry.
            this.listAreaX = popupX + CONTENT_PADDING;
            this.listAreaY = searchY + SEARCH_BAR_HEIGHT;
            this.listAreaWidth = popupWidth - 2 * CONTENT_PADDING;

            scroll.layout(listAreaHeight, filteredItems.size() * ROW_HEIGHT);

            // 6) Render rows. Empty state shows a muted note when the filter has no matches.
            if (filteredItems.isEmpty()) {
                var font = EngineFont.get();
                var noteY = listAreaY + (listAreaHeight - font.lineHeight + 2) / 2;
                graphics.drawString(font, Component.literal("(no matches)"), listAreaX + PADDING_X, noteY, EMPTY_NOTE_COLOR, false);
                scroll.renderScrollbar(graphics, listAreaX, listAreaY, listAreaWidth, listAreaHeight, mouseX, mouseY);
                return;
            }

            PanelScissor.enable(graphics, UiRect.of(listAreaX, listAreaY, listAreaWidth, listAreaHeight));
            try {
                var font = EngineFont.get();
                var firstVisible = (int) (scroll.scrollY() / ROW_HEIGHT);
                var lastVisible = Math.min(filteredItems.size(), firstVisible + MAX_VISIBLE_ROWS + 2);
                // When an iconProvider is set, reserve ICON_SIZE + ICON_TEXT_GAP at the start of each row for the
                // 16x16 icon. Without an iconProvider, text starts flush with the row's left padding.
                var hasIcons = iconProvider != null;
                var iconX = listAreaX + PADDING_X;
                var rowTextX = hasIcons ? iconX + ICON_SIZE + ICON_TEXT_GAP : listAreaX + PADDING_X;
                var rowTextMaxWidth = listAreaWidth - (rowTextX - listAreaX) - PADDING_X - ScrollContainer.SCROLLBAR_GUTTER;
                for (var i = firstVisible; i < lastVisible; i++) {
                    var rowY = listAreaY + i * ROW_HEIGHT - (int) scroll.scrollY();
                    if (rowY + ROW_HEIGHT < listAreaY) {
                        continue;
                    }
                    if (rowY > listAreaY + listAreaHeight) {
                        break;
                    }
                    var hovered = mouseX >= listAreaX
                        && mouseX < listAreaX + listAreaWidth - ScrollContainer.SCROLLBAR_GUTTER
                        && mouseY >= rowY
                        && mouseY < rowY + ROW_HEIGHT;
                    if (hovered) {
                        graphics.fill(
                            listAreaX,
                            rowY,
                            listAreaX + listAreaWidth - ScrollContainer.SCROLLBAR_GUTTER + 2,
                            rowY + ROW_HEIGHT,
                            ROW_HOVER_BG
                        );
                    } else if (i == highlightedIndex) {
                        graphics.fill(
                            listAreaX,
                            rowY,
                            listAreaX + listAreaWidth - ScrollContainer.SCROLLBAR_GUTTER + 2,
                            rowY + ROW_HEIGHT,
                            ROW_SELECTED_BG
                        );
                    }
                    var item = filteredItems.get(i);
                    if (hasIcons) {
                        var stack = iconProvider.apply(item.value());
                        if (stack != null && !stack.isEmpty()) {
                            // Vertically center the 16x16 icon in the 18px row.
                            graphics.renderItem(stack, iconX, rowY + (ROW_HEIGHT - ICON_SIZE) / 2);
                        }
                    }
                    UiText.drawClipped(
                        graphics,
                        font,
                        item.label(),
                        rowTextX,
                        rowY + (ROW_HEIGHT - font.lineHeight + 2) / 2,
                        rowTextMaxWidth,
                        ROW_TEXT_COLOR
                    );
                }
            } finally {
                PanelScissor.disable(graphics);
            }

            scroll.renderScrollbar(graphics, listAreaX, listAreaY, listAreaWidth, listAreaHeight, mouseX, mouseY);
        }

        public boolean mouseClicked(double mouseX, double mouseY, int button) {
            // Search input first — it owns its own rect-test.
            if (searchInput.mouseClicked(mouseX, mouseY, button)) {
                return true;
            }
            // Scrollbar next so a click on the bar doesn't fall through to the row beneath it.
            if (scroll.mouseClicked(mouseX, mouseY, button)) {
                return true;
            }
            // List rows. Hit-test in the list area (excluding the scrollbar gutter), translate by scrollY to find
            // the actual item index.
            if (
                button == 0
                    && mouseX >= listAreaX
                    && mouseX < listAreaX + listAreaWidth - ScrollContainer.SCROLLBAR_GUTTER
                    && mouseY >= listAreaY
                    && mouseY < listAreaY + listAreaHeight
            ) {
                var localY = mouseY - listAreaY + scroll.scrollY();
                var index = (int) (localY / ROW_HEIGHT);
                if (index >= 0 && index < filteredItems.size()) {
                    owner.selectItemFromPopup(filteredItems.get(index).value());
                    return true;
                }
            }
            return false;
        }

        public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
            var direction = ListKeyboardNavigation.directionForKey(keyCode, modifiers);
            if (direction != 0) {
                moveHighlight(direction);
                return true;
            }
            if (
                modifiers == 0
                    && freeTextParser == null
                    && (keyCode == GLFW.GLFW_KEY_ENTER || keyCode == GLFW.GLFW_KEY_KP_ENTER)
                    && highlightedIndex >= 0
                    && highlightedIndex < filteredItems.size()
            ) {
                owner.selectItemFromPopup(filteredItems.get(highlightedIndex).value());
                return true;
            }
            return false;
        }

        public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
            return scroll.mouseDragged(mouseX, mouseY, button);
        }

        public boolean mouseReleased(double mouseX, double mouseY, int button) {
            return scroll.mouseReleased(mouseX, mouseY, button);
        }

        public boolean mouseScrolled(double mouseX, double mouseY, double scrollDy) {
            // Only consume scroll when cursor is over the popup body — otherwise scroll wheel events outside the
            // popup should still close-and-fall-through (handled by the screen).
            if (!isInside(mouseX, mouseY)) {
                return false;
            }
            return scroll.mouseScrolled(scrollDy);
        }

        private void moveHighlight(int direction) {
            if (filteredItems.isEmpty()) {
                highlightedIndex = -1;
                return;
            }
            highlightedIndex = ListKeyboardNavigation.moveIndex(highlightedIndex, filteredItems.size(), direction);
            ensureHighlightedVisible();
        }

        private void ensureHighlightedVisible() {
            if (highlightedIndex < 0) {
                return;
            }
            ListKeyboardNavigation.scrollRangeIntoView(
                scroll,
                highlightedIndex * ROW_HEIGHT,
                highlightedIndex * ROW_HEIGHT + ROW_HEIGHT,
                listAreaHeight
            );
        }

        private int indexOfCurrentValue() {
            if (owner.currentValue == null) {
                return 0;
            }
            for (var i = 0; i < filteredItems.size(); i++) {
                if (owner.currentValue.equals(filteredItems.get(i).value())) {
                    return i;
                }
            }
            return 0;
        }
    }
}
